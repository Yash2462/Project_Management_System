package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.dto.DashboardStatisticsDto;
import com.projectmanagementsystembackend.dto.ProjectCountsDto;
import com.projectmanagementsystembackend.dto.RecentActivityDto;
import com.projectmanagementsystembackend.model.User;

import java.util.List;

public interface DashboardService {

    DashboardStatisticsDto getStatistics(User user) throws Exception;

    List<RecentActivityDto> getRecentActivity(User user, int limit) throws Exception;

    ProjectCountsDto getProjectCounts(User user) throws Exception;
}

