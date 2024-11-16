package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.Comments;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.request.CommentRequest;
import com.projectmanagementsystembackend.service.CommentService;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Object> createComment(@RequestBody CommentRequest request,
                                                @RequestHeader("Authorization") String token
                                                ) throws Exception {
        User user = userService.findUserProfileByJwt(token);
       Comments comments = commentService.createComment(request.getIssueId(), user.getId(),request.getContent());
        ResponseMessage responseMessage = new ResponseMessage();
       if (comments == null){
           responseMessage.setMessage("Error in create comment");
           responseMessage.setStatus(500);

           return new ResponseEntity<>(responseMessage, HttpStatus.INTERNAL_SERVER_ERROR);
       }
       responseMessage.setMessage("Comment Created Successfully");
       responseMessage.setStatus(201);
       responseMessage.setData(comments);

       return new ResponseEntity<>(responseMessage,HttpStatus.CREATED);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Object> deleteComment(@PathVariable Long commentId ,
                                                @RequestHeader("Authorization") String token) throws Exception {

        User user = userService.findUserProfileByJwt(token);
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            commentService.deleteComment(commentId, user.getId());
            responseMessage.setMessage("Comment Deleted Successfully");
            responseMessage.setStatus(200);
            return new ResponseEntity<>(responseMessage,HttpStatus.OK);
        }catch (Exception e){
            responseMessage.setMessage("Error in delete message");
            responseMessage.setStatus(500);
            return new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{issueId}")
    public ResponseEntity<Object> getCommentByIssueId(@PathVariable Long issueId) throws Exception {

        List<Comments> comments = commentService.findCommentByIssueId(issueId);
        ResponseMessage responseMessage = new ResponseMessage();

        if (comments.isEmpty()){
            responseMessage.setMessage("Not found any comment in this issue");
            responseMessage.setStatus(404);

            return new ResponseEntity<>(responseMessage,HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("Found Comments for this issue");
        responseMessage.setStatus(200);
        responseMessage.setData(comments);

        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }
}
