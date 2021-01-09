package me.blok601.nightshadeuhc.task;

import com.nightshadepvp.core.utils.ActionBarUtil;
import com.wimbli.WorldBorder.BorderData;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.MathUtils;
import me.blok601.nightshadeuhc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

/**
 * Created by Blok on 3/17/2017.
 */
public class WorldBorderTask extends BukkitRunnable {

    public static int counter;
    private World world;
    private int first;
    private GameManager gameManager;

    public WorldBorderTask(int counter, World world, int first, GameManager gameManager) {
        this.counter = counter;
        this.world = world;
        this.first = first;
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        BorderData bd = com.wimbli.WorldBorder.WorldBorder.plugin.getWorldBorder(world.getName());

        if (counter > 0) {
            if (counter == (counter - 300)) {
                //5 mins before
                Util.staffLog("The border will shrink in 5 minutes!");
                ChatUtils.sendBorderMessage("The border will shrink to " + gameManager.getFirstShrink() + " radius in 5 minutes");
                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> {
                    ActionBarUtil.sendActionBarMessage(player, "§bShrink to " + gameManager.getFirstShrink() + " radius in " + get(counter), 1, UHC.get());
                });
            } else if (MathUtils.isBetween(10, 0, counter)) {
                ChatUtils.sendBorderMessage("The border will shrink to " + gameManager.getFirstShrink() + " radius in " + counter);
            }
        } else if (counter == 0) {
            bd.setRadius(first);
            new BukkitRunnable(){
                @Override
                public void run() {
                    gameManager.genWalls(first, gameManager.getWorld());
                    gameManager.setBorderID(gameManager.getBorderID() + 1);
                }
            }.runTaskLater(UHC.get(), 2);
            for (Player pls : Bukkit.getOnlinePlayers()) {
                pls.playSound(pls.getLocation(), Sound.BAT_DEATH, 5, 1);
            }
            ChatUtils.sendBorderMessage("&bThe border has shrunk to " + gameManager.getFirstShrink() + " radius!");
            counter = -1;
            ShrinkTask shrinkTask = new ShrinkTask(world);
            gameManager.setShrinkTask(shrinkTask);
            shrinkTask.runTaskTimer(UHC.get(), 290 * Util.TICKS, 290 * Util.TICKS);
            this.cancel();
            return;
        }
        counter--;
    }


    private String get(int i) {
        int m = i / 60;
        int s = i % 60;

        return "§b" + m + "§fm§b" + s + "§fs";
    }
}
