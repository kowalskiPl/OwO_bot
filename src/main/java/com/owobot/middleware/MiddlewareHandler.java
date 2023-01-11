package com.owobot.middleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class MiddlewareHandler {
    private Map<String, Middleware> registeredMiddlewares;
    private static final Logger log = LoggerFactory.getLogger(MiddlewareStack.class);

    public MiddlewareHandler() {
        registeredMiddlewares = new LinkedHashMap<>();
    }

    public void registerMiddleware(String name, Middleware middleware) {
        if (registeredMiddlewares.containsKey(name)) {
            log.error(name + " has already been registered as a middleware");
        }

        registeredMiddlewares.put(name, middleware);
    }

    public Middleware getMiddleware(String name) {
        return registeredMiddlewares.getOrDefault(name.toLowerCase(), null);
    }

    public String getName(Class<? extends Middleware> clazz) {
        for (var middleware : registeredMiddlewares.entrySet()) {
            if (middleware.getValue().getClass().getSimpleName().equalsIgnoreCase(clazz.getSimpleName())){
                return middleware.getKey();
            }
        }
        return null;
    }
}
