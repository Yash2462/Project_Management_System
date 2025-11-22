package com.projectmanagementsystembackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDto {
    private int totalProjects;
    private String totalProjectsChange;
    private int teamMembers;
    private String teamMembersChange;
    private int activeIssues;
    private String activeIssuesChange;
    private int completedTasks;
    private String completedTasksChange;
}

