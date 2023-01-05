package com.owobot.async;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {
    private final String name;
    private int count;

    public NamedThreadFactory(String namePattern) {
        name = namePattern;
        count = 1;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        String newName = name + "-" + count++;
        return new Thread(r, newName);
    }
}
