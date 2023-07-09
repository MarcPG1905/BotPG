package me.marcpg1905.economy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.marcpg1905.BotPG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class StatsCommand extends ListenerAdapter {
    public static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("stats")) {
            User user = event.getUser();
            if (event.getOption("user") != null) {
                user = Objects.requireNonNull(event.getOption("user")).getAsUser();
                BotPG.LOGGER.auditLog(event.getUser().getEffectiveName() + " executed /stats " + user.getEffectiveName());
            } else {
                BotPG.LOGGER.auditLog(user.getEffectiveName() + " executed /stats");
            }

            try {
                String id = user.getId();
                var level = MAPPER.readTree(new File("data/level.json")).get(id);
                var levelExp = MAPPER.readTree(new File("data/levelExp.json")).get(id);
                var warns = MAPPER.readTree(new File("data/warns.json")).get(id);

                if (level != null) {
                    long requiredExp = 7L * (level.asLong() * level.asLong() + 50L);
                    EmbedBuilder embedBuilder = new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("**===== " + user.getEffectiveName() + "'s Stats =====**")
                            .addField("Level Exp:", levelExp + "/" + requiredExp, true)
                            .addField("Level:", String.valueOf(level), true)
                            .addField("Warns:", (warns == null ? "0" : warns) + "/8", true);
                    event.replyEmbeds(embedBuilder.build()).queue();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
