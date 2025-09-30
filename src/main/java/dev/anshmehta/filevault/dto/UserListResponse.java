package dev.anshmehta.filevault.dto;

public class UserListResponse {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String userId;
    private String username;

    
    public UserListResponse(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }
}
