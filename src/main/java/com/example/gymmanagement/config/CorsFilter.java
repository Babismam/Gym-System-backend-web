package com.example.gymmanagement.config;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching // <--- ΠΟΛΥ ΣΗΜΑΝΤΙΚΟ ΓΙΑ ΝΑ ΜΗΝ ΤΡΩΣ 405
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        if (isPreflightRequest(request)) {
            request.abortWith(Response.ok().build());
            return;
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        if (request.getHeaderString("Origin") == null) return;
        response.getHeaders().putSingle("Access-Control-Allow-Origin", "https://gym-system-app-frontend.netlify.app");
        response.getHeaders().putSingle("Access-Control-Allow-Credentials", "true");
        response.getHeaders().putSingle("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.getHeaders().putSingle("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, X-Requested-With");
    }

    private boolean isPreflightRequest(ContainerRequestContext request) {
        return request.getHeaderString("Origin") != null
                && request.getMethod().equalsIgnoreCase("OPTIONS");
    }
}