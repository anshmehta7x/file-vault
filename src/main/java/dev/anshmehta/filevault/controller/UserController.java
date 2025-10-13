package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.UserAuthRequest;
import dev.anshmehta.filevault.dto.UserAuthResponse;
import dev.anshmehta.filevault.dto.UserListResponse;
import dev.anshmehta.filevault.service.UserService;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserAuthResponse> registerUser(@RequestBody UserAuthRequest userAuthRequest) {
        Pair<String,String> result = userService.registerUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
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
        Pair<String,String> result = userService.loginUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
        if (result != null) {
            UserAuthResponse response = new UserAuthResponse("User logged in successfully", result);
            return ResponseEntity.ok(response);
        } else {
            UserAuthResponse response = new UserAuthResponse("User login failed");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<UserListResponse>> getAllUsers() {
        try{
            List<UserListResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }


}
