package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.UserAuthRequest;
import dev.anshmehta.filevault.service.UserService;
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
    public boolean registerUser(@RequestBody UserAuthRequest userAuthRequest) {
        return userService.registerUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
    }

    @PostMapping("/login")
    public boolean loginUser(@RequestBody UserAuthRequest userAuthRequest) {
        return userService.authenticateUser(userAuthRequest.getUsername(), userAuthRequest.getPassword());
    }



}
