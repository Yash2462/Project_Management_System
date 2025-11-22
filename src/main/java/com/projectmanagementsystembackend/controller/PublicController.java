package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.Invitation;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.service.InvitationService;
import com.projectmanagementsystembackend.service.ProjectService;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Public controller for endpoints that don't require authentication.
 * These endpoints are accessible without JWT tokens.
 */
@RestController
@RequestMapping("/projects")
public class PublicController {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    /**
     * Accept project invitation - Public endpoint
     * Users receive invitation links via email and may not be authenticated
     * 
     * @param token The unique invitation token
     * @return Response with invitation details
     */
    @GetMapping("/accept_invitation")
    public ResponseEntity<Object> acceptProjectInvite(@RequestParam String token) throws Exception {
        ResponseMessage responseMessage = new ResponseMessage();
        
        try {
            // Accept invitation using the token - token contains user email info
            Invitation invitation = invitationService.acceptInvitation(token);
            
            // Get user by email from invitation
            User user = userService.findUserByEmail(invitation.getEmail());
            
            // Add user to project
            projectService.addUserToProject(invitation.getProjectId(), user.getId());

            responseMessage.setMessage("Invitation accepted successfully. You have been added to the project.");
            responseMessage.setStatus(200);
            responseMessage.setData(invitation);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        } catch (Exception e) {
            responseMessage.setMessage("Failed to accept invitation: " + e.getMessage());
            responseMessage.setStatus(400);
            return new ResponseEntity<>(responseMessage, HttpStatus.BAD_REQUEST);
        }
    }
}

