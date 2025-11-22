package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.dto.IssueDto;
import com.projectmanagementsystembackend.model.Issue;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.request.IssueRequest;
import com.projectmanagementsystembackend.service.IssueService;
import com.projectmanagementsystembackend.service.UserService;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/issues")
public class IssueController {
    @Autowired
    private IssueService issueService;
    @Autowired
    private UserService userService;

   @GetMapping("/{issueId}")
    public ResponseEntity<Object> getIssueById(@PathVariable(value = "issueId") Long issueId) throws Exception {
       Issue issue = issueService.getIssueById(issueId);
       ResponseMessage responseMessage = new ResponseMessage();
       if (issue == null){
           responseMessage.setMessage("Not found issue");
           responseMessage.setStatus(404);

           return new ResponseEntity<>(responseMessage, HttpStatus.NOT_FOUND);
       }

       responseMessage.setMessage("Data Found Successfully");
       responseMessage.setStatus(200);
       responseMessage.setData(issue);

       return new ResponseEntity<>(responseMessage,HttpStatus.OK);
   }

   @GetMapping("/project/{projectId}")
   public ResponseEntity<Object> getIssueByProjectId(@PathVariable(value = "projectId") Long projectId) throws Exception {
       List<Issue> issues = issueService.getIssueByProjectId(projectId);
       ResponseMessage responseMessage = new ResponseMessage();
       if (issues.isEmpty()){
           responseMessage.setMessage("Not found any issue");
           responseMessage.setStatus(404);

           return new ResponseEntity<>(responseMessage,HttpStatus.NOT_FOUND);
       }

       responseMessage.setMessage("Data Found Success");
       responseMessage.setStatus(200);
       responseMessage.setData(issues);

       return new ResponseEntity<>(responseMessage,HttpStatus.OK);
   }


   @PostMapping
   public ResponseEntity<Object> createIssue(@Valid @RequestBody IssueRequest issueRequest) throws Exception {
       User tokenUser = userService.getCurrentUser();
       Issue issue = issueService.createIssue(issueRequest, tokenUser.getId());
       ResponseMessage responseMessage = new ResponseMessage();
       if (issue != null) {
           IssueDto issueDto = new IssueDto();
           issueDto.setId(issue.getId());
           issueDto.setStatus(issue.getStatus());
           issueDto.setPriority(issue.getPriority());
           issueDto.setTitle(issue.getTitle());
           issueDto.setDescription(issue.getDescription());
           issueDto.setDueDate(issue.getDueDate());
           issueDto.setProject(issue.getProject());
           issueDto.setAssignee(issue.getAssignee());
           issueDto.setTags(issue.getTags());

           responseMessage.setMessage("Issue created successfully");
           responseMessage.setStatus(201);
           responseMessage.setData(issueDto);

           return new ResponseEntity<>(responseMessage,HttpStatus.CREATED);
       }

       responseMessage.setMessage("Error in issue creation");
       responseMessage.setStatus(500);
       return new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
   }

   @DeleteMapping("/{issueId}")
   public ResponseEntity<Object> deleteIssue(@PathVariable(value ="issueId") Long issueId) throws Exception {
       User tokenUser = userService.getCurrentUser();
       issueService.deleteIssue(issueId, tokenUser.getId());

       ResponseMessage responseMessage = new ResponseMessage();

       responseMessage.setMessage("User Deleted Successfully");
       responseMessage.setStatus(200);

       return new ResponseEntity<>(responseMessage,HttpStatus.OK);
   }


   @PutMapping("/{issueId}/assignee/{userId}")
   public ResponseEntity<Object> addUserToIssue(@PathVariable(value = "issueId") Long issueId,
                                                @PathVariable(value = "userId") Long userId) throws Exception {

       Issue issue = issueService.addUserToIssue(issueId,userId);
       ResponseMessage responseMessage = new ResponseMessage();
       if (issue == null){
           responseMessage.setMessage("Error in adding user to issue");
           responseMessage.setStatus(500);

           return new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
       }

       responseMessage.setMessage("User added to issue successfully");
       responseMessage.setStatus(200);
       responseMessage.setData(issue);

       return new ResponseEntity<>(responseMessage,HttpStatus.OK);
   }


   @PutMapping("/{issueId}/status/{status}")
   public ResponseEntity<Object> updateIssueStatus(@PathVariable(value = "issueId") Long issueId,
                                                   @PathVariable(value = "status") String status) throws Exception {
       Issue issue = issueService.updateIssueStatus(issueId,status);
       ResponseMessage responseMessage = new ResponseMessage();
       if (issue == null){
           responseMessage.setMessage("Error in updating issue");
           responseMessage.setStatus(500);

           return new ResponseEntity<>(responseMessage,HttpStatus.INTERNAL_SERVER_ERROR);
       }

       responseMessage.setMessage("Issue updated successfully");
       responseMessage.setStatus(200);
       responseMessage.setData(issue);

       return new ResponseEntity<>(responseMessage,HttpStatus.OK);
   }

}
