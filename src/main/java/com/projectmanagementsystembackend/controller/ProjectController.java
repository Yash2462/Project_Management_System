package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.model.Chat;
import com.projectmanagementsystembackend.model.Invitation;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.request.InviteRequest;
import com.projectmanagementsystembackend.service.InvitationService;
import com.projectmanagementsystembackend.service.ProjectService;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;
    @Autowired
    private InvitationService invitationService;

    @GetMapping
    public ResponseEntity<Object> getProjects(@RequestParam(required = false) String category,
                                                     @RequestParam(required = false) String tag,
                                                     @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projects = projectService.getProjectByTeam(user,category,tag);
        ResponseMessage responseMessage = new ResponseMessage();
        if (projects.isEmpty()){
            responseMessage.setMessage("Not found Data");
            responseMessage.setStatus(404);
            responseMessage.setData(projects);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("Data Found Successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(projects);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    @GetMapping("/{projectId}")
    public ResponseEntity<Object> getProjectById(@PathVariable(value = "projectId")Long projectId,
                                              @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Project project = projectService.getProjectById(projectId);
        ResponseMessage responseMessage = new ResponseMessage();
        if (project == null){
            responseMessage.setMessage("Not found Data");
            responseMessage.setStatus(404);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("Data Found Successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(project);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Object> createProject(@RequestHeader("Authorization") String jwt,
                                                 @RequestBody Project project) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        ResponseMessage responseMessage = new ResponseMessage();
        Project project1 = projectService.createProject(project,user);
        if (project1 == null){
            responseMessage.setMessage("Error in data Save");
            responseMessage.setStatus(500);

            return  new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseMessage.setMessage("Data saved successfully");
        responseMessage.setStatus(201);
        return new ResponseEntity<>(responseMessage,HttpStatus.CREATED);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Object> updateProject(@PathVariable(value = "projectId") Long projectId,
                                                @RequestHeader("Authorization") String jwt,
                                                @RequestBody Project project) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Project existingProject = projectService.updateProject(project,projectId);
        ResponseMessage responseMessage = new ResponseMessage();
        if (existingProject == null){
            responseMessage.setMessage("Error in data Save");
            responseMessage.setStatus(500);

            return  new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        responseMessage.setMessage("Project updated successfully");
        responseMessage.setStatus(200);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Object> deleteProject(@PathVariable(value = "projectId") Long projectId,
                                                @RequestHeader("Authorization") String jwt
                                                ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
         projectService.deleteProject(projectId,user.getId());
        ResponseMessage responseMessage = new ResponseMessage();

        responseMessage.setMessage("Project deleted successfully");
        responseMessage.setStatus(200);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<Object> searchProjects(@RequestParam(required = false) String keyword,
                                                 @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Project> projects = projectService.searchProjects(keyword,user);
        ResponseMessage responseMessage = new ResponseMessage();
        if (projects.isEmpty()){
            responseMessage.setMessage("Not found Any Projects");
            responseMessage.setStatus(404);
            responseMessage.setData(projects);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("Projects Found Successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(projects);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    @GetMapping("/{projectId}/chat")
    public ResponseEntity<Object> getChatByProjectId(@PathVariable(value = "projectId")Long projectId,
                                                 @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Chat chat = projectService.getChatByProjectId(projectId);
        ResponseMessage responseMessage = new ResponseMessage();
        if (chat == null){
            responseMessage.setMessage("Not found Any Chats");
            responseMessage.setStatus(404);
            responseMessage.setData(chat);
            return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
        }

        responseMessage.setMessage("Chats Found Successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(chat);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }


    @PostMapping("/invite")
    public ResponseEntity<Object> inviteProject(@RequestBody InviteRequest inviteRequest,
                                                @RequestHeader("Authorization") String jwt,
                                                @RequestBody Project project) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        ResponseMessage responseMessage = new ResponseMessage();
        Project project1 = projectService.createProject(project,user);
        invitationService.sendInvitation(inviteRequest.getEmail(),inviteRequest.getProjectId());


        responseMessage.setMessage("User Invitation sent successfully");
        responseMessage.setStatus(200);
        return new ResponseEntity<>(responseMessage,HttpStatus.OK);
    }

    @GetMapping("/accept_invitation")
    public ResponseEntity<Object> acceptProjectInvite(@RequestParam String token,
                                                @RequestHeader("Authorization") String jwt,
                                                @RequestBody Project project) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        ResponseMessage responseMessage = new ResponseMessage();
        Invitation invitation = invitationService.acceptInvitation(token, user.getId());
        projectService.addUserToProject(invitation.getProjectId(), user.getId());

        responseMessage.setMessage("User Invitation accepted successfully");
        responseMessage.setStatus(200);
        responseMessage.setData(invitation);
        return new ResponseEntity<>(responseMessage,HttpStatus.ACCEPTED);
    }


}
