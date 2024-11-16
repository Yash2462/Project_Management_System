package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.Comments;
import com.projectmanagementsystembackend.model.Issue;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.CommentsRepository;
import com.projectmanagementsystembackend.repository.IssueRepository;
import com.projectmanagementsystembackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;
    @Override
    public Comments createComment(Long issueId, Long userId, String comment) throws Exception {
        Optional<Issue> issueOptional = issueRepository.findById(issueId);
        Optional<User> userOptional = userRepository.findById(userId);

        if (issueOptional.isEmpty()){
            throw  new Exception("Not found issue");
        }
        if (userOptional.isEmpty()){
            throw new Exception("Not found user");
        }
        Issue issue = issueOptional.get();
        User user = userOptional.get();

        Comments comments = new Comments();

        comments.setUser(user);
        comments.setIssue(issue);
        comments.setCreatedDateTime(LocalDateTime.now());
        comments.setContent(comment);

        Comments savedComment = commentsRepository.save(comments);

        issue.getComments().add(savedComment);

        return savedComment;
    }

    @Override
    public void deleteComment(Long commentId, Long userId) throws Exception {
        Optional<Comments> commentsOptional = commentsRepository.findById(commentId);
        Optional<User> userOptional = userRepository.findById(userId);

        Comments comments = commentsOptional.get();
        User user = userOptional.get();

        if (comments.getUser().equals(user)) {
            commentsRepository.delete(comments);
        }else {
            throw new Exception("User does not have the permission to delete this comment");
        }
    }

    @Override
    public List<Comments> findCommentByIssueId(Long issueId) throws Exception {

        List<Comments> comments = commentsRepository.findCommentsByIssueId(issueId);
        if (comments.isEmpty()){
            throw new Exception("Not found comments for this issue :"+issueId);
        }

        return comments;
    }
}
