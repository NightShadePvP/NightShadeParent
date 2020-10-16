package me.blok601.nightshadeuhc.command.game.setup;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Created by Blok on 3/23/2018.
 */
public class WorldsCommand implements UHCCommand{
    @Override
    public String[] getNames() {
        return new String[]{
                "worlds"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;


        HashSet<World> worlds = Bukkit.getWorlds().stream().filter(world -> world.getName().toLowerCase().startsWith("uhc")).collect(Collectors.toCollection(HashSet::new));

        p.sendMessage(ChatUtils.message("&eWorlds List: "));
        FancyMessage fancyMessage;
        for (World world : worlds) {
            fancyMessage = new FancyMessage("- ").color(ChatColor.WHITE).then(world.getName()).color(ChatColor.AQUA).command("/tpworld " + world.getName())
                    .tooltip("Click to teleport to " + world.getName()).color(ChatColor.GOLD);
            fancyMessage.send(p);
        }

    }

    @Override
    public boolean playerOnly() {
        return false;
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
