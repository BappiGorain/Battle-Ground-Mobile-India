package com.bgmi.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bgmi.entities.Player;
import com.bgmi.service.JwtService;
import com.bgmi.serviceImpl.PlayerServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bgmi")
public class PlayerController {

    @Autowired
    private PlayerServiceImpl playerServiceImpl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("profilePhoto");
    }

    // REGISTRATION METHODS
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("player", new Player());
        logger.info("Register page loaded");
        return "register";
    }

    @PostMapping("/add")
    public String addPlayer(@Valid @ModelAttribute Player player, BindingResult bindingResult, Model model,
                            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            Player savedPlayer = this.playerServiceImpl.addPlayer(player);
            logger.info("New player " + savedPlayer.getName() + " has been added with ID: " + savedPlayer.getGameId());
            redirectAttributes.addFlashAttribute("player", savedPlayer);
            return "redirect:/bgmi/player/profile/" + savedPlayer.getGameId();
        } catch (Exception e) {
            logger.error("Registration failed: " + e.getMessage(), e);
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    // LOGIN METHODS
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/process-login")
    public String processLogin(@RequestParam("inputId") String inputId,
                             @RequestParam("inputPassword") String inputPassword,
                             Model model, HttpSession session,
                             HttpServletRequest request, HttpServletResponse response) {

        logger.info("Processing login for: " + inputId);

        if (inputId == null || inputId.trim().isEmpty() ||
            inputPassword == null || inputPassword.trim().isEmpty()) {
            model.addAttribute("failedToLoginMessage", "Please enter both GameId and Password");
            return "login";
        }

        Optional<Player> optPlayer = this.playerServiceImpl.getOptionalPlayer(inputId);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            
            try {
                boolean passwordMatches = passwordEncoder.matches(inputPassword, player.getPassword());
                if (!passwordMatches) {
                    model.addAttribute("failedToLoginMessage", "Invalid GameId or Password");
                    return "login";
                }

                Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(inputId, inputPassword));
                    
                if (authentication.isAuthenticated()) {
                    // CRITICAL FIX: Manually save SecurityContext to session
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    context.setAuthentication(authentication);
                    SecurityContextHolder.setContext(context);
                    
                    // Save to HTTP session explicitly
                    HttpSessionSecurityContextRepository repo = new HttpSessionSecurityContextRepository();
                    repo.saveContext(context, request, response);
                    
                    // Your existing session attributes
                    String token = jwtService.generateToken(player.getGameId());
                    session.setAttribute("jwtToken", token);
                    session.setAttribute("authenticatedUser", player.getGameId());
                    session.setAttribute("isAuthenticated", true);
                    
                    logger.info("Authentication successful and SecurityContext saved");
                    return "redirect:/bgmi/player/profile/" + player.getGameId();
                }
            } catch (BadCredentialsException e) {
                logger.error("Invalid credentials for: " + inputId);
                model.addAttribute("failedToLoginMessage", "Invalid GameId or Password");
                return "login";
            } catch (Exception e) {
                logger.error("Authentication error: " + e.getMessage(), e);
                model.addAttribute("failedToLoginMessage", "Authentication failed. Please try again.");
                return "login";
            }
        } else {
            logger.warn("Player not found: " + inputId);
            model.addAttribute("failedToLoginMessage", "Player not found");
            return "login";
        }
        
        model.addAttribute("failedToLoginMessage", "Authentication failed");
        return "login";
    }

    // PROFILE METHODS
    @GetMapping("/player/profile/{gameId}")
    public String showProfile(@PathVariable String gameId, Model model, HttpSession session) {
        // Check if user is authenticated via session
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String authenticatedUser = (String) session.getAttribute("authenticatedUser");
        
        if (isAuthenticated == null || !isAuthenticated || !gameId.equals(authenticatedUser)) {
            return "redirect:/bgmi/login";
        }

        Optional<Player> optPlayer = this.playerServiceImpl.getOptionalPlayer(gameId);
        if (optPlayer.isPresent()) {
            model.addAttribute("player", optPlayer.get());
            
            // Add room info from session if available
            String roomId = (String) session.getAttribute("roomId");
            String roomPassword = (String) session.getAttribute("roomPassword");
            model.addAttribute("roomId", roomId);
            model.addAttribute("roomPassword", roomPassword);
            
            return "player/profile";
        } else {
            return "redirect:/bgmi/login";
        }
    }

    // Alternative profile route (for backward compatibility)
    @GetMapping("/{gameId}")
    public String getSinglePlayer(@PathVariable("gameId") String gameId, HttpSession session, Model model) {
        Optional<Player> optPlayer = this.playerServiceImpl.getOptionalPlayer(gameId);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            model.addAttribute("player", player);
            
            String roomId = (String) session.getAttribute("roomId");
            String roomPassword = (String) session.getAttribute("roomPassword");
            model.addAttribute("roomId", roomId);
            model.addAttribute("roomPassword", roomPassword);
            
            logger.info("Your player is: " + player.getName());
            return "player/profile";
        }
        return "redirect:/bgmi/login";
    }

    // UPDATE METHODS
    @GetMapping("/update/{gameId}")
    public String editPlayer(@PathVariable("gameId") String gameId, Model model, HttpSession session) {
        // Check if user is authenticated via session
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String authenticatedUser = (String) session.getAttribute("authenticatedUser");
        
        if (isAuthenticated == null || !isAuthenticated || !gameId.equals(authenticatedUser)) {
            return "redirect:/bgmi/login";
        }

        Player player = playerServiceImpl.getPlayer(gameId);
        model.addAttribute("player", player);
        return "player/updatePlayer";
    }

    @PostMapping("/update/{gameId}")
    public String updatePlayer(@PathVariable("gameId") String gameId,
                              @ModelAttribute Player player,
                              @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        
        // Check authentication
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String authenticatedUser = (String) session.getAttribute("authenticatedUser");
        
        if (isAuthenticated == null || !isAuthenticated || !gameId.equals(authenticatedUser)) {
            return "redirect:/bgmi/login";
        }
        
        try {
            if (profilePhoto != null && !profilePhoto.isEmpty()) {
                // Create unique filename
                String originalFileName = profilePhoto.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String uniqueFileName = gameId + "_profile" + fileExtension;
                
                // Use static directory for web access
                String uploadDir = "src/main/resources/static/images/profiles/";
                
                // Create directory if it doesn't exist
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Save file
                Path filePath = uploadPath.resolve(uniqueFileName);
                Files.copy(profilePhoto.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // Set relative path for database (accessible via web)
                player.setPhotoPath("/images/profiles/" + uniqueFileName);
                logger.info("Profile photo uploaded: " + uniqueFileName);
            }
            
            // Update player
            playerServiceImpl.updatePlayer(gameId, player);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/bgmi/player/profile/" + gameId;
            
        } catch (Exception e) {
            logger.error("Error updating player: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            return "redirect:/bgmi/update/" + gameId;
        }
    }

    // DELETE METHOD
    @PostMapping("/delete/{gameId}")
    public String deletePlayer(@PathVariable("gameId") String gameId, RedirectAttributes redirectAttributes) {
        this.playerServiceImpl.deletePlayer(gameId);
        redirectAttributes.addFlashAttribute("deleteMessage",
                "Player with Game ID: " + gameId + " has been deleted successfully!");
        logger.info("Player deleted with id: " + gameId);
        return "redirect:/bgmi/all";
    }

    // LOGOUT METHOD
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        logger.info("User logged out successfully");
        return "redirect:/bgmi/login?logout=true";
    }

    // ADMIN/UTILITY METHODS
    @GetMapping("/all")
    public String getAllPlayers(Model model) {
        List<Player> allPlayers = this.playerServiceImpl.getAllPlayer();
        model.addAttribute("players", allPlayers);
        logger.info("All players fetched");
        return "player/allPlayers";
    }

    @GetMapping("/events")
    public String upcomingEvents() {
        logger.info("Events page loaded");
        return "events";
    }

    // ROOM ID/PASSWORD METHODS
    @GetMapping("/id-password")
    public String getIdPassword() {
        return "player/idPassword";
    }

    @PostMapping("/get-id-password")
    public String setIdPassword(@RequestParam("roomId") String roomId,
                               @RequestParam("roomPassword") String roomPassword,
                               HttpSession session,
                               Model model, RedirectAttributes redirectAttributes) {
        
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomPassword", roomPassword);
        session.setAttribute("roomId", roomId);
        session.setAttribute("roomPassword", roomPassword);
        
        logger.info("Room ID: " + roomId + ", Room Password: " + roomPassword);
        return "player/idPassword";
    }

    // TEST METHOD (for debugging passwords)
    @GetMapping("/test-password")
    @ResponseBody
    public String testPassword(@RequestParam String password) {
        String storedHash = "$2a$12$6beOKucVNb4FEd2IP0KE2.xZccAnYLHqGPFIYHz2J.1sxSm4jnNoW";
        boolean matches = passwordEncoder.matches(password, storedHash);
        logger.info("Password test - Raw: " + password + ", Matches: " + matches);
        return "Password matches: " + matches;
    }
}
