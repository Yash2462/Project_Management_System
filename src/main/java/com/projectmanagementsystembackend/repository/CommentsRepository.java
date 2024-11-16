package com.projectmanagementsystembackend.repository;

import com.projectmanagementsystembackend.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments,Long> {


    List<Comments> findCommentsByIssueId(Long issueId);
}
