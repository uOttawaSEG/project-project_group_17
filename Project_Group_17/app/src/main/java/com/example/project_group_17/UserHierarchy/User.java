package com.example.project_group_17.UserHierarchy;

public class User {
    private String userType;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    public User(String userType, String first, String last, String email, String pass, String pn){
        this.userType = userType;
        this.firstName = first;
        this.lastName = last;
        this.email = email;
        this.password = pass;
        this.phoneNumber = pn;
    }

    public String getUserType(){
        return userType;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
}
