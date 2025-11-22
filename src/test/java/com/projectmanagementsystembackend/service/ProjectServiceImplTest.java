package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.exception.ProjectNotFoundException;
import com.projectmanagementsystembackend.model.Chat;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserService userService;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ProjectServiceImpl projectService;

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
        testProject.setOwner(testUser);
    }

    @Test
    void createProject_ShouldCreateProjectSuccessfully() throws Exception {
        // Arrange
        Chat mockChat = new Chat();
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(chatService.createChat(any(Chat.class))).thenReturn(mockChat);

        // Act
        Project result = projectService.createProject(testProject, testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testProject.getName(), result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(chatService, times(1)).createChat(any(Chat.class));
    }

    @Test
    void getProjectById_WhenProjectExists_ShouldReturnProject() throws Exception {
        // Arrange
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // Act
        Project result = projectService.getProjectById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testProject.getId(), result.getId());
        assertEquals(testProject.getName(), result.getName());
    }

    @Test
    void getProjectById_WhenProjectNotExists_ShouldThrowException() {
        // Arrange
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(999L));
    }

    @Test
    void getProjectByTeam_ShouldReturnFilteredProjects() throws Exception {
        // Arrange
        Project project1 = new Project();
        project1.setName("Project 1");
        project1.setCategory("Development");

        Project project2 = new Project();
        project2.setName("Project 2");
        project2.setCategory("Testing");

        List<Project> projects = Arrays.asList(project1, project2);
        when(projectRepository.findByTeamContainingOrOwner(testUser, testUser)).thenReturn(projects);

        // Act
        List<Project> result = projectService.getProjectByTeam(testUser, "Development", null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Development", result.get(0).getCategory());
    }

    @Test
    void deleteProject_ShouldDeleteProjectSuccessfully() throws Exception {
        // Act
        projectService.deleteProject(1L, testUser.getId());

        // Assert
        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void updateProject_ShouldUpdateProjectSuccessfully() throws Exception {
        // Arrange
        Project updateData = new Project();
        updateData.setName("Updated Name");
        updateData.setDescription("Updated Description");

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        Project result = projectService.updateProject(updateData, 1L);

        // Assert
        assertNotNull(result);
        verify(projectRepository, times(1)).save(any(Project.class));
    }
}

