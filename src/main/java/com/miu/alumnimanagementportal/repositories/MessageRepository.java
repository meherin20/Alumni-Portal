package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientEmailOrderByCreatedDateAsc(String recipientEmail);
}


