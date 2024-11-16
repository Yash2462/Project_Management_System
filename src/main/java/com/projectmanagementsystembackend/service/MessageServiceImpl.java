package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.Chat;
import com.projectmanagementsystembackend.model.Message;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class MessageServiceImpl implements MessageService{
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Override
    public Message sendMessage(Long senderId, Long projectId, String content) throws Exception {

        User user = userService.findUserById(senderId);
        Chat chat = projectService.getChatByProjectId(projectId);

        Message message = new Message();
        message.setChat(chat);
        message.setContent(content);
        message.setSender(user);
        message.setCreatedAt(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        chat.getMessages().add(message);
        return savedMessage;
    }

    @Override
    public List<Message> getMessagesByProjectId(Long projectId) throws Exception {
       Chat chat = projectService.getChatByProjectId(projectId);

       List<Message> messages = messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId());

        return messages;
    }
}
