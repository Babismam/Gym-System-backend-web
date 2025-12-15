package com.example.gymmanagement.config;

import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@PreMatching // ΣΗΜΑΝΤΙΚΟ: Τρέχει πριν ψάξει για Resources (λύνει το πρόβλημα του OPTIONS 404)
public class CorsResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        // Αν είναι OPTIONS (Preflight), απαντάμε αμέσως ΟΚ για να μην ψάξει για endpoint και χτυπήσει 404
        if (isPreflightRequest(request)) {
            request.abortWith(Response.ok().build());
        }
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        // Αν δεν υπάρχουν ήδη headers, τους βάζουμε εμείς
        if (request.getHeaderString("Origin") == null) {
            return;
        }

        // Χρησιμοποιούμε putSingle για να αντικαταστήσουμε τυχόν υπάρχοντες headers και να αποφύγουμε διπλότυπα
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