package me.marcpg1905.moderation;

import me.marcpg1905.BotPG;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ServerBanPreventer extends ListenerAdapter {
    @Override
    public void onMessageUpdate(@NotNull MessageUpdateEvent event) {
        if (event.isFromGuild()) {
            Message msg = event.getMessage();
            if (msg.getContentRaw().contains("https://") || msg.getContentRaw().contains("http://")){
                BotPG.LOGGER.info(msg.getAuthor().getEffectiveName() + " edited a message containing a link! The message was deleted.");
                msg.reply("Due to server security reasons, you cannot edit messages with links!").queue();
                msg.delete().queue();
            }
        }
    }
}
