package me.marcpg1905.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RockPaperScissors extends ListenerAdapter {
    String p1;

    int p1c = 0;

    String p2;

    int p2c = 0;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    boolean gameRunning = false;

    boolean gameStarted = false;

    Message gameMessage;

    EmbedBuilder gameEmbed = new EmbedBuilder();

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("rps"))
            if (!gameRunning) {
                gameRunning = true;
                p1 = event.getUser().getId();
                p2 = Objects.requireNonNull(event.getOption("user")).getAsUser().getId();

                gameEmbed.setColor(Color.GREEN)
                        .setTitle("Rock, Paper, Scissors!")
                        .setDescription("<@" + p1 + "> vs. <@" + p2 + ">")
                        .addField("Play", "<@" + p2 + ">, please click the button below to start the game!", false);

                event.reply("<@" + p2 + ">, <@" + p1 + "> challenged you to a round of Rock, Paper, Scissors!")
                        .setEmbeds(new MessageEmbed[] { gameEmbed.build() }).addActionRow(new ItemComponent[] {Button.success("ConfirmRPSGame", "Confirm")}).queue(reply -> reply.retrieveOriginal().queue());

                executorService.schedule(() -> {
                    if (!gameStarted) {
                        gameEmbed.setColor(Color.RED)
                                .setDescription("The game has ended, due to inactivity")
                                .clearFields();

                        gameMessage.editMessageEmbeds(gameEmbed.build()).setActionRow(Button.success("ConfirmRPSGame", "Confirm").asDisabled()).queue();
                        resetGame();
                    }
                }, 10L, TimeUnit.SECONDS);
            } else {
                event.reply("There is already a game running!").setEphemeral(true).queue();
            }
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String user = event.getUser().getId();

        if (user.equals(p1) || user.equals(p2)) {
            int choice;
            if (buttonId.equals("ConfirmRPSGame")) {
                if (user.equals(p2)) {
                    event.reply("Confirmed the game!\nGood luck!").setEphemeral(true).queue();
                    gameStarted = true;
                    gameEmbed.clearFields()
                            .setColor(Color.WHITE)
                            .addField("Play", "<@" + p1 + "> and <@" + p2 + ">. Please select your choice!", false);
                    event.getMessage().editMessageEmbeds(gameEmbed.build()).setActionRow(Button.secondary("RockPS", "Rock").withEmoji(Emoji.fromUnicode("U+1FAA8")),
                            Button.secondary("RPaperS", "Paper").withEmoji(Emoji.fromUnicode("U+1F4C4")),
                            Button.secondary("RPScissors", "Scissors").withEmoji(Emoji.fromUnicode("U+2702"))).queue();
                } else {
                    event.reply("You aren't the user that was challenged!").setEphemeral(true).queue();
                }
                return;
            }
            switch (buttonId) {
                case "RockPS" -> choice = 1;
                case "RPaperS" -> choice = 2;
                case "RPScissors" -> choice = 3;
                default -> {
                    return;
                }
            }
            if (user.equals(p1) && p1c == 0) {
                p1c = choice;
            } else if (user.equals(p2) && p2c == 0) {
                p2c = choice;
            } else {
                event.reply("You already chose or aren't part of this game!").setEphemeral(true).queue();
                return;
            }
            event.reply("You chose " + event.getButton().getLabel() + " good luck!").setEphemeral(true).queue();
            if (p1c != 0 && p2c != 0) {
                gameEmbed.clearFields();
                switch (checkForWinner()) {
                    case 0 -> gameEmbed.setColor(Color.GRAY).setDescription("The game ended in a tie!");
                    case 1 -> gameEmbed.setColor(Color.GREEN).setDescription("<@" + p1 + "> won the game!");
                    case 2 -> gameEmbed.setColor(Color.GREEN).setDescription("<@" + p2 + "> won the game!");
                }
                String p1cw = null;
                String p2cw = null;
                switch (p1c) {
                    case 1 -> p1cw = "Rock";
                    case 2 -> p1cw = "Paper";
                    case 3 -> p1cw = "Scissors";
                }
                switch (p2c) {
                    case 1 -> p2cw = "Rock";
                    case 2 -> p2cw = "Paper";
                    case 3 -> p2cw = "Scissors";
                }
                gameEmbed.addField("Choices", "<@" + p1 + ">: " + p1cw + "\n<@" + p2 + ">: " + p2cw, false);
                event.getMessage().editMessage("End of Game").setEmbeds(gameEmbed.build()).setActionRow(Button.secondary("RockPS", "Rock").withEmoji(Emoji.fromUnicode("U+1FAA8")).asDisabled(),
                        Button.secondary("RPaperS", "Paper").withEmoji(Emoji.fromUnicode("U+1F4C4")).asDisabled(),
                        Button.secondary("RPScissors", "Scissors").withEmoji(Emoji.fromUnicode("U+2702")).asDisabled()).queue();
                resetGame();
            }
        } else {
            event.reply("You aren't part of this game!").setEphemeral(true).queue();
        }
    }

    public int checkForWinner() {
        if (p1c == p2c)
            return 0;
        if ((p1c == 1 && p2c == 3) || (p1c == 2 && p2c == 1) || (p1c == 3 && p2c == 2))
            return 1;
        return 2;
    }

    public void resetGame() {
        p1 = null;
        p1c = 0;
        p2 = null;
        p2c = 0;
        gameEmbed.clearFields();
        gameStarted = false;
        gameMessage = null;
        gameRunning = false;
    }
}
