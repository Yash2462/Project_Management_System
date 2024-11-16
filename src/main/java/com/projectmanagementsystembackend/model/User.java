package com.projectmanagementsystembackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String fullName;
    private String email;
    private String password;
    // whenever we create project increase this and remove project then decrease this tracking number of project
    private int projectSize;

    @OneToMany(mappedBy = "assignee",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Issue> assignedIssues = new ArrayList<>();
}
