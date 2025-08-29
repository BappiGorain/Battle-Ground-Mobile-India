package com.bgmi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController 
{

    @GetMapping("/greeting")
    public String helloPage()
    {
        System.out.println("hello page");
        return "hello";
    }

    
}
