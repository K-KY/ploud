package com.java.ploud.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Profile("local")
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestDebugFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("=== RAW REQUEST ===");
        System.out.println("method = " + request.getMethod());
        System.out.println("requestURI = " + request.getRequestURI());
        System.out.println("servletPath = " + request.getServletPath());
        System.out.println("origin = " + request.getHeader("Origin"));
        System.out.println("host = " + request.getHeader("Host"));
        System.out.println("content-type = " + request.getHeader("Content-Type"));
        System.out.println("===================");

        filterChain.doFilter(request, response);
    }
}