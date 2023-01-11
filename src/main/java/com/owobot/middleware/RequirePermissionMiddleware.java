package com.owobot.middleware;

import com.owobot.OwoBot;
import com.owobot.commands.Command;
import com.owobot.core.Permissions;
import com.owobot.middleware.permission.PermissionCheck;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RequirePermissionMiddleware extends Middleware{
    public RequirePermissionMiddleware(OwoBot owoBot) {
        super(owoBot);
    }

    @Override
    public boolean handle(Command command, MiddlewareStack stack, String... args) {
        var message = command.getCommandMessage();
        if (!message.getMessageChannel().getType().isGuild()){
            return false;
        }

        if (args.length < 2){
            owoBot.getLog().warn("Error parsing middleware, invalid amount of arguments for command: " + command.getName());
            return false;
        }

        PermissionCheck permissionCheck = new PermissionCheck(command, args);
        if (!permissionCheck.checkPermission()){
            return false;
        }

        if (!permissionCheck.getMissingUserPermissions().isEmpty()){
            String sb = "User is missing following permissions: " +
                    permissionCheck.getMissingUserPermissions().stream().map(Permissions::getPermission).map(Permission::getName).collect(Collectors.joining("', '"));
            command.getCommandMessage().getMessageChannel().sendMessage(sb).queue(newMessage -> newMessage.delete().queueAfter(30, TimeUnit.SECONDS));
            return false;
        }

        if (!permissionCheck.getMissingBotPermissions().isEmpty()){
            String sb = "Bot is missing following permissions: " +
                    permissionCheck.getMissingBotPermissions().stream().map(Permissions::getPermission).map(Permission::getName).collect(Collectors.joining("', '"));
            command.getCommandMessage().getMessageChannel().sendMessage(sb).queue(newMessage -> newMessage.delete().queueAfter(30, TimeUnit.SECONDS));
            return false;
        }

        return permissionCheck.isEmpty() && stack.next();
    }
}
