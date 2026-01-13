package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRecipientEmailOrderByCreatedDateAsc(String recipientEmail);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.email = :email1 AND m.recipient.email = :email2) OR " +
           "(m.sender.email = :email2 AND m.recipient.email = :email1) " +
           "ORDER BY m.createdDate ASC")
    List<Message> findConversation(@Param("email1") String email1, @Param("email2") String email2);
}


