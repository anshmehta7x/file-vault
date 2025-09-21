package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.UserAuthRequest;
import dev.anshmehta.filevault.dto.UserAuthResponse;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.service.UserService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<UserAuthResponse> registerUser(@RequestBody UserAuthRequest userAuthRequest) {
        String result = userService.registerUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
        if (result != null) {
            UserAuthResponse response = new UserAuthResponse("User registered successfully", result);
            return ResponseEntity.ok(response);
        } else {
            UserAuthResponse response = new UserAuthResponse("User registration failed");
            return ResponseEntity.badRequest().body(response);
        }
    }



    @PostMapping("/login")
    public ResponseEntity<UserAuthResponse> loginUser(@RequestBody UserAuthRequest userAuthRequest) {
        String token = userService.loginUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
        if (token != null) {
            UserAuthResponse response = new UserAuthResponse("User logged in successfully", token);
            return ResponseEntity.ok(response);
        } else {
            UserAuthResponse response = new UserAuthResponse("User login failed");
            return ResponseEntity.badRequest().body(response);
        }
    }




}
