package com.projectmanagementsystembackend.service;

import com.projectmanagementsystembackend.config.JwtProvider;
import com.projectmanagementsystembackend.exception.UserNotFoundException;
import com.projectmanagementsystembackend.model.User;
import com.projectmanagementsystembackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
       //below method is extracts email from jwt token
        String email = JwtProvider.getEmailFromToken(jwt);
        return findUserByEmail(email);
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null){
            throw new UserNotFoundException("Not found user with username :"+email);
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()){
            throw new UserNotFoundException("Not found user with id :"+userId);
        }

        return user.get();
    }

    @Override
    public User updateUsersProjectSize(User user, int number) throws Exception {
        User existingUser = userRepository.findByEmail(user.getEmail());
        existingUser.setProjectSize(number);
        return userRepository.save(existingUser);
    }
}
