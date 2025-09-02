package com.bgmi.controller;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.bgmi.chat.ChatMessage;

@Controller
public class ChatController {
    
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage handleMessage(ChatMessage message) {
        // Set server timestamp
        message.setTimestamp(LocalTime.now().format(TIME_FORMAT));
        
        System.out.println("Broadcasting message: " + message.getContent() + 
                          " from: " + message.getSender());
        
        return message;
    }
    
    @GetMapping("/chatbot")
    public String chatPage() {
        return "player/chat";
    }
}
