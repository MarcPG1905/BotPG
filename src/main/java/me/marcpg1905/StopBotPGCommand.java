package me.marcpg1905;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class StopBotPGCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("stopbotpg")) {
            event.reply("Bye!").queue();
            BotPG.jda.shutdown();
            System.exit(0);
        }
    }
}
