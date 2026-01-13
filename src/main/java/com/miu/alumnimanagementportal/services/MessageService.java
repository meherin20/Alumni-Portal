package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.MessageCreateDto;
import com.miu.alumnimanagementportal.dtos.MessageDto;

import java.util.List;

public interface MessageService {

    MessageDto send(MessageCreateDto dto);

    List<MessageDto> getInbox(String recipientEmail);
    
    List<MessageDto> getConversation(String email1, String email2);
}


