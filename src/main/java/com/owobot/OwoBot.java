package com.owobot;

import com.owobot.core.CommandCache;
import com.owobot.core.CommandListenerStack;
import com.owobot.core.EventListenerStack;
import com.owobot.core.ModuleManager;
import com.owobot.database.MongoDbContext;
import com.owobot.messagelisteners.MainMessageListener;
import com.owobot.modules.admin.AdminModule;
import com.owobot.modules.music.MusicEmbedMessageSender;
import com.owobot.modules.music.MusicModule;
import com.owobot.utilities.Config;
import com.owobot.utilities.listener.BasicMessageHandler;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.SessionControllerAdapter;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

public class OwoBot {
    protected static OwoBot owoBot;
    private static final Logger log = LoggerFactory.getLogger(OwoBot.class);
    private final ShardManager shardManager;
    private final Config config;
    private final MongoDbContext mongoDbContext;
    private final EventListenerStack eventListenerStack;
    private final ModuleManager moduleManager;
    private final CommandCache commandCache;

    private final CommandListenerStack commandListenerStack;

    public OwoBot(Config config) {
        OwoBot.owoBot = this;
        this.config = config;

        log.info("Setting up DB connection");
        mongoDbContext = new MongoDbContext(config.getConnectionString(), 3, config.getMongoDb());

        log.info("Creating event listener stack");
        eventListenerStack = new EventListenerStack(config.getEventThreadPoolSize());
        eventListenerStack.registerListener(new MusicEmbedMessageSender(), new BasicMessageHandler());

        log.info("Creating command cache");
        commandCache = new CommandCache(this);

        log.info("Creating module manager");
        moduleManager = new ModuleManager(this);

        commandListenerStack = new CommandListenerStack(config.getEventThreadPoolSize());

        log.info("Loading standard modules");
        moduleManager.loadModule(new MusicModule(owoBot));
        moduleManager.loadModule(new AdminModule(owoBot));


        shardManager = createShardManager();
    }

    private ShardManager createShardManager() {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT
                ))
                .setToken(config.getDiscordToken())
                .setSessionController(new SessionControllerAdapter())
                .setActivity(Activity.playing("OwO-ing"))
                .setBulkDeleteSplittingEnabled(false)
                .setChunkingFilter(ChunkingFilter.NONE)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                .setEnableShutdownHook(true)
                .setAutoReconnect(true)
                .setContextEnabled(true)
                .setShardsTotal(3);

        builder.addEventListeners(new MainMessageListener(this));

        return builder.build();
    }

    public void shutdown() {
        shardManager.shutdown();
        eventListenerStack.shutdown();
        mongoDbContext.shutdown();
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public Config getConfig() {
        return config;
    }

    public MongoDbContext getMongoDbContext() {
        return mongoDbContext;
    }

    public EventListenerStack getEventListenerStack() {
        return eventListenerStack;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandCache getCommandCache() {
        return commandCache;
    }

    public CommandListenerStack getCommandListenerStack() {
        return commandListenerStack;
    }
}
