package dev.anshmehta.filevault.service;


import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean registerUser(String username, String password) {
        if(userRepository.existsByUsername(username)) {
            return false; // User already exists
        } else {
            User newUser = new User();
            newUser.setUsername(username);
            String hashedPassword = passwordEncoder.encode(password);
            newUser.setPasswordHash(hashedPassword);
            userRepository.save(newUser);
            return true;
        }
    }

    public boolean authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElse(false);
    }

}
