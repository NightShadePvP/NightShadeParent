package me.blok601.nightshadeuhc.scoreboard.provider;

import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.ArenaSession;
import me.blok601.nightshadeuhc.scoreboard.SidebarEntry;
import me.blok601.nightshadeuhc.scoreboard.SidebarEntry;
import me.blok601.nightshadeuhc.scoreboard.SidebarProvider;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Blok on 12/24/2018.
 */
public class ArenaProvider implements SidebarProvider {

    /**
     * Gets the current Sidebar lines of the Scoreboard
     *
     * @param p
     * @return the Scoreboard Sidebar lines
     */
    @Override
    public List<SidebarEntry> getLines(Player p) {
        ArrayList<SidebarEntry> lines = new ArrayList<>();
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------") + ChatColor.AQUA.toString()));
        lines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatUtils.format("&fPlayers: &b"), ChatColor.AQUA + "" + UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isInArena).count()));
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------" + ChatColor.BLUE.toString())));
        lines.add(new SidebarEntry(ChatUtils.format("&bYour Stats:")));
        lines.add(new SidebarEntry(ChatColor.BLUE.toString(), ChatUtils.format("&fKills: &b"), ChatColor.AQUA + "" + uhcPlayer.getArenaKills()));
        lines.add(new SidebarEntry(ChatColor.GREEN.toString(), ChatUtils.format("&fDeaths: &b"), ChatColor.AQUA + "" + uhcPlayer.getArenaDeaths()));
        lines.add(new SidebarEntry(ChatColor.YELLOW.toString(), ChatUtils.format("&fHighest KillStreak: &b"), ChatColor.AQUA + "" + uhcPlayer.getHighestArenaKillStreak()));
        lines.add(new SidebarEntry(ChatColor.DARK_GREEN.toString(), ChatUtils.format("&fK/DR: &b"), ChatColor.AQUA + "" + ArenaSession.DECIMAL_FORMAT.format(uhcPlayer.getArenaKDR())));
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------" + ChatColor.DARK_BLUE.toString())));
        lines.add(new SidebarEntry(ChatUtils.format("&bCurrent Session:")));
        lines.add(new SidebarEntry(ChatColor.BLACK.toString(), ChatUtils.format("&fKills: &b"), ChatColor.AQUA + "" + uhcPlayer.getArenaSession().getKills()));
        lines.add(new SidebarEntry(ChatColor.DARK_GRAY.toString(), ChatUtils.format("&fDeaths: &b"), ChatColor.AQUA + "" + uhcPlayer.getArenaSession().getDeaths()));
        lines.add(new SidebarEntry(ChatColor.LIGHT_PURPLE.toString(), ChatUtils.format("&fKillStreak: &b"), ChatColor.AQUA + "" + uhcPlayer.getArenaSession().getKillstreak()));
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------")));
        lines.add(new SidebarEntry(ChatUtils.format("&bdiscord.nightshadepvp.com")));
        return lines;
    }
}
