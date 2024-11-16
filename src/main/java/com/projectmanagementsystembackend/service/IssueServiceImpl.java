package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.Issue;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.IssueRepository;
import com.projectmanagementsystembackend.repository.UserRepository;
import com.projectmanagementsystembackend.request.IssueRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IssueServiceImpl implements IssueService{
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @Override
    public Issue getIssueById(Long issueId) throws Exception {
        Optional<Issue> issue = issueRepository.findById(issueId);
        if (issue.isEmpty()){
            throw new Exception("Not found issue with given id :"+issueId);
        }
        return issue.get();
    }

    @Override
    public List<Issue> getIssueByProjectId(Long projectId) throws Exception {
        List<Issue> issues = issueRepository.findByProjectId(projectId);

        if (issues.isEmpty()){
            throw new Exception("Not found any issues for this project");
        }
        return issues;
    }

    @Override
    public Issue createIssue(IssueRequest issueRequest, Long userId) throws Exception {
        Issue issue = new Issue();
        Project project = projectService.getProjectById(issueRequest.getProjectId());
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("Not found user"));
        issue.setStatus(issueRequest.getStatus());
//        issue.setAssignee(user);
        issue.setDescription(issueRequest.getDescription());
        issue.setDueDate(issueRequest.getDueDate());
        issue.setPriority(issueRequest.getPriority());
        issue.setTitle(issueRequest.getTitle());
        issue.setProjectId(issueRequest.getProjectId());
        issue.setProject(project);

        issueRepository.save(issue);
        return issue;
    }

    @Override
    public void deleteIssue(Long issueId, Long userId) throws Exception {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new Exception("Not found") );

        issueRepository.delete(issue);
    }

    @Override
    public Issue addUserToIssue(Long issueId, Long userId) throws Exception {
        User user = userService.findUserById(userId);
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new Exception("Not found"));
        issue.setAssignee(user);
        return issueRepository.save(issue);
    }

    @Override
    public Issue updateIssueStatus(Long issueId, String status) throws Exception {
        Issue issue = getIssueById(issueId);
        if (issue == null){
            throw new Exception("Not found issue");
        }
        issue.setStatus(status);
        return issueRepository.save(issue);
    }
}
