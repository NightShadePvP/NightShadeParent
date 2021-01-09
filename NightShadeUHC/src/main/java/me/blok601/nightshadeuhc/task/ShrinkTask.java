package me.blok601.nightshadeuhc.task;

import com.wimbli.WorldBorder.BorderData;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Blok on 7/18/2017.
 */
public class ShrinkTask extends BukkitRunnable {

    private World world;
    //private int counter;

    public ShrinkTask(World world) {
        this.world = world;
        //this.counter = 300;
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            int counter = 10;

            @Override
            public void run() {
                if (counter > 0) {
                    ChatUtils.sendBorderMessage("The border will shrink to " + GameManager.get().getShrinks()[GameManager.get().getBorderID()] + " radius in " + counter);

                } else if (counter == 0) {
                    BorderData bd = com.wimbli.WorldBorder.WorldBorder.plugin.getWorldBorder(world.getName());
                    bd.setRadius(GameManager.get().getShrinks()[GameManager.get().getBorderID()]);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            GameManager.get().genWalls(GameManager.get().getShrinks()[GameManager.get().getBorderID()], GameManager.get().getWorld());
                            GameManager.get().setBorderID(GameManager.get().getBorderID() + 1);
                        }
                    }.runTaskLater(UHC.get(), 2);
                    //this.counter = 300;


                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.BAT_DEATH, 5, 5);
                    }
                    ChatUtils.sendBorderMessage("&bThe border has shrunk to " + bd.getRadiusX() + " radius!");


                    if (GameManager.get().getShrinks()[GameManager.get().getBorderID() + 1] == 0) {
                        this.cancel();
                        Util.staffLog(ChatUtils.format("&eThe final shrink has occurred"));
                        return;
                    }
                }

                counter--;
            }
        }.runTaskTimer(UHC.get(), 0, 20);
    }
}
