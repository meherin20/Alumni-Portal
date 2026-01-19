package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.dtos.MessageCreateDto;
import com.miu.alumnimanagementportal.dtos.MessageDto;
import com.miu.alumnimanagementportal.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class MessageSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/messages.send")
    public void send(@Valid @Payload MessageCreateDto dto) {
        MessageDto saved = messageService.send(dto);
        String recipientDestination = "/topic/messages." + saved.getRecipientEmail();
        String senderDestination = "/topic/messages." + saved.getSenderEmail();
        messagingTemplate.convertAndSend(recipientDestination, saved);
        messagingTemplate.convertAndSend(senderDestination, saved);
    }
}
