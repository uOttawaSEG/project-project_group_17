package com.example.project_group_17.UserHierarchy;

import java.io.Serializable;

public class User implements Serializable {
    private String userType;
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String registrationStatus;

    public User() {}
    public User(String userType, String id, String first, String last, String email, String pass, String pn){
        this.userType = userType;
        this.id = id;
        this.firstName = first;
        this.lastName = last;
        this.email = email;
        this.password = pass;
        this.phoneNumber = pn;
        this.registrationStatus="pending";
    }

    public String getUserType(){
        return userType;
    }
    public String getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getEmail(){
        return email;
    }
    public String getPassword(){
        return password;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getRegistrationStatus(){
        return  registrationStatus;
    }
    public void setRegistrationStatus(String reg){
        if(!reg.equals("approved")||!reg.equals("rejected")){
            System.out.println("Invalid");
        }
        registrationStatus=reg;
    }

}
