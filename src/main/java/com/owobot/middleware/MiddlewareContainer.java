package com.owobot.middleware;

public class MiddlewareContainer {
    private final Middleware middlewareReference;
    private final String[] args;

    public MiddlewareContainer(Middleware middlewareReference) {
        this.middlewareReference = middlewareReference;
        args = new String[0];
    }

    public MiddlewareContainer(Middleware middlewareReference, String[] args) {
        this.middlewareReference = middlewareReference;
        this.args = args;
    }

    public Middleware getMiddlewareReference() {
        return middlewareReference;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isWithArgs() {
        return args.length > 0;
    }
}
