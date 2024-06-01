package com.example.config;

import com.example.model.UserDetailsAdapter;
import com.example.utils.SecurityUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class SecondFactorAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        handle(request, response, authentication);
        clearAuthenticationAttributes(request);

    }

    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException{

        String targetUrl = getTargetUrl(authentication);
        if (response.isCommitted())
            return;

        redirectStrategy.sendRedirect(request, response, targetUrl);

    }

    private String getTargetUrl(Authentication authentication) {
        UserDetailsAdapter user = (UserDetailsAdapter) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
//        UserDetailsAdapter user = (UserDetailsAdapter) authentication.getPrincipal();

        if (user.getAccount().getAuth2FA()) {
            return "/code";
        } else {
            SecurityUtils.setAuthentication();
            return "/home";
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
    }


}
