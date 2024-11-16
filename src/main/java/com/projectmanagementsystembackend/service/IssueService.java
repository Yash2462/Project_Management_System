package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.Issue;
import com.projectmanagementsystembackend.request.IssueRequest;

import java.util.List;
import java.util.Optional;

public interface IssueService {

    Issue getIssueById(Long issueId) throws Exception;

    List<Issue> getIssueByProjectId(Long projectId) throws Exception;

    Issue createIssue (IssueRequest issueRequest, Long userId) throws Exception;

    void deleteIssue(Long issueId ,Long userId) throws  Exception;

    Issue addUserToIssue(Long issueId , Long userId) throws Exception;

    Issue updateIssueStatus (Long issueId , String status) throws Exception;
}
