package com.bgmi.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bgmi.entities.Player;
import com.bgmi.serviceImpl.PlayerServiceImpl;



@Controller
@RequestMapping("/bgmi") 
public class PlayerController
{

    @Autowired
    private PlayerServiceImpl playerServiceImpl;

    Logger logger = LoggerFactory.getLogger(PlayerController.class);


    @GetMapping("/login")
    public String login(Model model)
    {
        model.addAttribute("player", new Player());
        System.out.println("Player Page");
        return "login";
    }
    
    
    @PostMapping("/add")
    public String addPlayer(@ModelAttribute Player player,RedirectAttributes redirectAttributes)
    {   

        Player savedPlayer = this.playerServiceImpl.addPlayer(player);

        logger.info("New player " + savedPlayer.getName() + " has been added with ID: " + savedPlayer.getGameId());

        // Send player object to profile page
        redirectAttributes.addFlashAttribute("player", savedPlayer);

        return "redirect:/bgmi/profile";
    }


    @GetMapping("/profile")
    public String profile(@ModelAttribute("player") Player player, Model model)
    {
        model.addAttribute("player", player);
        System.out.println("Profile Page");        
        return "player/profile";
    }

    @GetMapping("/{id}")
    public String getSinglePlayer(@PathVariable("id") String id)
    {
        Player player = this.playerServiceImpl.getSinglePlayer(id);

        logger.info("your player is : " + player.getName());

        return "player";
    }


    @GetMapping("/all")
    public String getallPlayers(Model model)
    {
        List<Player> allPlayers = this.playerServiceImpl.getAllPlayer();
        model.addAttribute("players", allPlayers);

        logger.info("All players fetched");

        return "player/allPlayers";
    }


    @GetMapping("/edit/{id}")
    public String updatePlayer(@PathVariable("id") String id, Model model)
    {
        Player player = this.playerServiceImpl.getSinglePlayer(id);
        model.addAttribute("player", player);

        return "/bgmi/updatePlayer";
    }

    @PostMapping("/update/{id}")
    public String updatedPlayer(@ModelAttribute Player player, @PathVariable("id") String id,RedirectAttributes redirectAttributes)
    {
        Player updatedPlayerDeails = this.playerServiceImpl.updatePlayer(id, player);
        logger.info("Player updated with id : " + updatedPlayerDeails.getGameId());

        return "redirect:/bgmi/profile";
    }
    


    @DeleteMapping("/{id}")
    public String deletePlayer(@PathVariable("id") String id)
    {
        this.playerServiceImpl.deletePlayer(id);
        logger.info("Player deleted with id : " + id);
        return "player";
    }

}
