package com.mixsz.workouttracker.infra.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginRateLimiter rateLimiter;

    public LoginRateLimitFilter(LoginRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        boolean isLoginRequest = "POST".equals(request.getMethod())
                && "/auth/login".equals(request.getRequestURI());

        if (isLoginRequest) {
            String ip = request.getRemoteAddr();
            if (!rateLimiter.isAllowed(ip)) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Muitas tentativas de login. Tente novamente em instantes.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}