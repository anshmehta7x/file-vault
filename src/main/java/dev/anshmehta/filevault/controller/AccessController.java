package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.UserListResponse;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.service.AccessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/access")
public class AccessController {

    private final AccessService accessService;
    public AccessController(AccessService accessService) {
     this.accessService = accessService;
    }

    @GetMapping("/list/{fileId}")
    public ResponseEntity<List<UserListResponse>> getAccessList(@PathVariable String fileId, @AuthenticationPrincipal User user) {
        try {
            List<User> accessList = accessService.getFileAccessList(fileId, user);
            List<UserListResponse> userListResponse = new ArrayList<>();
            for(User u : accessList) {
                userListResponse.add(new UserListResponse(u.getUserId(), u.getUsername()));
            }
            return ResponseEntity.ok(userListResponse);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/give/{fileId}")
    public ResponseEntity<List<UserListResponse>> giveAccess(@PathVariable String fileId, @RequestBody List<String> addAccessList, @AuthenticationPrincipal User user) {
        try {
            accessService.giveFileAccess(fileId, user, addAccessList);
            return getAccessList(fileId, user);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/revoke/{fileId}")
    public ResponseEntity<List<UserListResponse>> revokeAccess(@PathVariable String fileId, @RequestBody List<String> removeAccessList, @AuthenticationPrincipal User user) {
        try {
            accessService.revokeFileAccess(fileId, user, removeAccessList);
            return getAccessList(fileId, user);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}
