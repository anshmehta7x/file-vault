package dev.anshmehta.filevault.service;


import dev.anshmehta.filevault.config.JwtUtil;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String registerUser(String username, String password) {
        if(userRepository.existsByUsername(username)) {
            return null; // User already exists
        } else {
            User newUser = new User();
            newUser.setUsername(username);
            String hashedPassword = passwordEncoder.encode(password);
            newUser.setPasswordHash(hashedPassword);
            userRepository.save(newUser);
            return jwtUtil.generateToken(newUser.getUserId());
        }
    }

    public boolean authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .map(user -> passwordEncoder.matches(password, user.getPasswordHash()))
                .orElse(false);
    }

    public String loginUser(String username, String password) {
        if (authenticateUser(username, password)) {
            return userRepository.findByUsername(username)
                    .map(user -> jwtUtil.generateToken(user.getUserId()))
                    .orElse(null);
        }
        return null;
    }



}
