package dev.anshmehta.filevault.dto;
import org.springframework.data.util.Pair;

public class UserAuthResponse {
    private String message;
    private String userId;
    private String token;

    public UserAuthResponse(String message) {
        this.message = message;
    }

    public UserAuthResponse(String message, Pair<String,String> data) {
        this.message = message;
        this.token = data.getFirst();
        this.userId = data.getSecond();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
