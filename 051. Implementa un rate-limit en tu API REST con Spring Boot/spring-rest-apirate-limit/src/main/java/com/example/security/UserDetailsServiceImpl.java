package com.example.security;

import com.example.service.UserEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/*
Let Spring Security know how to extract the database user
to perform authentication
 */
@Service
@Primary
public class UserDetailsServiceImpl implements UserDetailsService {

    Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UserEntityService userService;

    public UserDetailsServiceImpl(UserEntityService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loadUserByUsername {}", username);
        return this.userService.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username + " not found")
        );
    }
}
