package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.ImmutableSet;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class CobbleHatersScenario extends Scenario{

    private Set<Material> disallowedItems;
    public CobbleHatersScenario() {
        super("Cobble Haters", "You cannot mine stone, craft furnaces, or make stone tools", new ItemBuilder(Material.STONE_PICKAXE).name("Cobble Haters").make());
        disallowedItems = ImmutableSet.of(Material.FURNACE, Material.STONE_AXE, Material.STONE_HOE, Material.STONE_PICKAXE, Material.STONE_SPADE);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event){
        if(!isEnabled()) return;
        if(event.getInventory() == null || event.getCurrentItem() == null && event.getClickedInventory() == null) return;
        if(event.getWhoClicked() != null && event.getWhoClicked() instanceof Player){
            Player player = (Player) event.getWhoClicked();
            if(disallowedItems.contains(event.getRecipe().getResult().getType())){
                event.setCancelled(true);
                sendMessage(player, "&cYou can't craft this item!");
                return;
            }
        }

    }

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(!isEnabled()) return;
        Player player = event.getPlayer();
        if(event.getBlock().getType() == Material.STONE){
            event.setCancelled(true);
            sendMessage(player, "&cYou can't break stone!");
            return;
        }
    }


}
