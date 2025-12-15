package com.example.gymmanagement.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

// Αυτό το φίλτρο τρέχει σε ΚΑΘΕ αίτημα, πριν από οτιδήποτε άλλο
@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Επιτρέπουμε το Frontend
        res.setHeader("Access-Control-Allow-Origin", "https://gym-system-app-frontend.netlify.app");
        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        res.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, X-Requested-With");

        // Αν είναι έλεγχος (OPTIONS), απαντάμε ΟΚ και σταματάμε εδώ
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Συνεχίζουμε κανονικά
        chain.doFilter(request, response);
    }
}