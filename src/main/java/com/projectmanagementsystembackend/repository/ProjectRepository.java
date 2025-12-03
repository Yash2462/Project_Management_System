package com.projectmanagementsystembackend.repository;

import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {
    List<Project> findByOwner(User user);

    List<Project> findByNameContainingAndTeamContains(String partialName,User user);

    @Query("SELECT p from Project p Join p.team t where t=:user")
    List<Project> findProjectByTeam(@Param("user") User user);

    List<Project> findByTeamContainingOrOwner(User user,User owner);

    @Query("SELECT p FROM Project p WHERE (p.owner = :user OR :user MEMBER OF p.team) AND p.createdAt >= :startDate")
    List<Project> findProjectsCreatedAfter(@Param("user") User user, @Param("startDate") LocalDateTime startDate);

    @Query("SELECT p FROM Project p WHERE (p.owner = :user OR :user MEMBER OF p.team) AND p.createdAt >= :startDate ORDER BY p.createdAt DESC")
    List<Project> findRecentProjects(@Param("user") User user, @Param("startDate") LocalDateTime startDate);
}
