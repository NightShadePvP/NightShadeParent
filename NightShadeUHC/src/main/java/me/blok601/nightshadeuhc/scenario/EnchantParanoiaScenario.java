package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.inventory.ItemStack;

public class EnchantParanoiaScenario extends Scenario {

    public EnchantParanoiaScenario() {
        super("Enchant Paranoia", "Player's coords are broadcast when they enchant an item", new ItemBuilder(Material.ENCHANTMENT_TABLE).name("Enchant Paranoia").make());
    }


    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        if (!isEnabled()) return;
        if (event.isCancelled()) return;

        Player player = event.getEnchanter();
        Location location = player.getLocation();
        broadcast("&f" + player.getName() + " &benchanted an item at &f" + location.getX() + "," + location.getY() + "," + location.getZ());
    }

}
