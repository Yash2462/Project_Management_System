package com.projectmanagementsystembackend.controller;

import com.projectmanagementsystembackend.dto.DashboardStatisticsDto;
import com.projectmanagementsystembackend.dto.PaginationDto;
import com.projectmanagementsystembackend.dto.ProjectCountsDto;
import com.projectmanagementsystembackend.dto.RecentActivityDto;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.service.DashboardService;
import com.projectmanagementsystembackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Controller
 * Provides endpoints for dashboard statistics, recent activity, and project counts
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private UserService userService;

    /**
     * Get dashboard statistics
     * GET /api/dashboard/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                return createErrorResponse("User not found", "UNAUTHORIZED", true);
            }

            DashboardStatisticsDto statistics = dashboardService.getStatistics(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching dashboard statistics", e);
            return createErrorResponse("Failed to fetch dashboard data", "INTERNAL_ERROR", false);
        }
    }

    /**
     * Get recent activity
     * GET /api/dashboard/recent-activity?limit=10
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                return createErrorResponse("User not found", "UNAUTHORIZED", true);
            }

            List<RecentActivityDto> activities = dashboardService.getRecentActivity(user, limit);

            // Create pagination info
            PaginationDto pagination = new PaginationDto();
            pagination.setTotal(activities.size());
            pagination.setPage(1);
            pagination.setLimit(limit);
            pagination.setHasNext(activities.size() > limit);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", activities);
            response.put("pagination", pagination);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching recent activity", e);
            return createErrorResponse("Failed to fetch dashboard data", "INTERNAL_ERROR", false);
        }
    }

    /**
     * Get project counts
     * GET /api/dashboard/project-counts
     */
    @GetMapping("/project-counts")
    public ResponseEntity<Map<String, Object>> getProjectCounts() {
        try {
            User user = userService.getCurrentUser();
            if (user == null) {
                return createErrorResponse("User not found", "UNAUTHORIZED", true);
            }

            ProjectCountsDto counts = dashboardService.getProjectCounts(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", counts);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching project counts", e);
            return createErrorResponse("Failed to fetch dashboard data", "INTERNAL_ERROR", false);
        }
    }

    /**
     * Helper method to create error responses
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(
            String message, String code, boolean redirectToLogin) {
        Map<String, Object> error = new HashMap<>();
        error.put("code", code);
        error.put("message", message);
        if (redirectToLogin) {
            error.put("redirectToLogin", true);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", error);

        HttpStatus status = "UNAUTHORIZED".equals(code) ? HttpStatus.UNAUTHORIZED : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status).body(response);
    }
}

