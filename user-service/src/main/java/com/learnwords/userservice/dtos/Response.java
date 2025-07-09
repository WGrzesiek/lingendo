package com.learnwords.userservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



public abstract class Response {
    private String message;
    private String username;

    public Response(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public String getUsername() {
        return username;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
