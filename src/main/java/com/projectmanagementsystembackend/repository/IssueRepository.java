package com.projectmanagementsystembackend.repository;

import com.projectmanagementsystembackend.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue,Long> {

    List<Issue> findByProjectId(Long projectId);

    @Query("SELECT i FROM Issue i WHERE i.projectId IN :projectIds AND i.status = 'done' AND i.updatedAt >= :startDate")
    List<Issue> findCompletedIssuesAfter(@Param("projectIds") List<Long> projectIds, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT i FROM Issue i WHERE i.projectId IN :projectIds AND i.status != 'done' AND i.dueDate IS NOT NULL AND i.dueDate < CAST(CURRENT_DATE AS java.time.LocalDate)")
    List<Issue> findOverdueIssues(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT i FROM Issue i WHERE i.projectId IN :projectIds AND i.status != 'done' AND i.dueDate IS NOT NULL AND i.dueDate >= CAST(CURRENT_DATE AS java.time.LocalDate) AND i.dueDate <= CAST(CURRENT_DATE + 7 AS java.time.LocalDate)")
    List<Issue> findDueSoonIssues(@Param("projectIds") List<Long> projectIds);

    @Query("SELECT i FROM Issue i WHERE i.projectId IN :projectIds AND i.createdAt >= :startDate")
    List<Issue> findIssuesCreatedAfter(@Param("projectIds") List<Long> projectIds, @Param("startDate") LocalDateTime startDate);
}
