package com.owobot;

import net.dv8tion.jda.api.sharding.ShardManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OwoBot {
    protected static OwoBot owoBot;
    private static final Logger log = LoggerFactory.getLogger(OwoBot.class);
    private ShardManager shardManager;
}
