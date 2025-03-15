package com.owobot.core;

import com.owobot.OwoBot;
import com.owobot.utilities.Pair;
import com.owobot.utilities.Reflectional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SlashCommandsManager extends Reflectional {

    private Set<Command> loadedGlobalCommands = new LinkedHashSet<>();
    private final Set<SlashCommandData> desiredGlobalCommands = new LinkedHashSet<>();
    // we assume that every server gets the same set of commands
    private final Set<SlashCommandData> desiredGuildCommands = new LinkedHashSet<>();
    private final Logger log = LoggerFactory.getLogger(SlashCommandsManager.class);

    public SlashCommandsManager(OwoBot owoBot) {
        super(owoBot);

        owoBot.getModuleManager().getModules().forEach(module -> desiredGuildCommands.addAll(module.getGuildSlashCommands()));
        owoBot.getModuleManager().getModules().forEach(module -> desiredGlobalCommands.addAll(module.getGlobalSlashCommands()));

        try { //shit hangs here!!! sometimes?
            log.info("Loading Slash Commands Manager");
            loadGlobalSlashCommands();
        } catch (InterruptedException e) {
            log.error("Failed to load global slash commands");
        }
    }

    private List<Pair<List<Command>, Integer>> getGlobalSlashCommands() throws InterruptedException {
        var jdaList = owoBot.getShardManager().getShards();
        List<Pair<List<Command>, Integer>> allCommands = new ArrayList<>();
        log.info("Jda list size: {}", jdaList.size());
        for (var jda : jdaList) {
            log.info("Loading global commands");
            jda.awaitReady();
            log.info("Jda ready");
            var commands = jda.retrieveCommands().complete();
            log.info("Got commands");
            allCommands.add(new Pair<>(commands, commands.size()));
        }

        //validate command count per shard just in case

        return allCommands;
    }

    private void loadGlobalSlashCommands() throws InterruptedException {
        var globalCommandsPerShard = getGlobalSlashCommands();
        loadedGlobalCommands.clear();
        for (var commandList : globalCommandsPerShard) {
            loadedGlobalCommands.addAll(commandList.getKey());
        }
    }

    public synchronized void loadBotCommandsForGuild(Guild guild) {
        guild.updateCommands().addCommands(desiredGuildCommands).queue();
    }
}
