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
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String description;
    private String status;
    private Long projectId;
    private String priority;
    private LocalDate dueDate;
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
