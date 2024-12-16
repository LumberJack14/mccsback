package org.mccheckers.mccheckers_backend;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.ext.Provider;
import org.mccheckers.mccheckers_backend.Resources.*;
import org.mccheckers.mccheckers_backend.filters.CORSFilter;
import org.mccheckers.mccheckers_backend.filters.JwtFilter;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class CheckersApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CORSFilter.class);
        classes.add(AuthResource.class);
        classes.add(JwtFilter.class);
        classes.add(AdminResource.class);
        classes.add(HelloResource.class);
        classes.add(UserResource.class);
        classes.add(RequestResource.class);
        classes.add(MatchResource.class);
        return classes;
    }
}