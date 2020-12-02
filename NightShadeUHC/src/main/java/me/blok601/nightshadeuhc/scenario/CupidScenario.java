package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;

public class CupidScenario extends Scenario {

    private final DecimalFormat format = new DecimalFormat("##.##");

    public CupidScenario() {
        super("Cupid", "Bowshots heal back 1% of your total health", new ItemBuilder(Material.BOW).name("Cupid").make());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!isEnabled()) return;
        if (e.getDamage() <= 0) return;
        Entity damager = e.getDamager();
        if (!(damager instanceof Arrow)) return;
        Arrow arrow = (Arrow) damager;
        if (!(arrow.getShooter() instanceof Player)) return;

        Player shooter = (Player) arrow.getShooter();
        if (shooter.equals(e.getEntity())) return;
        double health = shooter.getHealth();
        double healthToHeal = health * 0.01;
        if (health + healthToHeal > shooter.getMaxHealth()) {
            shooter.setHealth(shooter.getMaxHealth());
        } else {
            shooter.setHealth(health + healthToHeal);
        }

        sendMessage(shooter, "&eYou have gained &a+" + format.format(healthToHeal) + "&c♥ &efrom that bowshot!");

    }

}
