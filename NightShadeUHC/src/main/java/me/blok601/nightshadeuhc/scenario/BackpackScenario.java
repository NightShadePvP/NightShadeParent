package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.event.CustomDeathEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Master on 7/14/2017.
 */
public class BackpackScenario extends Scenario{

    public static HashMap<UUID, Inventory> bps = new HashMap<>();

    public BackpackScenario() {
        super("BackPack", "Each player gets their very own backpack.", "BP", new ItemBuilder(Material.WOOD).name("BackPacks").make());
    }

    @EventHandler
    public void onStart(GameStartEvent e){
        if(!isEnabled()) return;
        for (Player player : Bukkit.getOnlinePlayers()){
            bps.put(player.getUniqueId(), Bukkit.createInventory(null, 27, player.getName() + "'s BackPack"));
        }
    }

    @EventHandler
    public void onDeath(CustomDeathEvent event){
        if(!isEnabled()) return;
        Player dead = event.getKilled();
        for (ItemStack stack : bps.get(dead.getUniqueId()).getContents()){
            if(stack == null || stack.getType() == Material.AIR) continue;
            dead.getWorld().dropItemNaturally(dead.getLocation(), stack);
        }
    }
}
