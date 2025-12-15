package com.example.gymmanagement.config;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {

        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin",
                "https://gym-system-app-frontend.netlify.app"
        );
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials",
                "true"
        );
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
                "Authorization, Content-Type"
        );
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS"
        );
    }
}
