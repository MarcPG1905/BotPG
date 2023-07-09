package me.marcpg1905;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hectus.color.McColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class DevLog extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("devlog")) {
            EmbedBuilder embed = new EmbedBuilder();

            switch (Objects.requireNonNull(event.getSubcommandName())) {
                case "test" -> {
                    String tested = Objects.requireNonNull(event.getOption("tested")).getAsString();
                    boolean success = Objects.requireNonNull(event.getOption("success")).getAsBoolean();

                    embed.setColor(McColor.LIME.awtColor())
                            .setTitle("Playtest")
                            .setDescription("A playtest happened.")
                            .addField("Tested Feature(s)", tested, false)
                            .addField("Success?", success ? "Yes" : "No", true);

                    if (event.getOption("with") != null)
                        embed.addField("With", Objects.requireNonNull(event.getOption("with")).getAsUser().getAsMention(), true);

                    if (event.getOption("bugs") != null)
                        embed.addField("Bugs & Issues", Objects.requireNonNull(event.getOption("bugs")).getAsString(), false);
                }
                case "turn" -> {
                    String type = Objects.requireNonNull(event.getOption("type")).getAsString();
                    String name = Objects.requireNonNull(event.getOption("name")).getAsString();

                    embed.setColor(McColor.GRAY.awtColor()).setTitle(name);

                    switch (type) {
                        case "add" -> embed.setDescription(name + " was added and should be fully functional now.");
                        case "modify" -> {
                            embed.setDescription(name + " was modified/changed.")
                                    .addField("Modification", Objects.requireNonNull(event.getOption("modification")).getAsString(), false);
                        }
                        case "cleanup" -> embed.setDescription(name + "'s code was cleaned up to be better.");
                        case "remove" -> embed.setColor(Color.PINK).setDescription(name + " was removed");
                    }
                }
                case "feature" -> {
                    String type = Objects.requireNonNull(event.getOption("type")).getAsString();
                    String name = Objects.requireNonNull(event.getOption("name")).getAsString();
                    String description = Objects.requireNonNull(event.getOption("description")).getAsString();
                    String clazz = Objects.requireNonNull(event.getOption("class")).getAsString();

                    embed.setColor(McColor.BLUE.awtColor())
                            .addField("Description", description, false)
                            .addField("Class", clazz, true);

                    switch (type) {
                        case "add" -> embed.setDescription(name + " was added and should be fully functional now.");
                        case "modify" -> {
                            embed.setDescription(name + " was modified/changed.")
                                    .addField("Modification", Objects.requireNonNull(event.getOption("modification")).getAsString(), false);
                        }
                        case "cleanup" -> embed.setDescription(name + "'s code was cleaned up to be better.");
                        case "remove" -> embed.setColor(Color.PINK).setDescription(name + " was removed");
                    }
                }
                case "fix" -> {
                    String feature = Objects.requireNonNull(event.getOption("feature")).getAsString();
                    boolean success = Objects.requireNonNull(event.getOption("success")).getAsBoolean();

                    embed.setColor(McColor.GOLD.awtColor())
                            .setTitle("Bug/Issue Fix")
                            .setDescription("Some Bug or Issue got fixed.")
                            .addField("Fixed Feature", feature, true)
                            .addField("Success?", success ? "Yes" : "No", true);
                }
                case "other" -> {
                    String title = Objects.requireNonNull(event.getOption("title")).getAsString();
                    String description = Objects.requireNonNull(event.getOption("description")).getAsString();

                    embed.setColor(McColor.WHITE.awtColor())
                            .setTitle(title)
                            .setDescription(description);
                }
            }

            event.reply("<@&1096043615735787610>").setEmbeds(embed.build()).queue();
        }
    }
}
