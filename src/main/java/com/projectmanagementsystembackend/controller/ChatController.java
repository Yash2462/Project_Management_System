package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.Message;
import com.projectmanagementsystembackend.request.MessageRequest;
import com.projectmanagementsystembackend.service.MessageService;
import com.projectmanagementsystembackend.service.ProjectService;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class ChatController {
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

   @PostMapping("/send")
    public ResponseEntity<Object> sendMessages(@RequestBody MessageRequest messageRequest) throws Exception{
        Message message = messageService.sendMessage(messageRequest.getSenderId(),
                                                    messageRequest.getProjectId(),
                                                    messageRequest.getContent());
        ResponseMessage responseMessage = new ResponseMessage();
        if (message == null){
            responseMessage.setMessage("Error in sending message");
            responseMessage.setStatus(500);

            return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseMessage.setMessage("Message sent Successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(message);

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }

    @GetMapping("/chat/{projectId}")
    public ResponseEntity<Object> getMessagesByProjectId(@PathVariable(value = "projectId") Long projectId) throws Exception {

       List<Message> messages = messageService.getMessagesByProjectId(projectId);
       ResponseMessage responseMessage = new ResponseMessage();

       if (messages.isEmpty()){
           responseMessage.setMessage("Not found any messages for project :"+projectId);
           responseMessage.setStatus(404);

           return new ResponseEntity<>(responseMessage,HttpStatus.NOT_FOUND);
       }

       responseMessage.setMessage("Found messages Successfully");
       responseMessage.setStatus(200);
       responseMessage.setData(messages);

       return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }
}
