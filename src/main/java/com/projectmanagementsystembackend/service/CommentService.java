package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.Comments;

import java.util.List;

public interface CommentService {

    Comments createComment(Long issueId,Long userId , String comment) throws  Exception;

    void deleteComment(Long commentId , Long userId ) throws Exception;

    List<Comments> findCommentByIssueId(Long issueId) throws  Exception;
}
