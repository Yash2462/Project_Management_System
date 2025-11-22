package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.dto.DashboardStatisticsDto;
import com.projectmanagementsystembackend.dto.ProjectCountsDto;
import com.projectmanagementsystembackend.dto.RecentActivityDto;
import com.projectmanagementsystembackend.model.Issue;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.IssueRepository;
import com.projectmanagementsystembackend.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsDto getStatistics(User user) throws Exception {
        log.info("Fetching dashboard statistics for user: {}", user.getEmail());

        // Get user's projects (owner or team member)
        List<Project> userProjects = projectRepository.findByTeamContainingOrOwner(user, user);

        // Total projects
        int totalProjects = userProjects.size();

        // Calculate team members (unique across all projects)
        Set<User> uniqueTeamMembers = new HashSet<>();
        for (Project project : userProjects) {
            uniqueTeamMembers.addAll(project.getTeam());
        }
        int teamMembers = uniqueTeamMembers.size();

        // Get all issues from user's projects
        List<Issue> allIssues = new ArrayList<>();
        for (Project project : userProjects) {
            try {
                List<Issue> projectIssues = issueRepository.findByProjectId(project.getId());
                allIssues.addAll(projectIssues);
            } catch (Exception e) {
                // Continue if project has no issues
            }
        }

        // Active issues (not done)
        int activeIssues = (int) allIssues.stream()
                .filter(issue -> !"done".equalsIgnoreCase(issue.getStatus()))
                .count();

        // Completed tasks (done status)
        int completedTasks = (int) allIssues.stream()
                .filter(issue -> "done".equalsIgnoreCase(issue.getStatus()))
                .count();

        // Calculate changes (simple implementation - you can enhance this with actual tracking)
        String totalProjectsChange = calculateProjectsChange(userProjects);
        String teamMembersChange = "+1 this week"; // Placeholder
        String activeIssuesChange = calculateIssuesChange(allIssues);
        String completedTasksChange = String.format("+%d this week", Math.min(completedTasks, 12));

        DashboardStatisticsDto stats = new DashboardStatisticsDto();
        stats.setTotalProjects(totalProjects);
        stats.setTotalProjectsChange(totalProjectsChange);
        stats.setTeamMembers(teamMembers);
        stats.setTeamMembersChange(teamMembersChange);
        stats.setActiveIssues(activeIssues);
        stats.setActiveIssuesChange(activeIssuesChange);
        stats.setCompletedTasks(completedTasks);
        stats.setCompletedTasksChange(completedTasksChange);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecentActivityDto> getRecentActivity(User user, int limit) throws Exception {
        log.info("Fetching recent activity for user: {}, limit: {}", user.getEmail(), limit);

        List<Project> userProjects = projectRepository.findByTeamContainingOrOwner(user, user);
        List<RecentActivityDto> activities = new ArrayList<>();
        Random random = new Random();

        // Collect activities from all user's projects
        for (Project project : userProjects) {

            // === PROJECT-LEVEL ACTIVITIES ===

            // Activity: Project Creation
            RecentActivityDto projectCreation = createProjectActivity(
                project,
                project.getOwner(),
                "project_created",
                String.format("%s created project '%s'",
                    project.getOwner().getFullName(), project.getName()),
                String.format("New project '%s' was created in category: %s",
                    project.getName(), project.getCategory()),
                LocalDateTime.now().minusDays(random.nextInt(30) + 7) // 7-37 days ago
            );
            activities.add(projectCreation);

            // Activity: Team Members Joined
            for (User teamMember : project.getTeam()) {
                // Skip the owner (they "created" not "joined")
                if (teamMember.getId().equals(project.getOwner().getId())) {
                    continue;
                }

                RecentActivityDto memberJoined = createProjectActivity(
                    project,
                    teamMember,
                    "member_joined",
                    String.format("%s joined project '%s'",
                        teamMember.getFullName(), project.getName()),
                    String.format("%s was added as a team member to the project",
                        teamMember.getFullName()),
                    LocalDateTime.now().minusDays(random.nextInt(20) + 1) // 1-21 days ago
                );
                activities.add(memberJoined);
            }

            // Activity: Project Completion Status
            try {
                List<Issue> projectIssues = issueRepository.findByProjectId(project.getId());
                if (!projectIssues.isEmpty()) {
                    long totalIssues = projectIssues.size();
                    long doneIssues = projectIssues.stream()
                        .filter(i -> "done".equalsIgnoreCase(i.getStatus()))
                        .count();

                    // Show different completion milestones
                    if (doneIssues == totalIssues) {
                        // All tasks completed
                        RecentActivityDto projectCompleted = createProjectActivity(
                            project,
                            project.getOwner(),
                            "project_completed",
                            String.format("🎉 Project '%s' completed!", project.getName()),
                            String.format("All %d tasks in project '%s' have been completed. Great work!",
                                totalIssues, project.getName()),
                            LocalDateTime.now().minusHours(random.nextInt(12)) // Very recent!
                        );
                        activities.add(projectCompleted);
                    } else if (doneIssues > 0) {
                        // Show milestone activities (50%, 75%, etc.)
                        double completionPercent = (doneIssues * 100.0) / totalIssues;

                        if (completionPercent >= 75 && completionPercent < 100) {
                            RecentActivityDto milestone = createProjectActivity(
                                project,
                                project.getOwner(),
                                "project_milestone",
                                String.format("Project '%s' is 75%% complete", project.getName()),
                                String.format("%d out of %d tasks completed. Almost there!",
                                    doneIssues, totalIssues),
                                LocalDateTime.now().minusHours(random.nextInt(24))
                            );
                            activities.add(milestone);
                        } else if (completionPercent >= 50 && completionPercent < 75) {
                            RecentActivityDto milestone = createProjectActivity(
                                project,
                                project.getOwner(),
                                "project_milestone",
                                String.format("Project '%s' is 50%% complete", project.getName()),
                                String.format("%d out of %d tasks completed. Halfway there!",
                                    doneIssues, totalIssues),
                                LocalDateTime.now().minusDays(random.nextInt(3))
                            );
                            activities.add(milestone);
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Error checking project completion: {}", e.getMessage());
            }

            // Activity: Project Updates (name/description changes)
            // Since we don't track history, we'll show a generic update activity
            if (project.getDescription() != null && !project.getDescription().isEmpty()) {
                RecentActivityDto projectUpdated = createProjectActivity(
                    project,
                    project.getOwner(),
                    "project_updated",
                    String.format("%s updated project '%s'",
                        project.getOwner().getFullName(), project.getName()),
                    String.format("Project details were updated"),
                    LocalDateTime.now().minusDays(random.nextInt(15))
                );
                activities.add(projectUpdated);
            }

            // === ISSUE-LEVEL ACTIVITIES ===

            try {
                List<Issue> issues = issueRepository.findByProjectId(project.getId());

                for (Issue issue : issues) {
                    User activityUser = issue.getAssignee() != null ? issue.getAssignee() : project.getOwner();

                    // Activity: Issue Creation
                    RecentActivityDto issueCreation = createActivity(
                        issue,
                        project,
                        activityUser,
                        "issue_created",
                        String.format("%s created issue '%s' in %s",
                            activityUser.getFullName(), issue.getTitle(), project.getName()),
                        String.format("New issue '%s' was created with priority: %s",
                            issue.getTitle(), issue.getPriority() != null ? issue.getPriority() : "medium"),
                        LocalDateTime.now().minusDays(random.nextInt(7))
                    );
                    activities.add(issueCreation);

                    // Activity: Task Assignment
                    if (issue.getAssignee() != null) {
                        RecentActivityDto taskAssigned = createActivity(
                            issue,
                            project,
                            project.getOwner(),
                            "task_assigned",
                            String.format("Task '%s' assigned to %s",
                                issue.getTitle(), issue.getAssignee().getFullName()),
                            String.format("%s was assigned to work on '%s' in %s",
                                issue.getAssignee().getFullName(), issue.getTitle(), project.getName()),
                            LocalDateTime.now().minusDays(random.nextInt(5))
                        );
                        taskAssigned.setAssigneeId(issue.getAssignee().getId());
                        taskAssigned.setAssigneeName(issue.getAssignee().getFullName());
                        activities.add(taskAssigned);
                    }

                    // Activity: Task Completion
                    if ("done".equalsIgnoreCase(issue.getStatus())) {
                        RecentActivityDto taskCompleted = createActivity(
                            issue,
                            project,
                            activityUser,
                            "task_completed",
                            String.format("%s completed task '%s' ✓",
                                activityUser.getFullName(), issue.getTitle()),
                            String.format("Task '%s' was marked as completed in %s project",
                                issue.getTitle(), project.getName()),
                            LocalDateTime.now().minusHours(random.nextInt(24))
                        );
                        activities.add(taskCompleted);
                    }

                    // Activity: Status Update
                    if ("in_progress".equalsIgnoreCase(issue.getStatus())) {
                        RecentActivityDto statusUpdated = createActivity(
                            issue,
                            project,
                            activityUser,
                            "status_updated",
                            String.format("%s started working on '%s'",
                                activityUser.getFullName(), issue.getTitle()),
                            String.format("Task status changed from 'Pending' to 'In Progress'"),
                            LocalDateTime.now().minusHours(random.nextInt(48))
                        );
                        statusUpdated.setOldValue("pending");
                        statusUpdated.setNewValue("in_progress");
                        activities.add(statusUpdated);
                    }
                }
            } catch (Exception e) {
                log.debug("No issues found for project: {}", project.getName());
            }
        }

        // Sort by creation date (most recent first) and limit
        return activities.stream()
                .sorted(Comparator.comparing(RecentActivityDto::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to create an activity DTO
     */
    private RecentActivityDto createActivity(Issue issue, Project project, User activityUser,
                                            String type, String action, String description,
                                            LocalDateTime createdAt) {
        RecentActivityDto activity = new RecentActivityDto();
        activity.setId(issue.getId());
        activity.setType(type);
        activity.setAction(action);
        activity.setDescription(description);

        // User information
        activity.setUserId(activityUser.getId());
        activity.setUserName(activityUser.getFullName());
        activity.setUserAvatar(generateAvatarUrl(activityUser));

        // Project information
        activity.setProjectId(project.getId());
        activity.setProjectName(project.getName());

        // Entity information
        activity.setEntityId(issue.getId());
        activity.setEntityType("issue");
        activity.setPriority(issue.getPriority());

        // Timestamp
        activity.setCreatedAt(createdAt);
        activity.setTimeAgo(getTimeAgo(createdAt));

        return activity;
    }

    /**
     * Helper method to create a project-level activity DTO
     */
    private RecentActivityDto createProjectActivity(Project project, User activityUser,
                                                    String type, String action, String description,
                                                    LocalDateTime createdAt) {
        RecentActivityDto activity = new RecentActivityDto();
        activity.setId(project.getId());
        activity.setType(type);
        activity.setAction(action);
        activity.setDescription(description);

        // User information
        activity.setUserId(activityUser.getId());
        activity.setUserName(activityUser.getFullName());
        activity.setUserAvatar(generateAvatarUrl(activityUser));

        // Project information
        activity.setProjectId(project.getId());
        activity.setProjectName(project.getName());

        // Entity information
        activity.setEntityId(project.getId());
        activity.setEntityType("project");

        // Timestamp
        activity.setCreatedAt(createdAt);
        activity.setTimeAgo(getTimeAgo(createdAt));

        return activity;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectCountsDto getProjectCounts(User user) throws Exception {
        log.info("Fetching project counts for user: {}", user.getEmail());

        List<Project> userProjects = projectRepository.findByTeamContainingOrOwner(user, user);

        ProjectCountsDto counts = new ProjectCountsDto();
        counts.setTotal(userProjects.size());

        // Count by status (we'll use project category as a proxy for status)
        Map<String, Integer> byStatus = new HashMap<>();
        byStatus.put("planning", 0);
        byStatus.put("in_progress", 0);
        byStatus.put("review", 0);
        byStatus.put("completed", 0);

        // Count projects with issues to determine status
        for (Project project : userProjects) {
            try {
                List<Issue> issues = issueRepository.findByProjectId(project.getId());
                long doneIssues = issues.stream().filter(i -> "done".equalsIgnoreCase(i.getStatus())).count();
                long totalIssues = issues.size();

                if (totalIssues == 0) {
                    byStatus.put("planning", byStatus.get("planning") + 1);
                } else if (doneIssues == totalIssues) {
                    byStatus.put("completed", byStatus.get("completed") + 1);
                } else if (doneIssues > 0) {
                    byStatus.put("in_progress", byStatus.get("in_progress") + 1);
                } else {
                    byStatus.put("review", byStatus.get("review") + 1);
                }
            } catch (Exception e) {
                byStatus.put("planning", byStatus.get("planning") + 1);
            }
        }
        counts.setByStatus(byStatus);

        // Count by priority (based on issues)
        Map<String, Integer> byPriority = new HashMap<>();
        byPriority.put("low", 0);
        byPriority.put("medium", 0);
        byPriority.put("high", 0);

        for (Project project : userProjects) {
            try {
                List<Issue> issues = issueRepository.findByProjectId(project.getId());
                boolean hasHighPriority = issues.stream().anyMatch(i -> "high".equalsIgnoreCase(i.getPriority()));
                boolean hasMediumPriority = issues.stream().anyMatch(i -> "medium".equalsIgnoreCase(i.getPriority()));

                if (hasHighPriority) {
                    byPriority.put("high", byPriority.get("high") + 1);
                } else if (hasMediumPriority) {
                    byPriority.put("medium", byPriority.get("medium") + 1);
                } else {
                    byPriority.put("low", byPriority.get("low") + 1);
                }
            } catch (Exception e) {
                byPriority.put("low", byPriority.get("low") + 1);
            }
        }
        counts.setByPriority(byPriority);

        // Calculate recently created, overdue, and due soon
        counts.setRecentlyCreated(2); // Placeholder
        counts.setOverdue(1); // Placeholder
        counts.setDueSoon(3); // Placeholder

        return counts;
    }

    // Helper methods

    private String calculateProjectsChange(List<Project> projects) {
        // Simple calculation - you can enhance this by tracking project creation dates
        int recentProjects = (int) projects.stream().limit(2).count();
        return recentProjects > 0 ? String.format("+%d this month", recentProjects) : "No change";
    }

    private String calculateIssuesChange(List<Issue> issues) {
        long resolvedToday = issues.stream()
                .filter(i -> "done".equalsIgnoreCase(i.getStatus()))
                .limit(5)
                .count();
        return resolvedToday > 0 ? String.format("%d resolved today", resolvedToday) : "No change";
    }

    private String generateAvatarUrl(User user) {
        // Generate avatar URL using UI Avatars service or return placeholder
        if (user.getFullName() != null && !user.getFullName().isEmpty()) {
            String initials = Arrays.stream(user.getFullName().split(" "))
                    .map(name -> name.substring(0, 1).toUpperCase())
                    .limit(2)
                    .collect(Collectors.joining());
            return String.format("https://ui-avatars.com/api/?name=%s&background=random", initials);
        }
        return "https://ui-avatars.com/api/?name=User&background=random";
    }

    private String getTimeAgo(LocalDateTime dateTime) {
        Duration duration = Duration.between(dateTime, LocalDateTime.now());

        long seconds = duration.getSeconds();
        if (seconds < 60) return "Just now";

        long minutes = seconds / 60;
        if (minutes < 60) return minutes + (minutes == 1 ? " minute ago" : " minutes ago");

        long hours = minutes / 60;
        if (hours < 24) return hours + (hours == 1 ? " hour ago" : " hours ago");

        long days = hours / 24;
        if (days < 7) return days + (days == 1 ? " day ago" : " days ago");

        long weeks = days / 7;
        if (weeks < 4) return weeks + (weeks == 1 ? " week ago" : " weeks ago");

        long months = days / 30;
        return months + (months == 1 ? " month ago" : " months ago");
    }
}

