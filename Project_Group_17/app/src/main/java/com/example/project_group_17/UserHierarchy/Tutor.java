package com.example.project_group_17.UserHierarchy;

import java.util.List;

public class Tutor {

    private final String firstName;
    private final String lastName;
    private final String phone;
    private final String degree;
    private final List<String> courses;

    private final String username;
    private final String email;
    private String passwordPlain;

    public Tutor(String firstName, String lastName, String phone, String degree, List<String> courses, String username, String email, String passwordPlain) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.degree = degree;
        this.courses = courses;
        this.username = username;
        this.email = email;
        this.passwordPlain = passwordPlain;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getDegree() {
        return degree;
    }

    public List<String> getCourses() {
        return courses;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordPlain() {
        return passwordPlain;
    }


}