package com.projectmanagementsystembackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDto {
    private Long id;
    private String action;
    private String description;
    private String type;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long projectId;
    private String projectName;
    private Long entityId;
    private String entityType;
    private String priority;
    private Long assigneeId;
    private String assigneeName;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
    private String timeAgo;
}

