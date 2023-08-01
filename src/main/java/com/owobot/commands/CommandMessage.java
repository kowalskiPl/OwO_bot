package com.owobot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

import java.util.Objects;

public class CommandMessage implements CommandContext {
    private final Guild guild;
    private Member member;
    private User author;
    private final TextChannel textChannel;
    private final Message message;
    private final boolean guildMessage;

    public CommandMessage(Message message) {
        this.message = message;
        this.guildMessage = message.isFromGuild();

        if (this.guildMessage){
            this.member = message.getMember();
            this.author = Objects.requireNonNull(message.getMember()).getUser();
            this.textChannel = message.getChannel().asTextChannel();
            this.guild = message.getGuild();
        } else {
            this.author = message.getAuthor();
            this.textChannel = null;
            this.guild = null;
        }
    }

    public CommandMessage(ButtonInteractionEvent event) {
        this.message = event.getMessage();
        this.guild = event.isFromGuild() ? event.getGuild() : null;
        this.member = event.isFromGuild() ? event.getMember() : null;
        this.textChannel = event.isFromGuild() ? event.getChannel().asTextChannel() : null;
        this.guildMessage = event.isFromGuild();
    }

    public JDA getJDA() {
        return message.getJDA();
    }

    public AuditableRestAction<Void> delete() {
        return message.delete();
    }

    @Override
    public Guild getGuild() {
        return guild;
    }

    @Override
    public Member getMember() {
        return member;
    }

    @Override
    public User getUser() {
        return author;
    }

    @Override
    public TextChannel getTextChannel() {
        return textChannel;
    }

    @Override
    public MessageChannel getMessageChannel() {
        return message.getChannel();
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public boolean isGuildMessage() {
        return guildMessage;
    }
}
