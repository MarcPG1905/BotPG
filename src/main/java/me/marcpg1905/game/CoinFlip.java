package me.marcpg1905.game;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class CoinFlip extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("coinflip")) {
            event.reply(new Random().nextBoolean() ? "Heads!" : "Tails!").queue();
        }
    }
}
