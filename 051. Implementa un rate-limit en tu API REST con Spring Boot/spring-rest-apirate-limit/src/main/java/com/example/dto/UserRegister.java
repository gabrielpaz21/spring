package com.example.dto;

/*
DTO with the information necessary to register a new user in the database
{
    "username": "user1",
    "email": "user1@jwt.io",
    "password": "admin",
    "password2": "admin"
}
 */
public record UserRegister(String username, String email, String password, String password2) {
}