package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.Sets;
import com.nightshadepvp.core.Core;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.LoggedOutPlayer;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.event.PvPEnableEvent;
import me.blok601.nightshadeuhc.manager.LoggerManager;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public class RocketStartScenario extends Scenario {

    public RocketStartScenario() {
        super("Rocket Start", "Players receive speed and haste until PvP is enabled", new ItemBuilder(Material.POTION).durability(8258).name("Rocket Start").make());
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        if (!isEnabled()) return;

        for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllPlaying()) {
            if (uhcPlayer.isOnline()) {
                uhcPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, true));
                uhcPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 0, false, true));
                sendMessage(uhcPlayer.getPlayer(), "&bYou have received &fSpeed I &band &fHaste I");
            }
        }
    }

    @EventHandler
    public void onPvP(PvPEnableEvent event) {
        if (!isEnabled()) return;
        Player player;
        for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllPlaying()) {
            if (uhcPlayer.isOnline()) {
                player = uhcPlayer.getPlayer();
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1, 0, false, false));
                sendMessage(uhcPlayer.getPlayer(), "&fSpeed I &band &fHaste I&b have been removed! Good luck!");
            }
        }

        for (LoggedOutPlayer loggedOutPlayer : LoggerManager.getInstance().getLoggedOutPlayers()) {
            if (Core.get().getLoginTasks().containsKey(loggedOutPlayer.getUuid())) {
                ArrayList<Consumer<UUID>> tasks = Core.get().getLoginTasks().get(loggedOutPlayer.getUuid());
                HashSet<PotionEffect> tr = Sets.newHashSet();
                tasks.add(uuid -> {
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1, 0, false, false));
                        sendMessage(p.getPlayer(), "&fSpeed I &band &fHaste I&b have been removed! Good luck!");
                    }
                });
                Core.get().getLoginTasks().put(loggedOutPlayer.getUuid(), tasks);
            } else {
                ArrayList<Consumer<UUID>> newTasks = new ArrayList<>();
                newTasks.add(uuid -> {
                    HashSet<PotionEffect> tr = Sets.newHashSet();
                    Player p = Bukkit.getPlayer(uuid);
                    if (p != null) {
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1, 0, false, false));
                        p.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 1, 0, false, false));
                        sendMessage(p.getPlayer(), "&fSpeed I &band &fHaste I&b have been removed! Good luck!");
                    }
                });
                Core.get().getLoginTasks().put(loggedOutPlayer.getUuid(), newTasks);
            }
        }
    }
}
