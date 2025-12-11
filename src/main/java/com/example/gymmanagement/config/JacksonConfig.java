package com.example.gymmanagement.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {

    private final ObjectMapper mapper;

    public JacksonConfig() {
        this.mapper = new ObjectMapper();
        // Ενεργοποίηση υποστήριξης για LocalDate, LocalDateTime
        this.mapper.registerModule(new JavaTimeModule());
        // Να γράφει τις ημερομηνίες ως "2023-01-01" και όχι ως array [2023, 1, 1]
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}