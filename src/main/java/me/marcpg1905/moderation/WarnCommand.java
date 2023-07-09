package me.marcpg1905.moderation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.marcpg1905.BotPG;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class WarnCommand extends ListenerAdapter {
    private final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private HashMap<String, Integer> warnedUserWarns = new HashMap<>();

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("warn")) {
            User warner = event.getUser();
            Member warnedMember = Objects.requireNonNull(Objects.requireNonNull(event.getOption("user")).getAsMember());
            User warnedUser = warnedMember.getUser();
            String warnedUserID = warnedUser.getId();

            if (warnedUser.isBot() || warnedMember.hasPermission(Permission.ADMINISTRATOR)) {
                event.reply("You can't warn bots or users with admin permissions.").setEphemeral(true).queue();
                return;
            }

            String violation = Objects.requireNonNull(event.getOption("violation")).getAsString();

            WarnReason reason = WarnReason.OTHER;
            for (WarnReason tmpReason : WarnReason.values()) {
                if (violation.equals(tmpReason.value)) {
                    reason = tmpReason;
                }
            }

            var embed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle(warnedUser.getEffectiveName() + " got warned!")
                    .addField("Level:", String.valueOf(reason.level), true)
                    .addField("By:", warner.getEffectiveName(), true)
                    .addField("Reason:", reason.text, false).build();
            event.getChannel().sendMessageEmbeds(embed).queue();

            BotPG.LOGGER.auditLog("checking");
            if (warnedUserWarns.containsKey(warnedUserID)) {
                BotPG.LOGGER.auditLog("true");
                try {
                    updateUserWarns(warnedUserID, warnedUserWarns.get(warnedUserID) + reason.level);

                    if (warnedUserWarns.get(warnedUserID) >= 8) {
                        var banEmbed = new EmbedBuilder()
                                .setTitle("The Ban-Hammer has spoken!")
                                .setDescription(warnedUser.getAsMention() + " gets banned in **10s**, __say bye!__")
                                .addField("Reason:", "Too many warns (" + warnedUserWarns.get(warnedUserID) + "/8)", false).build();
                        event.getChannel().sendMessage(warnedUserID).setEmbeds(banEmbed).queue();

                        Thread.sleep(10000L);

                        warnedMember.ban(0, TimeUnit.SECONDS).queue();
                        event.getChannel().sendMessage(warnedUser.getEffectiveName() + " got banned.").queue();
                        BotPG.LOGGER.info("Banned user " + warnedUser.getEffectiveName() + " Reason: Too many warns.");

                        updateUserWarns(warnedUserID, 4);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                BotPG.LOGGER.auditLog("else");
                try {
                    warnedUserWarns = MAPPER.readValue(new File("data/warns.json"), new TypeReference<>() {});
                    updateUserWarns(warnedUserID, reason.level);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            BotPG.LOGGER.auditLog(String.format("%s executed /warn %s %s", warner.getEffectiveName(), warnedUser.getEffectiveName(), violation));
        }
    }

    private void updateUserWarns(String user, int warns) {
        warnedUserWarns.put(user, warns);
        try {
            MAPPER.writeValue(new File("data/warns.json"), warnedUserWarns);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
