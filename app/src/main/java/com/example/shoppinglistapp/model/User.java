package com.example.shoppinglistapp.model;

// User model class
public class User {
    public String userId;
    public String name;
    public String phoneNumber;
    public String email;
    public String role;

    public User(String userId, String name, String phoneNumber, String email, String role) {
        this.userId = userId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }
}