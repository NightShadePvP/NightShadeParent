package me.blok601.nightshadeuhc.command.staff;

import com.google.common.collect.Sets;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Created by Blok on 1/6/2019.
 */
public class NearCommand implements UHCCommand {

    private HashSet<UUID> cooldown = Sets.newHashSet();

    private UHC uhc;

    public NearCommand(UHC uhc) {
        this.uhc = uhc;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "near"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;

        if(cooldown.contains(p.getUniqueId())){
            p.sendMessage(ChatUtils.message("&cYou can only do this command every 15 seconds!"));
            return;
        }

        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        if (uhcPlayer.isSpectator() && uhcPlayer.getPlayerStatus() == PlayerStatus.SPECTATING) {
            List<Entity> near = p.getNearbyEntities(15, 15, 15);
            HashSet<Player> players = Sets.newHashSet();
            for (Entity e : near) {
                if (e instanceof Player) {
                    players.add((Player) e);
                }
            }

            if (players.size() == 0) {
                p.sendMessage(ChatUtils.message("&cThere are no nearby players!"));
                return;
            }
            //Got our players
            HashMap<Player, Integer> distances = new HashMap<>();
            players.forEach(player -> distances.put(player, (int) p.getLocation().distance(player.getLocation())));


            p.sendMessage(ChatUtils.message("&eNearby Players:"));
            p.sendMessage(ChatUtils.format("&f&m-----------------------------------"));
            p.sendMessage(ChatUtils.format(""));
            FancyMessage fancyMessage;
            for (Map.Entry<Player, Integer> entry : distances.entrySet()) {
                fancyMessage = new FancyMessage("- ")
                        .color(ChatColor.WHITE)
                        .then(entry.getKey().getName() + " ")
                        .color(ChatColor.AQUA)
                        .command("/tp " + entry.getKey().getName())
                        .then("(").color(ChatColor.DARK_GRAY)
                        .then(entry.getValue() + "m")
                        .color(ChatColor.AQUA)
                        .then(")")
                        .color(ChatColor.DARK_GRAY)
                        .command("/tp " + entry.getKey().getName());
                fancyMessage.send(p);
            }
            p.sendMessage(ChatUtils.format("&f&m-----------------------------------"));

            cooldown.add(p.getUniqueId());
            new BukkitRunnable(){
                @Override
                public void run() {
                    cooldown.remove(p.getUniqueId());
                }
            }.runTaskLater(uhc, 20*15);

            return;
        }

        p.sendMessage(ChatUtils.message("&cOnly spectators can do this command!"));

    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.TRIAL;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
