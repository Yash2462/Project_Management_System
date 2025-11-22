package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.exception.ChatNotFound;
import com.projectmanagementsystembackend.exception.ProjectNotFoundException;
import com.projectmanagementsystembackend.model.Chat;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.ChatRepository;
import com.projectmanagementsystembackend.repository.ProjectRepository;
import com.projectmanagementsystembackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatService chatService;

    @Override
    public Project createProject(Project project, User user) throws Exception {
        log.info("Creating new project: {} for user: {}", project.getName(), user.getEmail());
        Project createdProject = new Project();
        createdProject.setOwner(user);
        createdProject.setTags(project.getTags());
        createdProject.setName(project.getName());
        createdProject.setDescription(project.getDescription());
        createdProject.setCategory(project.getCategory());
        createdProject.getTeam().add(user);

        Project savedProject = projectRepository.save(createdProject);

        Chat chat = new Chat();
        chat.setProject(savedProject);

        Chat projectChat = chatService.createChat(chat);
        savedProject.setChat(projectChat);

        log.info("Project created successfully with id: {}", savedProject.getId());
        return savedProject;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getProjectByTeam(User user, String category, String tag) throws Exception {
        log.debug("Fetching projects for user: {}", user.getEmail());
        List<Project> projects = projectRepository.findByTeamContainingOrOwner(user,user);

        if (category != null){
            projects = projects.stream().filter(project -> project.getCategory().equals(category)).collect(Collectors.toList());
        }

        if (tag != null){
            projects = projects.stream().filter(project -> project.getTags().contains(tag)).collect(Collectors.toList());
        }

        return projects;
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) throws Exception {
        log.debug("Fetching project by id: {}", projectId);
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isEmpty()){
            throw new ProjectNotFoundException("Not found project with projectId :"+projectId);
        }
        return project.get();
    }

    @Override
    public void deleteProject(Long projectId, Long userId) throws Exception {
        log.info("Deleting project: {}", projectId);
        //getProjectById(projectId);
//        userService.findUserById(userId);
        projectRepository.deleteById(projectId);
        log.info("Project deleted successfully: {}", projectId);
    }

    @Override
    public Project updateProject(Project updateProject, Long id) throws Exception {
        log.info("Updating project: {}", id);
        Project existingProject = getProjectById(id);
        existingProject.setName(updateProject.getName());
        existingProject.setDescription(updateProject.getDescription());
        existingProject.setTags(updateProject.getTags());

        Project updated = projectRepository.save(existingProject);
        log.info("Project updated successfully: {}", id);
        return updated;
    }

    @Override
    public void addUserToProject(Long projectId, Long userId) throws Exception {
        log.info("Adding user {} to project {}", userId, projectId);
         Project project = getProjectById(projectId);
         User user = userService.findUserById(userId);
         if (!project.getTeam().contains(user)){
             project.getChat().getUsers().add(user);
             project.getTeam().add(user);
         }
         projectRepository.save(project);
    }

    @Override
    public void removeUserFromProject(Long projectId, Long userId) throws Exception {
        log.info("Removing user {} from project {}", userId, projectId);
        Project project = getProjectById(projectId);
        User user = userService.findUserById(userId);
        if (!project.getTeam().contains(user)){
            project.getChat().getUsers().remove(user);
            project.getTeam().remove(user);
        }
        projectRepository.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public Chat getChatByProjectId(Long projectId) throws Exception {
        Project project = getProjectById(projectId);
        Chat chat = project.getChat();
        if (chat == null){
            throw new ChatNotFound("Not Found chat for given project"+project.getName());
        }
        return chat;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> searchProjects(String keyword, User user) throws Exception {
        log.debug("Searching projects with keyword: {} for user: {}", keyword, user.getEmail());
        return projectRepository.findByNameContainingAndTeamContains(keyword,user);
    }
}
