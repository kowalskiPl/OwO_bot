package com.owobot.core;

import com.owobot.async.NamedThreadFactory;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.botadmin.BotAdminModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandListenerStack {
    private static final Logger log = LoggerFactory.getLogger(CommandListenerStack.class);
    private final ExecutorService threadPool;
    private final Set<CommandListener> eventListeners;
    private boolean acceptNewNonAdminCommands;

    public CommandListenerStack(int poolSize) {
        var factory = new NamedThreadFactory("Command-Listener-Executor");
        threadPool = Executors.newFixedThreadPool(poolSize, factory);
        eventListeners = new LinkedHashSet<>();
        acceptNewNonAdminCommands = true;
    }

    public void shutdown() {
        eventListeners.forEach(CommandListener::shutdown);
        threadPool.shutdown();
    }

    public void registerListener(CommandListener CommandListener) {
        eventListeners.add(CommandListener);
    }

    public void registerListener(CommandListener... CommandListener) {
        eventListeners.addAll(List.of(CommandListener));
    }

    public void registerListener(Set<CommandListener> CommandListener){
        eventListeners.addAll(CommandListener);
    }

    public void onCommand(Command command) {
        Runnable task = () -> {
            boolean result = false;
            if (!acceptNewNonAdminCommands && !command.getParentModule().equals(BotAdminModule.class.getName())) {
                log.warn("Unhandled command: " + command.getName());
                return;
            }
            for (CommandListener listener : eventListeners) {
                result = listener.onCommand(command);
                if (result){
                    log.info("Handled command: " + command);
                    break;
                }
            }
            if (!result)
                log.warn("Unhandled command: " + command.getName());
        };
        threadPool.submit(task);
    }

    public void setAcceptNewNonAdminCommands(boolean acceptNewNonAdminCommands) {
        this.acceptNewNonAdminCommands = acceptNewNonAdminCommands;
    }
}
