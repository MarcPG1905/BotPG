package me.marcpg1905.game;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TicTacToe extends ListenerAdapter {
    User player1;
    User player2;
    Map<String, Integer> board = new HashMap<>();
    boolean isPlayer1Turn = true;
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    AtomicLong lastMoveTime = new AtomicLong(System.currentTimeMillis());
    boolean gameStarted = false;
    Message TTTMessage;

    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("ttt"))
            if (!gameStarted) {
                player1 = event.getUser();
                
                EmbedBuilder gameStartEmbed = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setAuthor(player1.getName(), null, player1.getAvatarUrl())
                        .setTitle("TicTacToe Game!")
                        .setDescription("Click the Button below to join the game!");
                event.replyEmbeds(gameStartEmbed.build()).setActionRow(Button.success("JoinTTTGame", "Join Game")).queue();
                
                executorService.schedule(() -> {
                    EmbedBuilder noJoinEmbed = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("TicTacToe Game!")
                            .setDescription("The game has ended because no one joined within 10 seconds.");
                    event.getChannel().sendMessageEmbeds(noJoinEmbed.build()).queue();
                    resetGame();
                }, 10L, TimeUnit.SECONDS);
            } else {
                event.reply("There is a TicTacToe game running already,\nthere can only be one running at the same time.\nPlease try again shortly.\n")
                        .setEphemeral(true).queue();
            }
    }

    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        User u = event.getUser();
        
        if (event.getComponentId().equals("JoinTTTGame")) {
            if (u != player1) {
                EmbedBuilder gameStartedEmbed = new EmbedBuilder()
                        .setColor(Color.RED)
                        .setTitle("TicTacToe Game!")
                        .setDescription("This game has already started!");
                
                player2 = u;
                
                lastMoveTime.set(System.currentTimeMillis());
                
                event.editMessageEmbeds(gameStartedEmbed.build()).setActionRow(event.getButton().asDisabled()).queue();
                startGame(event.getChannel());
            } else {
                event.reply("You can't join this game, because you're already in it!").setEphemeral(true).queue();
            }
        } else if (event.getComponentId().startsWith("TTTButton")) {
            TTTMessage = event.getMessage();
            
            User currentPlayer = isPlayer1Turn ? player1 : player2;
            
            int buttonIndex = Integer.parseInt(event.getComponentId().substring(9));
            
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            
            if (!u.getName().equals(player1.getName()) && !u.getName().equals(player2.getName())) {
                event.reply("You're not part of this round!").setEphemeral(true).queue();
                return;
            }
            if (!u.getName().equals(currentPlayer.getName())) {
                event.reply("It's not your turn!").setEphemeral(true).queue();
                return;
            }
            if (board.getOrDefault(String.valueOf(buttonIndex), -1) != -1) {
                event.reply("This button has already been clicked!").setEphemeral(true).queue();
                return;
            }

            board.put(String.valueOf(buttonIndex), isPlayer1Turn ? 1 : 0);
            event.editComponents(getBoardActionRows()).queue();
            lastMoveTime.set(System.currentTimeMillis());

            if (checkForWinner()) {
                EmbedBuilder winnerEmbed = new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .setTitle("TicTacToe Game!")
                        .setDescription(currentPlayer.getName() + " won the game!");
                event.getChannel().sendMessageEmbeds(winnerEmbed.build()).queue();
                resetGame();
            } else if (isBoardFull()) {
                EmbedBuilder tieEmbed = new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setTitle("TicTacToe Game!")
                        .setDescription("The game ended in a tie!");
                event.getChannel().sendMessageEmbeds(tieEmbed.build()).queue();
                resetGame();
            } else {
                isPlayer1Turn = !isPlayer1Turn;
            }
        }
    }

    private ButtonStyle getButtonStyleForSymbol(int symbol) {
        if (symbol == 1) return ButtonStyle.DANGER;
        if (symbol == 0) return ButtonStyle.PRIMARY;
        return ButtonStyle.SECONDARY;
    }

    public void startGame(@NotNull MessageChannel channel) {
        board.clear();
        isPlayer1Turn = true;
        gameStarted = true;
        
        channel.sendMessage("Starting TicTacToe game between " + player1.getAsMention() + " and " + player2.getAsMention() + ". It's " + (isPlayer1Turn ? player1.getName() : player2.getName()) + "'s turn.")
                .queue(message -> message.editMessageComponents(getBoardActionRows()).queue());
        
        executorService.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - lastMoveTime.get() > 10000) {
                EmbedBuilder timeoutEmbed = new EmbedBuilder()
                        .setColor(Color.ORANGE)
                        .setTitle("TicTacToe Game End!")
                        .setDescription("The game has ended due to inactivity.");
                TTTMessage.delete().queue();
                channel.sendMessageEmbeds(timeoutEmbed.build()).queue();
                resetGame();
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public List<ActionRow> getBoardActionRows() {
        List<ActionRow> actionRows = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            actionRows.add(ActionRow.of(Button.of(getButtonStyleForSymbol(board.getOrDefault(String.valueOf(i * 3), -1)), "TTTButton" + i * 3, getSymbolAt(i * 3)),
                    Button.of(getButtonStyleForSymbol(board.getOrDefault(String.valueOf(i * 3) + 1, -1)), "TTTButton" + i * 3 + 1, getSymbolAt(i * 3 + 1)),
                    Button.of(getButtonStyleForSymbol(board.getOrDefault(String.valueOf(i * 3) + 2, -1)), "TTTButton" + i * 3 + 2, getSymbolAt(i * 3 + 2))));
        }
        return actionRows;
    }

    public String getSymbolAt(int index) {
        Integer value = board.getOrDefault(String.valueOf(index), -1);
        if (value == -1) return " ";
        if (value == 1) return "X";
        return "O";
    }

    public boolean checkForWinner() {
        int i;
        for (i = 0; i < 3; i++) {
            int first = board.getOrDefault(String.valueOf(i * 3), -1);
            int second = board.getOrDefault(String.valueOf(i * 3) + 1, -1);
            int third = board.getOrDefault(String.valueOf(i * 3) + 2, -1);
            if (first == second && second == third && first != -1)
                return true;
        }
        for (i = 0; i < 3; i++) {
            int first = board.getOrDefault(String.valueOf(i), -1);
            int second = board.getOrDefault(String.valueOf(i) + 3, -1);
            int third = board.getOrDefault(String.valueOf(i) + 6, -1);
            if (first == second && second == third && first != -1)
                return true;
        }
        int topLeft = board.getOrDefault("0", -1);
        int middle = board.getOrDefault("4", -1);
        int bottomRight = board.getOrDefault("8", -1);
        if (topLeft == middle && middle == bottomRight && topLeft != -1)
            return true;
        int topRight = board.getOrDefault("2", -1);
        int bottomLeft = board.getOrDefault("6", -1);
        return (topRight == middle && middle == bottomLeft && topRight != -1);
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 9; i++) {
            if (!board.containsKey(String.valueOf(i)))
                return false;
        }
        return true;
    }

    public void resetGame() {
        player1 = null;
        player2 = null;
        board.clear();
        isPlayer1Turn = true;
        lastMoveTime = null;
        gameStarted = false;
        TTTMessage = null;
    }
}
