package com.example.gymmanagement.api;

import com.example.gymmanagement.resources.*;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(AttendanceResource.class);
        classes.add(AuthResource.class);
        classes.add(DashboardResource.class);
        classes.add(OpeningHoursResource.class);
        classes.add(ProgramResource.class);
        classes.add(ScheduleResource.class);
        classes.add(TrainerResource.class);
        classes.add(UserResource.class);

        return classes;
    }
}
