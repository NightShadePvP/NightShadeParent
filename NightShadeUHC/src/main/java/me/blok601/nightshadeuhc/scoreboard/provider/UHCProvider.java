package me.blok601.nightshadeuhc.scoreboard.provider;


import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scoreboard.SidebarEntry;
import me.blok601.nightshadeuhc.scoreboard.SidebarProvider;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class UHCProvider implements SidebarProvider {

    /**
     * You can use up to 15 Scoreboard lines.
     * Max-length of your text is 32.
     *
     * @return
     */
    @Override
    public List<SidebarEntry> getLines(Player p) {
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        List<SidebarEntry> lines = new ArrayList<>();

        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------------" + ChatColor.BLUE.toString())));
        lines.add(new SidebarEntry(ChatColor.BLACK.toString(), ChatUtils.format("&fGame Clock: &b"), ChatColor.AQUA + (GameManager.get().getTimer().isRunning() ? GameManager.get().getTimer().getTime() : "Waiting...")));
        lines.add(new SidebarEntry(ChatColor.GREEN.toString(), ChatUtils.format("&fKills: &b"), ChatColor.AQUA + "" + GameManager.get().getKills().getOrDefault(p.getUniqueId(), 0)));
        if (GameManager.get().isIsTeam()) {
            //Team game
            Team team = TeamManager.getInstance().getTeam(p);


            if (team == null) {
                lines.add(new SidebarEntry(ChatColor.YELLOW.toString(), ChatUtils.format("&fTeam Kills: &b"), ChatColor.AQUA + "0"));
            } else {
                //They have a team now
                int teamKills = 0;
                for (String member : team.getMembers()) {
                    if (Bukkit.getPlayer(member) == null) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(member);
                        if (offlinePlayer == null) continue;
                        teamKills += GameManager.get().getKills().getOrDefault(offlinePlayer.getUniqueId(), 0);
                    } else {
                        teamKills += GameManager.get().getKills().getOrDefault(Bukkit.getPlayer(member).getUniqueId(), 0);
                    }
                }
                lines.add(new SidebarEntry(ChatColor.GRAY.toString(), ChatUtils.format("&fTeam Kills: &b"), ChatColor.AQUA + "" + teamKills));
            }
        }
        if (GameState.gameHasStarted()) {
            lines.add(new SidebarEntry(ChatColor.DARK_AQUA.toString(), ChatUtils.format("&fPlayers: &b"), ChatColor.AQUA + "" + (UHCPlayerColl.get().getAllPlaying().size() + UHC.loggedOutPlayers.size()) + "/" + GameManager.get().getMaxPlayers()));
        } else {
            lines.add(new SidebarEntry(ChatColor.DARK_RED.toString(), ChatUtils.format("&fPlayers: &b"), ChatColor.AQUA + "" + (Bukkit.getOnlinePlayers().size() - UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).count())));
        }

        lines.add(new SidebarEntry(ChatColor.DARK_PURPLE.toString(), ChatUtils.format("&fSpectators: &b"), ChatColor.AQUA + "" + UHCPlayerColl.get().getAllOnline().stream().filter(UHCPlayer::isSpectator).count()));
        if (GameManager.get().getWorld() == null) {
            lines.add(new SidebarEntry(ChatColor.BLUE.toString(), ChatUtils.format("&fBorder: &b"), ChatColor.AQUA + "World Not Set"));
        } else {
            //Have a world
            lines.add(new SidebarEntry(ChatColor.GOLD.toString(), ChatUtils.format("&fBorder: &b"), ChatColor.AQUA + "" + ((int) GameManager.get().getBorderSize())));
        }
        lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------------&r" + ChatColor.DARK_GREEN.toString())));
        if (uhcPlayer.isNoClean()) {
            lines.add(new SidebarEntry(ChatColor.WHITE.toString(), ChatUtils.format("&fNoClean: &b"), ChatColor.AQUA + "" + uhcPlayer.getNoCleanTimer() + "s"));
            lines.add(new SidebarEntry(ChatUtils.format("&f&m--------------------&r")));
        }
        lines.add(new SidebarEntry(ChatUtils.format("&bdiscord.nightshadepvp.com")));
        return lines;
    }


}
