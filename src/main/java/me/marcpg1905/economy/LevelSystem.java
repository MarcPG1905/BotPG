package me.marcpg1905.economy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import me.marcpg1905.BotPG;
import me.marcpg1905.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class LevelSystem extends ListenerAdapter {
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final Random RANDOM = new Random();
    private final HashMap<String, Integer> level = new HashMap<>();
    private final HashMap<String, Integer> levelExp = new HashMap<>();
    private final HashMap<String, Long> lastMessageTime = new HashMap<>();

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Guild guild = Objects.requireNonNull(BotPG.jda.getGuildById(1052224242965545103L));

        User author = event.getAuthor();

        if (!event.isFromGuild() || author.isBot() || author.isSystem() || event.isWebhookMessage()) return;

        int messageLength = Math.min(event.getMessage().getContentDisplay().length(), 15);
        if (messageLength <= 1) return;
        int exp = RANDOM.nextInt(messageLength, messageLength * 2);

        List<Message> messages = event.getChannel().getHistory().retrievePast(10).complete();
        ArrayList<String> messageContents = new ArrayList<>();
        for (Message message : messages) {
            if (message.getAuthor() == author) {
                String content = message.getContentDisplay();
                if (messageContents.contains(content)) exp /= 5;
                messageContents.add(content);
            }
        }

        List<Role> roles = Objects.requireNonNull(event.getMember()).getRoles();

        Role donatorRole = guild.getRolesByName("Donator", true).get(0);
        if (roles.contains(donatorRole)) exp *= 1.2;
        if (roles.contains(guild.getBoostRole())) exp *= 1.2;

        String id = author.getId();
        if (levelExp.containsKey(id)) {
            if (lastMessageTime.containsKey(id)) {
                int time = roles.contains(donatorRole) ? 10_000 : 15_000;
                if (System.currentTimeMillis() - lastMessageTime.get(id) >= time) {
                    updateLevelExp(id, levelExp.get(id) + exp);
                    BotPG.LOGGER.auditLog(String.format("Gave %s %d exp", author.getEffectiveName(), exp));
                    lastMessageTime.put(id, System.currentTimeMillis());
                }
            } else {
                lastMessageTime.put(id, System.currentTimeMillis());
            }
        } else {
            try {
                level.putAll(MAPPER.readValue(new File("data/level.json"), new TypeReference<>(){}));
                levelExp.putAll(MAPPER.readValue(new File("data/levelExp.json"), new TypeReference<>(){}));

                updateLevel(id, level.getOrDefault(id, 0));
                updateLevelExp(id, levelExp.getOrDefault(id, exp));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        int level = this.level.get(id);
        if (levelExp.get(id) < 7 * (level * level + 50)) return;

        level += 1;

        updateLevelExp(id, 0);
        updateLevel(id, level);

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("**🎉Congrats!🎉**")
                .setDescription("You have just reached level **" + level + "**!");
        event.getMessage().replyEmbeds(embed.build()).queue();

        if (level == 5) {
            guild.addRoleToMember(event.getMember(), guild.getRolesByName("Level 5+", true).get(0)).queue();
        } else if (level == 10) {
            guild.removeRoleFromMember(event.getMember(), guild.getRolesByName("Level 5+", true).get(0)).queue();
            guild.addRoleToMember(event.getMember(), guild.getRolesByName("Level 10+", true).get(0)).queue();
        } else if (level % 10 == 0) {
            guild.removeRoleFromMember(event.getMember(), guild.getRolesByName("Level " + (level - 10) + "+", true).get(0)).queue();
            guild.addRoleToMember(event.getMember(), guild.getRolesByName("Level " + (level) + "+", true).get(0)).queue();
        }
    }

    public void updateLevel(String id, int lvl) {
        level.put(id, lvl);
        try {
            MAPPER.writeValue(new File("data/level.json"), level);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLevelExp(String id, int xp) {
        levelExp.put(id, xp);
        try {
            MAPPER.writeValue(new File("data/levelExp.json"), levelExp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
