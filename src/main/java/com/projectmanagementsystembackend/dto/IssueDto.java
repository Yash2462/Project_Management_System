package com.projectmanagementsystembackend.dto;

import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssueDto {

    private Long id;

    private String title;
    private String description;
    private String status;
    private Long projectId;
    private String priority;
    private LocalDate dueDate;

    private List<String> tags = new ArrayList<>();
    private Project project;
    private User assignee;
}
