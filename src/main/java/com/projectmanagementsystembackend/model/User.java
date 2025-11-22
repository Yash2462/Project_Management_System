package com.projectmanagementsystembackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(
    name = "`user`",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_id", columnList = "id")
    }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String password;

    // whenever we create project increase this and remove project then decrease this tracking number of project
    private int projectSize;

    @OneToMany(mappedBy = "assignee",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Issue> assignedIssues = new ArrayList<>();
}
