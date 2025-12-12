package com.example.gymmanagement.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // ΣΩΣΤΟ origin — ΧΩΡΙΣ slash!
        response.setHeader("Access-Control-Allow-Origin", "https://gym-system-app-frontend.netlify.app");
        response.setHeader("Vary", "Origin");

        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.setHeader("Access-Control-Max-Age", "3600");

        // Αν είναι OPTIONS → ΤΕΡΜΑ, μην πάει παρακάτω
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Αλλιώς συνεχίζει στο AuthenticationFilter και μετά στους πόρους
        chain.doFilter(req, res);
    }
}
