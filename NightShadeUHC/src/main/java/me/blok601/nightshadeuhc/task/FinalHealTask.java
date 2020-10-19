package me.blok601.nightshadeuhc.task;

import com.massivecraft.massivecore.nms.NmsChat;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.FinalHealEvent;
import me.blok601.nightshadeuhc.event.PlayerStopSpectatingEvent;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Blok on 3/16/2017.
 */
public class FinalHealTask extends BukkitRunnable {

    private int counter;

    public FinalHealTask(int counter) {
        this.counter = counter;
    }

    @Override
    public void run() {

        if (counter <= -1) {
            return;
        }


        if (counter == 0) {

            UHCPlayerColl.get().getAllOnline().stream().filter(uhcPlayer -> {

                Player player = uhcPlayer.getPlayer();

                player.setHealth(player.getMaxHealth());
                player.setFoodLevel(20);

                if (uhcPlayer.isUsingOldVersion()) {
                    uhcPlayer.msg(ChatUtils.message("&3Final heal has been given!"));
                    return false;
                }

                return true;

            }).forEach(uhcPlayer -> NmsChat.get().sendTitleMessage(uhcPlayer.getPlayer(),
                    10,
                    30,
                    10,
                    ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Final Heal Has Been Given!",
                    ChatColor.DARK_RED + "Don't ask for another!"));

            Bukkit.getServer().getPluginManager().callEvent(new FinalHealEvent());
            counter = -1;
            cancel();
            return;
        }
        counter--;

    }

}
