package com.example.emanager.services;

public class UpdateUserProfileRequest {
    private String name;
    private String email;

    public UpdateUserProfileRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
