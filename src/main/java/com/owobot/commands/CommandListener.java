package com.owobot.commands;

public interface CommandListener {
    public boolean onCommand(Command command);
    public void shutdown();
}
