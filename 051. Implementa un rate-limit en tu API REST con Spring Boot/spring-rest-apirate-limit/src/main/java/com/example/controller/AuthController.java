package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.LoginResponse;
import com.example.dto.UserRegister;
import com.example.model.UserEntity;
import com.example.security.JwtTokenProvider;
import com.example.service.UserEntityService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final UserEntityService userService;
    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(UserEntityService userService, AuthenticationManager authManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authManager = authManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/auth/register")
    public UserEntity save(@RequestBody UserRegister userDTO){
        return this.userService.save(userDTO);
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@RequestBody LoginRequest loginDTO){
        Authentication authDTO = new UsernamePasswordAuthenticationToken(loginDTO.username(), loginDTO.password());

        Authentication authentication = this.authManager.authenticate(authDTO);
        UserEntity user = (UserEntity) authentication.getPrincipal();

        String token = this.jwtTokenProvider.generateToken(authentication);

        return new LoginResponse(user.getUsername(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList(),
                token);
    }
}
