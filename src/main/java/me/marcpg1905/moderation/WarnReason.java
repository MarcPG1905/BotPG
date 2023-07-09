package me.marcpg1905.moderation;

import net.dv8tion.jda.api.interactions.commands.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum WarnReason {
    DISCORD_RULES("Violating Discord's ToS/Guidelines", "discord", "Please avoid violating Discord's ToS or Guidelines!", 2),
    ENGLISH_ONLY("Not speaking English", "language", "Please only speak English!", 1),
    MEMBER_DISRESPECT("Being Disrespectful", "disrespect", "Be respectful to everyone!", 1),
    ADVERTISING("Advertising", "advert", "Don't advertise in Channels, that aren't made for Advertising", 2),
    MALICIOUS_LINK("Malicious Link / Link Shortener", "link", "Please only send links to well-known websites and don't use link shorteners", 1),
    WRONG_CHANNEL("Using the wrong Channel", "channel", "Use the right Channel!", 1),
    HARASSING("Harassing/Toxic/Annoying", "harassing", "Please don't harass or be toxic/annoying.", 2),
    SPAMMING("Spamming", "spam", "Stop spamming!", 2),
    GORE("Gore", "gore", "Don't send anything that is related to Gore!", 3),
    NSFW("NSFW", "nsfw", "Please don't send anything that's NSFW!", 3),
    SWEARING("Swearing", "swearing", "Stop swearing!", 2),
    BEGGING("Begging", "begging", "Stop begging and get a life.", 1),
    BAN_AVOIDING("Alt Account", "avoiding", "Don't use Alt Accounts!", 6),
    IMPERSONATING("Impersonation", "impersonation", "Don't impersonate anyone!", 2),
    DOXXING("Doxxing", "doxxing", "Don't doxx anyone, you stupid ass!", 8),
    FAKE_SWEAR("Fake Swearing", "fakeswear", "Stop fake-swearing, you're not funny.", 1),
    SPOILER_ABUSE("Spoiler Abuse", "spoiler", "Please use Spoilers properly", 2),
    STAFF_DISRESPECT("Staff/Server Disrespect", "staffdisrespect", "Please stop disrespecting Staff or the Server.", 2),
    PINGING("Pinging", "pinging", "Please stop pinging without a good Reason.", 1),
    STUPID_QUESTION("Stupid Question", "question", "Stop those stupid Questions, just read.", 1),
    VC_RULE("Breaking VoiceChat Rules", "vc", "Don't break the VC Rules, they are Rules too!", 2),
    POWER_ABUSE("Power/Role/Rank Abuse", "abuse", "Don't abuse your Power!", 5),
    OTHER("Other (1lvl)", "other", "Please ask the Warner for a Reason!", 1);

    public final String name, value, text;
    public final int level;

    WarnReason(String name, String value, String text, int level) {
        this.name = name;
        this.value = value;
        this.text = text;
        this.level = level;
    }

    @NotNull
    @Contract(" -> new")
    public Command.Choice toChoice() {
        return new Command.Choice(name, value);
    }
}
