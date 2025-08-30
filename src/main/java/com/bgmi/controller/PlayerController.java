package com.bgmi.controller;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bgmi.entities.Player;
import com.bgmi.serviceImpl.PlayerServiceImpl;



@Controller
@RequestMapping("/player") 
public class PlayerController
{

    @Autowired
    private PlayerServiceImpl playerServiceImpl;

    Logger logger = LoggerFactory.getLogger(PlayerController.class);


    @GetMapping("/player")
    public String testing(@ModelAttribute Player player,Model model)
    {

       model.addAttribute("player",new Player());

        System.out.println("Player Page");
        return "player";
    }
    
    
    @PostMapping("/add")
    public String addPlayer(@ModelAttribute Player player,Model model)
    {

        

        System.out.println("Received player data:");
        System.out.println("Name: " + player.getName());
        System.out.println("Game ID: " + player.getGameId());
        System.out.println("Email: " + player.getEmail());
        System.out.println("Password: " + player.getPassword());
        System.out.println("Phone: " + player.getPhoneNumber());

        Player savedPlayer = this.playerServiceImpl.addPlayer(player);
        

        logger.info("New player " + savedPlayer.getName() + " has been added with ID: " + savedPlayer.getGameId());

            model.addAttribute("successMessage", "Player " + savedPlayer.getName() + " added successfully with ID: " + savedPlayer.getGameId());

        return "player";
    }

    @GetMapping("/{id}")
    public String getSinglePlayer(@PathVariable("id") String id)
    {
        Player player = this.playerServiceImpl.getSinglePlayer(id);

        logger.info("your player is : " + player.getName());

        return "player";
    }


    @GetMapping("/all")
    public String getallPlayers()
    {

        this.playerServiceImpl.getAllPlayer();

        logger.info("All players fetched");

        return "player";
    }


    @PutMapping("/update/{id}")
    public String updatePlayer(@RequestBody Player player,@PathVariable("id") String id)
    {

        this.playerServiceImpl.updatePlayer(id,player);

        return "player";
    }


    @DeleteMapping("/{id}")
    public String deletePlayer(@PathVariable("id") String id)
    {
        this.playerServiceImpl.deletePlayer(id);
        logger.info("Player deleted with id : " + id);
        return "player";
    }

}
