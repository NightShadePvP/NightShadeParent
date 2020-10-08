package me.blok601.nightshadeuhc.component;

import com.nightshadepvp.core.utils.PacketUtils;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.event.UHCStatUpdateEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;

public class RecordedRoundComponent extends Component {

    private UHC uhc;
    private GameManager gameManager;
    private int episode;

    public RecordedRoundComponent(UHC uhc, GameManager gameManager) {
        super("Recorded Round", Material.REDSTONE_BLOCK, false, "Toggle if this is a recorded round or not");
        this.uhc = uhc;
        this.gameManager = gameManager;
        episode = 1;
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        if (!isEnabled()) return;
        new BukkitRunnable() {
            int counter = gameManager.getRrEpisodeLength();

            @Override
            public void run() {
                if (!isEnabled()) {
                    cancel();
                    return;
                }
                if (counter == 0) {
                    counter = gameManager.getRrEpisodeLength();
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        PacketUtils.sendTitle(player, 5, 40, 10, "&bEnd of Episode &f" + episode, null);
                        PlayerUtils.playSound(Sound.ENDERMAN_DEATH, player);
                    });
                    episode++;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Bukkit.getOnlinePlayers().forEach(player -> {
                                PacketUtils.sendTitle(player, 5, 40, 10, "&bEpisode &f" + episode + " &bhas begun!", null);
                                PlayerUtils.playSound(Sound.ENDERMAN_DEATH, player);
                            });
                        }
                    }.runTaskLater(uhc, 100);
                    return;
                }

                counter -= 60;
            }
        }.runTaskTimer(uhc, 0, 60 * Util.TICKS);
    }

    @EventHandler
    public void onStatUpdate(UHCStatUpdateEvent event) {
        if (!isEnabled()) return;
        event.setCancelled(true);
    }
}
