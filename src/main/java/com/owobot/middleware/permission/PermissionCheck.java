package com.owobot.middleware.permission;

import com.owobot.commands.Command;
import com.owobot.core.Permissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionCheck {
    private final String[] args;
    private final boolean isAdmin;
    private final Command command;
    private final PermissionType type;
    private final List<Permissions> missingBotPermissions = new ArrayList<>();
    private final List<Permissions> missingUserPermissions = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(PermissionCheck.class);

    public PermissionCheck(Command command, String[] args) {
        this.command = command;
        this.args = args;
        type = PermissionType.fromName(args[0]);
        isAdmin = command.getCommandMessage().getMember().hasPermission(Permissions.ADMINISTRATOR.getPermission());
    }

    public boolean checkPermission() {
        String[] permissions = Arrays.copyOfRange(args, 1, args.length);

        for (String permissionNode : permissions) {
            Permissions permission = Permissions.fromNode(permissionNode);

            if (permission == null) {
                log.warn(String.format("Invalid permission node given for the \"%s\" command: %s", command.getName(), permissionNode));
                return false;
            }

            if (!isAdmin && type.isCheckUser() && !command.getCommandMessage().getMember().hasPermission(permission.getPermission())) {
                missingUserPermissions.add(permission);
            }

            if (type.isCheckBot() && !command.getCommandMessage().getGuild().getSelfMember().hasPermission(permission.getPermission())) {
                missingBotPermissions.add(permission);
                continue;
            }

            if (type.isCheckBot() && !command.getCommandMessage().getGuild().getSelfMember().hasPermission(command.getCommandMessage().getTextChannel(), permission.getPermission())){
                missingBotPermissions.add(permission);
            }
        }
        return true;
    }

    public List<Permissions> getMissingBotPermissions() {
        return missingBotPermissions;
    }

    public List<Permissions> getMissingUserPermissions() {
        return missingUserPermissions;
    }

    public PermissionType getType() {
        return type;
    }

    public boolean isEmpty() {
        return missingBotPermissions.isEmpty() && missingUserPermissions.isEmpty();
    }
}
