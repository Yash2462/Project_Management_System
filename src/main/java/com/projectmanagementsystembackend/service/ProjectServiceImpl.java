package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.exception.ChatNotFound;
import com.projectmanagementsystembackend.exception.ProjectNotFoundException;
import com.projectmanagementsystembackend.model.Chat;
import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.ChatRepository;
import com.projectmanagementsystembackend.repository.ProjectRepository;
import com.projectmanagementsystembackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
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
        return savedProject;
    }

    @Override
    public List<Project> getProjectByTeam(User user, String category, String tag) throws Exception {
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
    public Project getProjectById(Long projectId) throws Exception {
        Optional<Project> project = projectRepository.findById(projectId);

        if (project.isEmpty()){
            throw new ProjectNotFoundException("Not found project with projectId :"+projectId);
        }
        return project.get();
    }

    @Override
    public void deleteProject(Long projectId, Long userId) throws Exception {

        //getProjectById(projectId);
//        userService.findUserById(userId);
        projectRepository.deleteById(projectId);
    }

    @Override
    public Project updateProject(Project updateProject, Long id) throws Exception {
        Project existingProject = getProjectById(id);
        existingProject.setName(updateProject.getName());
        existingProject.setDescription(updateProject.getDescription());
        existingProject.setTags(updateProject.getTags());

        return projectRepository.save(existingProject);
    }

    @Override
    public void addUserToProject(Long projectId, Long userId) throws Exception {
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

        Project project = getProjectById(projectId);
        User user = userService.findUserById(userId);
        if (!project.getTeam().contains(user)){
            project.getChat().getUsers().remove(user);
            project.getTeam().remove(user);
        }
        projectRepository.save(project);
    }

    @Override
    public Chat getChatByProjectId(Long projectId) throws Exception {
        Project project = getProjectById(projectId);
        Chat chat = project.getChat();
        if (chat == null){
            throw new ChatNotFound("Not Found chat for given project"+project.getName());
        }
        return chat;
    }

    @Override
    public List<Project> searchProjects(String keyword, User user) throws Exception {

        return projectRepository.findByNameContainingAndTeamContains(keyword,user);
    }
}
