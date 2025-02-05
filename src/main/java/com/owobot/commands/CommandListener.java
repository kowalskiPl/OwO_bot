package com.owobot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandListener {
    public boolean onCommand(Command command);
    public boolean onSlashCommand(SlashCommandInteractionEvent event);
    public void shutdown();
}
