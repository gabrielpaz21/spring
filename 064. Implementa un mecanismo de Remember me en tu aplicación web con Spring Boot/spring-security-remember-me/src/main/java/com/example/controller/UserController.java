package com.example.controller;

import com.example.model.UserEntity;
import com.example.repository.UserEntityRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {

    private final UserEntityRepository repo;

    private final PasswordEncoder encoder;

    public UserController(UserEntityRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @GetMapping
    public String index(){
        return "index";
    }

    // log in
    @GetMapping("/login")
    public String login(){
        return "login";
    }

    // show a registration form
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new UserEntity());
        return "signup-form";
    }

    // process registration form
    @PostMapping("/process-register")
    public String processRegister(UserEntity user){

        repo.findByUsername(user.getUsername()).ifPresent(u -> {
            throw new RuntimeException("Username already exists");
        });

        repo.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new RuntimeException("Email already exists");
        });

        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);
        return "register-success";
    }

    // show users
    @GetMapping("/users")
    public String listUsers(Model model, Principal principal){
        model.addAttribute("username", principal.getName());
        model.addAttribute("listUsers", repo.findAll());
        return "users";
    }

}
