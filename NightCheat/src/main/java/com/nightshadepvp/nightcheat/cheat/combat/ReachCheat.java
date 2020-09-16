package com.nightshadepvp.nightcheat.cheat.combat;

import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.nightcheat.cheat.Cheat;
import com.nightshadepvp.nightcheat.cheat.CheatType;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ReachCheat extends Cheat {

    public ReachCheat() {
        super("Reach", CheatType.COMBAT);
        this.setViolationsToNotify(2);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void on(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();

        if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) {
            return;
        }

        if (!(damager instanceof Player)) {
            return;
        }

        Player killer = (Player) damager;

        if(killer.getGameMode() == GameMode.CREATIVE) return;

        double distance = killer.getLocation().distance(entity.getLocation());

        if (distance < 5.0) {
            return;
        }

        flag(killer, this, "Hit a player from " + distance + " blocks away. Ping: " + NSPlayer.get(killer).getPing() + ")");

    }
}
