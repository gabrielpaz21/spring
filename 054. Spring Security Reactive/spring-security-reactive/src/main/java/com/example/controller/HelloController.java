package com.example.controller;

import com.example.dto.Login;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
public class HelloController {

    // mandatory to be authenticated
    @GetMapping("/hello")
    public Mono<String> hello() {
        return Mono.just("Hello World");
    }

    // Access authenticated user
    @GetMapping("/checkuser1")
    public Mono<String> checkuser1() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(auth -> "Hello " + auth.getName());
    }

    @GetMapping("/checkuser2")
    public Mono<String> checkuser2(Mono<Authentication> auth) {
        return auth.map(a -> "Hello " + ((UserDetails) a.getPrincipal()).getUsername());
    }

    @GetMapping("/checkuser3")
    public Mono<String> checkuser3(Mono<Principal> principal) {
        return principal
                .map(Principal::getName)
                .map(name -> "Hello " + name);
    }

    // allowed without authentication
    @PostMapping("/login")
    public Mono<String> login(@RequestBody Login login) {
        System.out.println(login);
        return Mono.just("authenticated");
    }

}
