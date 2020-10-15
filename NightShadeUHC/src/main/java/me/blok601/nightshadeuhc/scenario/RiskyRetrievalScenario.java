package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class RiskyRetrievalScenario extends Scenario {
    public RiskyRetrievalScenario() {
        super("RiskyRetrieval", "Gold and Diamonds, when mined, spawn in an enderchest at 0, 0.", new ItemBuilder(Material.PACKED_ICE).name("Risky Retrieval").make());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!isEnabled()) {
            return;
        }

        Player p = e.getPlayer();
        if (e.getBlock().getType() == Material.GOLD_ORE) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            p.setExp(p.getExp() + e.getExpToDrop());
            p.getEnderChest().addItem(new ItemStack(Material.GOLD_INGOT));
            return;
        }

        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            p.setExp(p.getExp() + e.getExpToDrop());
            p.getEnderChest().addItem(new ItemStack(Material.GOLD_INGOT));
            return;
        }
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (!isEnabled()) {
            return;
        }
        Block block = GameManager.get().getWorld().getHighestBlockAt(0, 0);
        block.setType(Material.ENDER_CHEST);
    }
}
