package com.projectmanagementsystembackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCountsDto {
    private int total;
    private Map<String, Integer> byStatus = new HashMap<>();
    private Map<String, Integer> byPriority = new HashMap<>();
    private int recentlyCreated;
    private int overdue;
    private int dueSoon;
}

