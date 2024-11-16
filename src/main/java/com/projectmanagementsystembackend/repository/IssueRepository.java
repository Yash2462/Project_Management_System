package com.projectmanagementsystembackend.repository;

import com.projectmanagementsystembackend.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Long> {

    List<Issue> findByProjectId(Long projectId);
}
