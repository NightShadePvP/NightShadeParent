package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.CustomDeathEvent;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class TrainingRabbitsScenario extends Scenario {

    public TrainingRabbitsScenario() {
        super("Training Rabbits", "You start the game with jump boost 1. For each kill after that, you gain an extra level of jump boost. Fall damage is disabled.", new ItemBuilder(Material.RABBIT_FOOT).name("Training Rabbits").make());
    }


    @EventHandler
    public void onKill(CustomDeathEvent event) {
        if (!isEnabled()) return;
        Player killer = event.getKiller();
        if (killer == null) return;

        int nextLevel = -1;
        if (killer.hasPotionEffect(PotionEffectType.JUMP)) {
            for (PotionEffect effect : killer.getActivePotionEffects()) {
                if (effect.getType() == PotionEffectType.JUMP) {
                    nextLevel = effect.getAmplifier() + 1;
                }
            }
        }else{
            nextLevel = 0;
        }

        killer.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, nextLevel, false, true));
        sendMessage(killer, "&eYou now have Jump Boost " + nextLevel);
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (!isEnabled()) return;
        UHCPlayerColl.get().getAllOnlinePlayers().forEach(uhcPlayer -> uhcPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, false, true)));
        broadcast("&eAll players will start with Jump Boost I");
    }

    @EventHandler
    public void onFall(EntityDamageEvent e) {
        if (!isEnabled()) return;
        if (e.getEntity() instanceof Player) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

}
