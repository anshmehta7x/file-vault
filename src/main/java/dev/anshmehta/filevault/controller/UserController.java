package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.UserAuthRequest;
import dev.anshmehta.filevault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserAuthRequest userAuthRequest) {
        boolean result = userService.registerUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
        if (result) {
            return ResponseEntity.ok("User registered successfully");
        } else {
            return ResponseEntity.status(400).body("User registration failed");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserAuthRequest userAuthRequest) {
        boolean result = userService.authenticateUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
        if(result){
            return ResponseEntity.ok("User logged successfully");
        }
        else{
            return ResponseEntity.status(400).body("User login failed");
        }
    }



}
