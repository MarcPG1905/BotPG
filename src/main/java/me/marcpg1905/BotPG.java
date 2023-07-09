package me.marcpg1905;

import me.marcpg1905.economy.LevelSystem;
import me.marcpg1905.economy.StatsCommand;
import me.marcpg1905.game.CoinFlip;
import me.marcpg1905.game.RockPaperScissors;
import me.marcpg1905.game.TicTacToe;
import me.marcpg1905.moderation.AutoAnswer;
import me.marcpg1905.moderation.WarnCommand;
import me.marcpg1905.moderation.WarnReason;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.hectus.logging.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BotPG {
    public static JDA jda;
    public static final Logger LOGGER = new Logger("BotPG");

    public static void main(String[] args) throws InterruptedException {
        jda = JDABuilder.createDefault(Config.get("token"))
                .setActivity(Activity.listening("marcpg1905's code"))
                .addEventListeners()
                .enableIntents(GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS))
                .addEventListeners(
                        new LevelSystem(),
                        new StatsCommand(),
                        new CoinFlip(),
                        new AutoAnswer(),
                        new WarnCommand(),
                        new StopBotPGCommand(),
                        new DevLog()
                )
                .build().awaitReady();

        OptionData type = new OptionData(OptionType.STRING, "type", "What type of log it is", true)
                .addChoice("add", "add")
                .addChoice("modify", "modify")
                .addChoice("cleanup", "cleanup")
                .addChoice("remove", "remove");

        Guild guild = Objects.requireNonNull(jda.getGuildById(1052224242965545103L));
        guild.updateCommands().addCommands(
                Commands.slash("stats", "Shows your Stats.")
                        .addOption(OptionType.USER, "user", "Which User to get the Stats from."),
                Commands.slash("warn", "Warn a User.")
                        .addOption(OptionType.USER, "user", "The User that gets warned.", true)
                        .addOptions(new OptionData(OptionType.STRING, "violation", "The rule that the user has broken.", true).addChoices(choices()))
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.BAN_MEMBERS)),
                Commands.slash("stopbotpg", "Stops BotPG. (administrator-only)")
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR)),
                Commands.slash("coinflip", "Flip a Coin."),
                Commands.slash("devlog", "Send a Dev-Log.").addSubcommands(
                        new SubcommandData("test", "A gameplay-test.")
                                .addOption(OptionType.STRING, "tested", "What was tested?", true)
                                .addOption(OptionType.BOOLEAN, "success", "If the test succeeded or failed.", true)
                                .addOption(OptionType.STRING, "bugs", "Bugs and Issues.")
                                .addOption(OptionType.USER, "with", "With who you tested."),
                        new SubcommandData("turn", "When you add a new Turn.").addOptions(type)
                                .addOption(OptionType.STRING, "modification", "What you changed. Just put some shit in here if you didn't choose modification.", true)
                                .addOption(OptionType.STRING, "name", "The turn's name.", true),
                        new SubcommandData("feature", "When you add a new Feature.").addOptions(type)
                                .addOption(OptionType.STRING, "modification", "What you changed. Just put some shit in here if you didn't choose modification.", true)
                                .addOption(OptionType.STRING, "name", "Give the feature a name.", true)
                                .addOption(OptionType.STRING, "description", "What does the feature do?", true)
                                .addOption(OptionType.STRING, "class", "The main class in which the feature operates.", true),
                        new SubcommandData("fix", "When you fix a bug/issue.")
                                .addOption(OptionType.STRING, "feature", "The feature you fixed.", true)
                                .addOption(OptionType.BOOLEAN, "success", "If the fix worked.", true),
                        new SubcommandData("other", "Something else.")
                                .addOption(OptionType.STRING, "title", "The Title.", true)
                                .addOption(OptionType.STRING, "description", "The Description.", true)
                ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_TTS))
        ).queue();

        LOGGER.success("All systems started!");
    }

    @NotNull
    private static List<Command.Choice> choices() {
        List<Command.Choice> choices = new ArrayList<>();
        for (WarnReason reason : WarnReason.values()) {
            choices.add(reason.toChoice());
        }
        return choices;
    }
}
