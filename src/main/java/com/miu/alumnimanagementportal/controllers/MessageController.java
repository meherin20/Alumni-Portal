package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.MessageCreateDto;
import com.miu.alumnimanagementportal.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> send(@Valid @RequestBody MessageCreateDto dto) {
        return converter.buildResponseEntity(
                Map.of("data", messageService.send(dto)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/inbox")
    public ResponseEntity<?> inbox(@RequestParam String recipientEmail) {
        return converter.buildResponseEntity(
                Map.of("data", messageService.getInbox(recipientEmail)),
                HttpStatus.OK
        );
    }
}


