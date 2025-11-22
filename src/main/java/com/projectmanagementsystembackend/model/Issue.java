package com.projectmanagementsystembackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "issue",
    indexes = {
        @Index(name = "idx_issue_status", columnList = "status"),
        @Index(name = "idx_issue_priority", columnList = "priority"),
        @Index(name = "idx_issue_project_id", columnList = "projectId"),
        @Index(name = "idx_issue_assignee", columnList = "assignee_id"),
        @Index(name = "idx_issue_due_date", columnList = "dueDate")
    }
)
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String description;

    private String status;
    private Long projectId;
    private String priority;
    private LocalDate dueDate;

    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "issue",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comments> comments = new ArrayList<>();

    @ManyToOne
    private User assignee;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "projects")
    private Project project;
}
