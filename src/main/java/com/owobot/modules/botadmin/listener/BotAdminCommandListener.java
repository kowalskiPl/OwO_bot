package com.owobot.modules.botadmin.listener;

import com.owobot.OwoBot;
import com.owobot.async.NamedThreadFactory;
import com.owobot.commands.Command;
import com.owobot.commands.CommandListener;
import com.owobot.modules.botadmin.BotAdminCommandParameters;
import com.owobot.modules.botadmin.commands.ShutdownCommand;
import com.owobot.modules.botadmin.commands.StopShutdownCommand;
import com.owobot.utilities.Reflectional;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotAdminCommandListener extends Reflectional implements CommandListener {

    private final AtomicBoolean shutdownInitiated = new AtomicBoolean(false);
    private Future<?> shutdownHook;
    private final ScheduledExecutorService scheduledExecutorService;

    public BotAdminCommandListener(OwoBot owoBot) {
        super(owoBot);
        var factory = new NamedThreadFactory("Shutdown-Worker");
        scheduledExecutorService = Executors.newScheduledThreadPool(1, factory);
    }

    @Override
    public boolean onCommand(Command command) {
        if (command instanceof ShutdownCommand shutdownCommand) {
            return handleShutdownCommand(shutdownCommand);
        }

        if (command instanceof StopShutdownCommand stopShutdownCommand){
            return handleStopShutdownCommand(stopShutdownCommand);
        }
        return false;
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
    }

    private boolean handleShutdownCommand(ShutdownCommand command) {
        if (!shutdownInitiated.get()) {
            var timeToShutdown = Long.parseLong(command.getParameterMap().get(BotAdminCommandParameters.BOT_ADMIN_TIME_TO_SHUTDOWN.getName()));
            owoBot.acceptNewCommands(false);
            if (timeToShutdown < 10) {
                timeToShutdown = 10L;
            }
            command.getCommandMessage().getMessage().getChannel().sendMessage("Shutting down in " + timeToShutdown + " seconds").queue();
            shutdownInitiated.set(true);

            shutdownHook = scheduledExecutorService.schedule(() -> {
                owoBot.shutdown();
                System.exit(0);
            }, timeToShutdown, TimeUnit.SECONDS);

        } else {
            command.getCommandMessage().getMessage().getChannel().sendMessage("Shutdown has already been initiated!").queue();
        }
        return true;
    }

    private boolean handleShutdownButtonPress() {

        return true;
    }

    private boolean handleStopShutdownCommand(StopShutdownCommand command) {
        shutdownHook.cancel(false);
        shutdownHook = null;
        shutdownInitiated.set(false);
        owoBot.acceptNewCommands(true);
        command.getCommandMessage().getMessage().getChannel().sendMessage("Shutdown has been stopped, normal operation resumed").queue();
        return true;
    }
}
