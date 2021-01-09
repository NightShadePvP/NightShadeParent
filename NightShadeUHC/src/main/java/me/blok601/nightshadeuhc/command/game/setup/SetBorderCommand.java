package me.blok601.nightshadeuhc.command.game.setup;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.MConf;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Blok on 12/20/2018.
 */
public class SetBorderCommand implements UHCCommand {

    private GameManager gameManager;

    public SetBorderCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "setborder"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if (args.length == 0) {
            p.sendMessage(ChatUtils.message("&cUsage: /setborder <radius> [world]"));
            return;
        }

        if (args.length == 1) {
            if (!MathUtils.isInt(args[0])) {
                p.sendMessage(ChatUtils.message("&cPlease supply a valid number!"));
                return;
            }

            int radius = Integer.parseInt(args[0]);
            World world = p.getWorld();

            if (world.getName().equalsIgnoreCase(MConf.get().getSpawnLocation().asBukkitLocation().getWorld().getName())) {
                if (!NSPlayer.get(p).hasRank(Rank.ADMIN)) {
                    p.sendMessage(ChatUtils.message("&cOnly admins can change the spawn border!"));
                    return;
                }
            }


            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + world.getName() + " set " + radius + " " + radius + " 0 0");
            new BukkitRunnable(){
                @Override
                public void run() {
                    gameManager.genWalls(radius, gameManager.getWorld());
                }
            }.runTaskLater(UHC.get(), 2);
            p.sendMessage(ChatUtils.message("&eThe border for &b" + world.getName() + " &eis now &b" + radius + " &ex&b " + radius));

        } else if (args.length == 2) {

            if (!MathUtils.isInt(args[0])) {
                p.sendMessage(ChatUtils.message("&cPlease supply a valid number!"));
                return;
            }

            int radius = Integer.parseInt(args[0]);

            World world = Bukkit.getWorld(args[1]);
            if (world == null) {
                p.sendMessage(ChatUtils.message("&cThat world could not be found!"));
                return;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb " + world.getName() + " set " + radius + " " + radius + " 0 0");
            gameManager.genWalls(radius, world);
            p.sendMessage(ChatUtils.message("&eThe border for &b" + world.getName() + " &eis now &b" + radius + " &ex&b " + radius));
        } else {
            p.sendMessage(ChatUtils.message("&cUsage: /setborder <radius> [world]"));
            return;
        }

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
