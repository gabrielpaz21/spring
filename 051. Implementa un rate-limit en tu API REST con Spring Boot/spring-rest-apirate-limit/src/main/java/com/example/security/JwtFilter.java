package com.example.security;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
It is responsible for removing the token that arrives in the Authorization header
Call JwtProvider to validate it
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final JwtTokenExtractor tokenExtractor;
    private final UserDetailsService userService;

    public JwtFilter(JwtTokenProvider tokenProvider,
                     JwtTokenExtractor tokenExtractor,
                     UserDetailsService userService) {
        this.tokenProvider = tokenProvider;
        this.tokenExtractor = tokenExtractor;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = this.tokenExtractor.extractToken(request);

        if (this.tokenProvider.isValidToken(token)) {

            String username = this.tokenProvider.getUsernameFromToken(token);

            UserDetails user = this.userService.loadUserByUsername(username);

            Authentication auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);

    }

}
