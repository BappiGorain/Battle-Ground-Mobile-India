package com.bgmi.controller;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bgmi.entities.Player;
import com.bgmi.serviceImpl.PlayerServiceImpl;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/bgmi") 
public class PlayerController
{

    @Autowired
    private PlayerServiceImpl playerServiceImpl;

    Logger logger = LoggerFactory.getLogger(PlayerController.class);

     @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("profilePhoto");
    }


    @GetMapping("/register")
    public String register(Model model)
    {
        model.addAttribute("player", new Player());
        System.out.println("Register page loaded");
        return "register";
    }
    
    
    @PostMapping("/add")
    public String addPlayer(@Valid @ModelAttribute Player player,BindingResult bindingResult,RedirectAttributes redirectAttributes)
    {   

        if(bindingResult.hasErrors())
        {
            return "register";
        }
        
        
        Player savedPlayer = this.playerServiceImpl.addPlayer(player);

        logger.info("New player " + savedPlayer.getName() + " has been added with ID: " + savedPlayer.getGameId());

        // Send player object to profile page
        redirectAttributes.addFlashAttribute("player", savedPlayer);

        return "redirect:/bgmi/player/profile/"+ savedPlayer.getGameId();
    }


    // Login

    @GetMapping("/login")
    public String login()
    {
        return "login";
    }   
    
    

    @GetMapping("/player/profile/{gameId}")
    public String showProfile(@PathVariable String gameId, Model model) {
        Player player = this.playerServiceImpl.getSinglePlayer(gameId);
        model.addAttribute("player", player);
        return "player/profile"; // profile.html
    }



    


    @GetMapping("/all")
    public String getallPlayers(Model model)
    {
        List<Player> allPlayers = this.playerServiceImpl.getAllPlayer();
        model.addAttribute("players", allPlayers);

        logger.info("All players fetched");

        return "player/allPlayers";
    }


    @GetMapping("/update/{gameId}")
    public String editPlayer(@PathVariable("gameId") String gameId, Model model) {
        
        Player player = playerServiceImpl.getSinglePlayer(gameId);
        model.addAttribute("player", player);
        return "player/updatePlayer"; // updatePlayer.html
    }

    @PostMapping("/update/{gameId}")
    public String updatedPlayer(@PathVariable("gameId") String gameId,
            @ModelAttribute Player player,
            @RequestParam(value = "profilePhoto", required = false) MultipartFile profilePhoto,
            RedirectAttributes redirectAttributes) {
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
            return "redirect:/bgmi/" + gameId; // Redirect to profile view

        } catch (Exception e) {
            logger.error("Error updating player: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
            return "redirect:/bgmi/update/" + gameId;
        }
    }


    @PostMapping("/delete/{gameId}")
    public String deletePlayer(@PathVariable("gameId") String gameId,RedirectAttributes redirectAttributes)
    {
        this.playerServiceImpl.deletePlayer(gameId);
        redirectAttributes.addFlashAttribute("deleteMessage","Player with Game ID : " + gameId +" has been deleted Successfully !!");
        logger.info("Player deleted with id : " + gameId);
        return "redirect:/bgmi/all";
    }


    @GetMapping("/id-password")
    public String getIdPassword()
    {
        return "player/idPassword";
    }

    @PostMapping("/get-id-password")
    public String getIdPassword(@RequestParam("roomId") String roomId,
                                @RequestParam("roomPassword") String roomPassword,
                                HttpSession session,
                                Model model,RedirectAttributes redirectAttributes)
    {
        model.addAttribute("roomId", roomId);
        model.addAttribute("roomPassword", roomPassword);

        session.setAttribute("roomId", roomId);
        session.setAttribute("roomPassword", roomPassword);
        logger.info("Room ID: " + roomId + ", Room Password: " + roomPassword);
        // redirectAttributes.addFlashAttribute("successMessage", "Room ID and Password Sent successfully!");
        return "player/idPassword";
    }


    @GetMapping("/{gameId}")
    public String getSinglePlayer(@PathVariable("gameId") String gameId,HttpSession session,Model model)
    {
        Player player = this.playerServiceImpl.getSinglePlayer(gameId);
        model.addAttribute("player", player);

        String roomId = (String)session.getAttribute("roomId");
        String roomPassword = (String)session.getAttribute("roomPassword");

        model.addAttribute("roomId", roomId);
        model.addAttribute("roomPassword", roomPassword);

        logger.info("your player is : " + player.getName());

        return "player/profile";
    }

    @GetMapping("/events")
    public String upCommingEventsString() {
        System.out.println("Events page loaded");
        return "events";
    }
    

}
