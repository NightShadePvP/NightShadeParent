package me.blok601.nightshadeuhc.scenario;

import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class NoTalkingScenario extends Scenario {

    public NoTalkingScenario() {
        super("No Talking", "Whenever you Talk, your location is broadcasted to everyone in chat", new ItemBuilder(Material.BARRIER).name(ChatUtils.format("No Talking")).make());
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent e) {
        if (!isEnabled()) return;

        Player p = e.getPlayer();

        if (UHCPlayer.get(p).getPlayerStatus() == PlayerStatus.PLAYING && !NSPlayer.get(p).isInStaffChat()) {
            Location loc = e.getPlayer().getLocation();
            Bukkit.broadcastMessage(ChatUtils.format(getPrefix() + "&e" + e.getPlayer().getName() + " &7is located at &8(&e" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "&8)"));
        }


    }
}
