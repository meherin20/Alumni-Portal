package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.dtos.MessageCreateDto;
import com.miu.alumnimanagementportal.dtos.MessageDto;
import com.miu.alumnimanagementportal.entities.Message;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.FriendRequestRepository;
import com.miu.alumnimanagementportal.repositories.MessageRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    @Override
    public MessageDto send(MessageCreateDto dto) {
        User sender = userRepository.findByEmail(dto.getSenderEmail());
        if (sender == null) {
            throw new ResourceNotFoundException("User with email " + dto.getSenderEmail() + " not found");
        }
        User recipient = userRepository.findByEmail(dto.getRecipientEmail());
        if (recipient == null) {
            throw new ResourceNotFoundException("User with email " + dto.getRecipientEmail() + " not found");
        }

        boolean hasAccepted = friendRequestRepository.existsByStudentEmailAndAlumniEmailAndStatus(
                sender.getEmail(),
                recipient.getEmail(),
                FriendRequestStatus.ACCEPTED
        );
        if (!hasAccepted) {
            throw new BadRequestException("You can only message alumni after your add request has been accepted.");
        }

        Message msg = new Message();
        msg.setSender(sender);
        msg.setRecipient(recipient);
        msg.setContent(dto.getContent());
        Message saved = messageRepository.save(msg);
        return toDto(saved);
    }

    @Override
    public List<MessageDto> getInbox(String recipientEmail) {
        return messageRepository.findByRecipientEmailOrderByCreatedDateAsc(recipientEmail)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private MessageDto toDto(Message msg) {
        String senderName = msg.getSender().getFirstName() + " " + msg.getSender().getLastName();
        return new MessageDto(
                msg.getId(),
                senderName,
                msg.getSender().getEmail(),
                msg.getRecipient().getEmail(),
                msg.getContent(),
                msg.getCreatedDate()
        );
    }
}


