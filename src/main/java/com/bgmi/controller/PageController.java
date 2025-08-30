package com.bgmi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.bgmi.entities.Player;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class PageController 
{

    @GetMapping("/greeting")
    public String helloPage(Model model)
    {

        Player player = new Player();
        player.setName("Bappi");
        player.setEmail("bappi123@gmail.com");
        player.setGameId("123456");
        player.setPassword("123456");
        player.setPhoneNumber("1234567890");
        
        model.addAttribute("player", player);
        
        System.out.println("hello page");
        return "hello";
    }


    @PostMapping("/submit-name")
    public String getName(@RequestParam String name, Model model)
    {

        model.addAttribute("name", name);

        System.out.println("name " + name);
        
        return "hello";
    }
    

}
