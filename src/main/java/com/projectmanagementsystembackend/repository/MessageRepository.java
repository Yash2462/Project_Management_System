package com.projectmanagementsystembackend.repository;

import com.projectmanagementsystembackend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {


    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);
}
