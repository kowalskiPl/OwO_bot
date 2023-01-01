package com.owobot.commands;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.internal.requests.DeferredRestAction;

public class CommandMessage implements CommandContext {
    private final Guild guild;
    private final Member member;
    private final TextChannel textChannel;
    private final Message message;

    public CommandMessage(Message message) {
        this.message = message;

        this.guild = message.isFromGuild() ? message.getGuild() : null;
        this.member = message.isFromGuild() ? message.getMember() : null;
        this.textChannel = message.isFromGuild() ? message.getChannel().asTextChannel() : null;
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
    public User getAuthor() {
        return member.getUser();
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
}
