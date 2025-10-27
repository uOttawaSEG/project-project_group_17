package com.example.project_group_17.UserHierarchy;

import java.io.Serializable;

public class Admin {
    private final String username = "Admin";
    private final String password = "12345";
    private final String userType = "Admin";

    public Admin(){

    }
    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getUserType(){
        return userType;
    }
}
