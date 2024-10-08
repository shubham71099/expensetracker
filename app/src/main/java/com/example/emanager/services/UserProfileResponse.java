package com.example.emanager.services;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {
    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;


    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
