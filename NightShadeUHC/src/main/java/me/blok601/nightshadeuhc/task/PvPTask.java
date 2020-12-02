package me.blok601.nightshadeuhc.task;

import com.massivecraft.massivecore.nms.NmsChat;
import me.blok601.nightshadeuhc.command.staff.PvPCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.PvPEnableEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

/**
 * Created by Blok on 3/16/2017.
 */
public class PvPTask extends BukkitRunnable {
    public static int counter;
    private World w;

    private boolean pvpEnabled;

    private GameManager gameManager;
    public PvPTask(int counter, World w, GameManager gameManager) {
        PvPTask.counter = counter;
        this.w = w;
        this.gameManager = gameManager;
        pvpEnabled = false;
    }

    @Override
    public void run() {

        if (counter <= -1) return;

        if (counter == 0) {

            PvPCommand.enablePvP(w);
            Bukkit.getServer().getPluginManager().callEvent(new PvPEnableEvent());

            PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"PvP Has Been Enabled!\",\"color\":\"dark_aqua\",\"bold\":true}"));

            for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllOnline()) {

                if (uhcPlayer.isUsingOldVersion()) {
                    uhcPlayer.msg(ChatUtils.message("&3PvP has been enabled!"));
                    gameManager.setWhitelistEnabled(true);
                    continue;
                }

                ((CraftPlayer) uhcPlayer.getPlayer()).getHandle().playerConnection.sendPacket(packet);
            }

            counter = -1;
            this.cancel();
            pvpEnabled = true;
            return;
        }

        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> {
            NmsChat.get().sendActionbarMessage(player, "§bPvP §8» " + formatTime(counter));
        });


        counter--;


    }

    public boolean isPvpEnabled() {
        return pvpEnabled;
    }

    private String formatTime(int i) {
        int m = i / 60;
        int s = i % 60;

        return "§b" + m + "§fm§b" + s + "§fs";
    }
}
