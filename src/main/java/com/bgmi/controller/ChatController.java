package com.bgmi.controller;

import java.time.format.DateTimeFormatter;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.bgmi.chat.ChatMessage;

@Controller
public class ChatController
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public ChatMessage chatMessage(ChatMessage message)
    {
        message.setTimestamp(java.time.LocalTime.now().format(FORMAT));
        return message;
    }

}
