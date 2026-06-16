package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LoginResponseDto is a class used to hold both the authentication token and the user
 * information that's returned from the server to the client from a login endpoint.
 *
 */
public class LoginResponseDto {

    private String token;
    private User user;

    public LoginResponseDto(String token, User user) {
        this.token = token;
        this.user = user;
    }

    @JsonProperty("token")
    String getToken() {
        return token;
    }

    void setToken(String token) {
        this.token = token;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
