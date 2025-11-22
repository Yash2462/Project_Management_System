package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.config.JwtProvider;
import com.projectmanagementsystembackend.exception.UserNotFoundException;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User findUserProfileByJwt(String jwt) throws Exception {
       //below method is extracts email from jwt token
        String email = JwtProvider.getEmailFromToken(jwt);
        return findUserByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) throws Exception {
        log.debug("Finding user by email: {}", email);
        User user = userRepository.findByEmail(email);

        if (user == null){
            throw new UserNotFoundException("Not found user with username :"+email);
        }
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long userId) throws Exception {
        log.debug("Finding user by id: {}", userId);
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            throw new UserNotFoundException("Not found user with id :"+userId);
        }

        return user.get();
    }

    @Override
    public User updateUsersProjectSize(User user, int number) throws Exception {
        log.info("Updating project size for user: {}", user.getEmail());
        User existingUser = userRepository.findByEmail(user.getEmail());
        existingUser.setProjectSize(number);
        return userRepository.save(existingUser);
    }

    @Override
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String email = JwtProvider.getCurrentUser();
        return userRepository.findByEmail(email);
    }
}
