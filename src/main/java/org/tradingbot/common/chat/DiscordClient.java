package org.tradingbot.common.chat;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordClient extends ChatClient {
    private static final AtomicBoolean DISCORD_OPEN = new AtomicBoolean();
    private final JDA jda;
    private final String channelName;


    public DiscordClient(String channelName, String token) throws DiscordException {
        this.channelName = channelName;
        DISCORD_OPEN.compareAndSet(false, true);
        if (!DISCORD_OPEN.get()) {
            throw new IllegalStateException("There is another discord opened");
        }
        try {
            jda = JDABuilder.createDefault(token).setAutoReconnect(true).build();
            jda.awaitReady();
        } catch (LoginException e) {
            throw new DiscordException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new DiscordException(e);
        }

    }

    @Override
    public void sendMessage(String message) {
        if (message.length() >= 2000) {
            var f = getTextChannel().sendFile(message.getBytes(StandardCharsets.UTF_8), "commandResult.txt");
            f.queue();
        } else {
            getTextChannel().sendMessage(message).queue();
        }
    }

    private TextChannel getTextChannel() {
        for (var channel : jda.getGuilds().get(0).getChannels()) {

            if (channelName.equals(channel.getName())) {
                return (TextChannel) channel;
            }
        }
        throw new IllegalStateException("Channel " + channelName + " not found");
    }

    @Override
    public void close() {
        super.close();
        jda.shutdownNow();
        DISCORD_OPEN.compareAndSet(true, false);
    }
}
