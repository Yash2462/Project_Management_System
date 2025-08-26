package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.model.Invitation;
import com.projectmanagementsystembackend.repository.InvitationRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class InvitationServiceImpl implements InvitationService{
    @Autowired
    private EmailService emailService;
    @Autowired
    private InvitationRepository invitationRepository;
    @Override
    public void sendInvitation(String email, Long projectId) throws MessagingException {
        String invitationToken = UUID.randomUUID().toString();
        Invitation invitation = new Invitation();
        invitation.setEmail(email);
        invitation.setProjectId(projectId);
        invitation.setToken(invitationToken);

        invitationRepository.save(invitation);
        String invitationLink = "http://localhost:5173/accept_invitation?token="+invitationToken;
        try {

            emailService.sendEmailWithToken(email,invitationLink);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Invitation acceptInvitation(String token, Long userId) throws Exception {
        Invitation invitation = invitationRepository.findByToken(token);
        if (invitation == null){
            throw new Exception("Invalid invitation token !");
        }
        invitation.setAccepted(true);
        invitationRepository.save(invitation);
        return invitation;
    }

    @Override
    public String getTokenByUserMail(String userEmail) {
        Invitation invitation = invitationRepository.findByEmail(userEmail);

        return invitation.getEmail();
    }

    @Override
    public void deleteToken(String token) {
       Invitation invitation = invitationRepository.findByToken(token);
       invitationRepository.delete(invitation);
    }
}
