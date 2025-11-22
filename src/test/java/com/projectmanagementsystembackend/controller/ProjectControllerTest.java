package com.projectmanagementsystembackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.service.ProjectService;
import com.projectmanagementsystembackend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    private User testUser;
    private Project testProject;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFullName("Test User");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setCategory("Development");
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getProjects_ShouldReturnProjectList() throws Exception {
        // Arrange
        List<Project> projects = Arrays.asList(testProject);
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(projectService.getProjectByTeam(any(User.class), isNull(), isNull())).thenReturn(projects);

        // Act & Assert
        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Data Found Successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void getProjectById_ShouldReturnProject() throws Exception {
        // Arrange
        when(projectService.getProjectById(1L)).thenReturn(testProject);

        // Act & Assert
        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.name").value("Test Project"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void createProject_ShouldCreateSuccessfully() throws Exception {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(testUser);
        when(projectService.createProject(any(Project.class), any(User.class))).thenReturn(testProject);

        // Act & Assert
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testProject)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Data saved successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void deleteProject_ShouldDeleteSuccessfully() throws Exception {
        // Arrange
        when(userService.getCurrentUser()).thenReturn(testUser);

        // Act & Assert
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Project deleted successfully"));
    }
}

