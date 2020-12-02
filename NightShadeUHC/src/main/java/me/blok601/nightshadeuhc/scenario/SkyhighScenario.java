package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.PvPEnableEvent;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

/**
 * Created by Blok on 2/16/2018.
 */
public class SkyhighScenario extends Scenario implements StarterItems {


    public SkyhighScenario() {
        super ("Skyhigh", "After a certain period of time, you will take damage every 30 seconds if you are below Y:100", new ItemBuilder(Material.COBBLESTONE).name("Skyhigh").make());
    }

    private BukkitTask task;

    @Override
    public void onToggle(boolean newState, Player toggler) {
        if (!newState) {
            if (task != null) {
                task.cancel();
                sendMessage(toggler, "&bSkyhigh tasks have been disabled!");
            }

            task = null;
        }
    }

    @EventHandler
    public void on(PvPEnableEvent e){
        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if(!isEnabled()){
                    this.cancel();
                    return;
                }

                UHCPlayerColl.get().getAllPlaying().stream().filter(uhcPlayer -> PlayerUtils.inGameWorld(uhcPlayer.getPlayer())).filter(uhcPlayer -> uhcPlayer.getPlayer().getLocation().getY() < 100).forEach(uhcPlayer -> {
                    uhcPlayer.getPlayer().setHealth(uhcPlayer.getPlayer().getHealth() - 1);
                    uhcPlayer.msg(ChatUtils.format(getPrefix() + " &eYou have lost 0.5 hearts for being under y:100!"));
                });


            }
        }.runTaskTimer(UHC.get(), 0, 20*30); //30 seconds
    }

    @Override
    public List<ItemStack> getStarterItems() {
        return MUtil.list(new ItemStack(Material.SNOW_BLOCK, 2),
                new ItemStack(Material.PUMPKIN),
                new ItemStack(Material.DIAMOND_SPADE),
                new ItemStack(Material.STAINED_CLAY, 64),
                new ItemStack(Material.STAINED_CLAY, 64),
                new ItemStack(Material.STRING, 2),
                new ItemStack(Material.FEATHER, 16));
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
