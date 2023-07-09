package me.marcpg1905.moderation;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.hectus.util.Randomizer;
import org.jetbrains.annotations.NotNull;

public class AutoAnswer extends ListenerAdapter {
    private static final String[] ips = {
            "blockbattles.isnt.out.yet",
            "it.isnt.out.yet",
            "it.isnt.released",
            "coming.soon",
            "stillbrewing.xyz",
            "notreleased.yet",
            "mc.theserverhasntreleased.yet",
            "just.read.please",
            "are.you.dumb?",
            "not.existing.ip",
            "blockbattlessandtheothergamemodesarentoutyet.net",
            "monke.aternos.me",
            "hacktus.aternos.me",
            "read.pls",
            "marcpg1905.aternos.me",
            "uh12i3788asd.gamer-craft.hosting.eu"
    };

    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String m = event.getMessage().getContentRaw().toLowerCase();
        if (m.contains(" ip") || m.contains("ip ") || m.contains("ip?") || m.contains("address")) {
            event.getMessage().reply("The IP is: " + Randomizer.fromArray(ips)).queue();
        }
    }
}
