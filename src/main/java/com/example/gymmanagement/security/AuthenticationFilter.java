package com.example.gymmanagement.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String SECRET_KEY = "mySuperSecretKeyThatIsAtLeast32Chars";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // === 1) ALLOW CORS PREFLIGHT ===
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return;
        }

        // === 2) PUBLIC ENDPOINTS (NO AUTH) ===
        if (path.startsWith("auth") ||
                path.contains("swagger") ||
                path.contains("openapi")) {
            return;
        }

        // === 3) PUBLIC GET RESOURCES ===
        if ("GET".equalsIgnoreCase(method)) {
            if (path.startsWith("trainers") ||
                    path.startsWith("schedule") ||
                    path.startsWith("opening-hours") ||
                    path.startsWith("programs")) {
                return;
            }
        }

        // === 4) JWT AUTHENTICATION ===

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing or invalid Authorization header")
                    .build());
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired token").build());
        }
    }
}
