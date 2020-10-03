package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SickToMyStomachScenario extends Scenario {

    public SickToMyStomachScenario() {
        super("Sick To The Stomach", "Eating raw meat and killing zombies give nausea for 10 seconds", new ItemBuilder(Material.RAW_BEEF).name("&eSick To The Stomach").make());
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        if (!isEnabled()) return;
        Player player = event.getPlayer();
        ItemStack food = event.getItem();
        switch (food.getType()) {
            case RAW_FISH:
            case RAW_CHICKEN:
            case RAW_BEEF:
            case RABBIT:
            case MUTTON:
            case PORK:
                PlayerUtils.givePotionEffect(player, PotionEffectType.CONFUSION, 200, 0);
                sendMessage(player, "&cYou've gotten sick to your stomach!");
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onKill(EntityDeathEvent entityDeathEvent) {
        if (!isEnabled()) return;
        Entity dead = entityDeathEvent.getEntity();
        if (dead.getType() != EntityType.ZOMBIE) return;

        Zombie zombie = (Zombie) dead;
        if (zombie.getKiller() == null) {
            return;
        }

        Player player = zombie.getKiller();
        PlayerUtils.givePotionEffect(player, PotionEffectType.CONFUSION, 200, 0);
        sendMessage(player, "&cYou've gotten sick to your stomach!");
    }

}
