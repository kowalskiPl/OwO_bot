package com.owobot;

import com.owobot.database.MongoDbContext;
import com.owobot.messagelisteners.MainMessageListener;
import com.owobot.messagelisteners.MusicListenerAdapter;
import com.owobot.utilities.ServiceContext;
import com.owobot.utilities.listener.BasicMessageHandler;
import com.owobot.utilities.listener.EventListenerStack;
import com.owobot.utilities.listener.MusicEmbedMessageSender;
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
    private ShardManager shardManager = null;

    public OwoBot() {
        OwoBot.owoBot = this;

        log.info("Setting up DB connection");
        var dbContext = new MongoDbContext(ServiceContext.getConfig().getConnectionString(), 3, ServiceContext.getConfig().getMongoDb());
        ServiceContext.provideDbConnectionPool(dbContext);

        log.info("Creating event listener stack");
        var listenerStack = new EventListenerStack(ServiceContext.getConfig().getEventThreadPoolSize());
        listenerStack.registerListener(new MusicEmbedMessageSender(), new BasicMessageHandler());
        ServiceContext.provideEventListenerStack(listenerStack);

        shardManager = createShardManager();
    }

    private ShardManager createShardManager(){
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.create(EnumSet.of(
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        ))
                .setToken(ServiceContext.getConfig().getDiscordToken())
                .setSessionController(new SessionControllerAdapter())
                .setActivity(Activity.playing("OwO-ing"))
                .setBulkDeleteSplittingEnabled(false)
                .setChunkingFilter(ChunkingFilter.NONE)
                .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                .setEnableShutdownHook(true)
                .setAutoReconnect(true)
                .setContextEnabled(true)
                .setShardsTotal(3);

        builder.addEventListeners(new MainMessageListener(), new MusicListenerAdapter());

        return builder.build();
    }

    public void shutdown(){
        shardManager.shutdown();
    }
}
