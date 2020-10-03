package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class TripleOresScenario extends Scenario{
    public TripleOresScenario() {
        super("Triple Ores", "All ores are tripled!", new ItemBuilder(Material.DIAMOND_ORE).name("&eTriple Ores").make());
    }

    @EventHandler
    public void onMine(BlockBreakEvent e) {
        if (!isEnabled()) return;
        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.DIAMOND, 2));
        }
        if (e.getBlock().getType() == Material.IRON_ORE) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.IRON_ORE, 2));
        }
        if (e.getBlock().getType() == Material.GOLD_ORE) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.GOLD_ORE, 2));
        }
        if (e.getBlock().getType() == Material.COAL_ORE) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.COAL, 2));
        }
        if (e.getBlock().getType() == Material.GLOWING_REDSTONE_ORE) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.REDSTONE, 10));
        }
        if (e.getBlock().getType() == Material.LAPIS_ORE) {
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.INK_SACK, 10, (short) 4));
        }
        if(e.getBlock().getType() == Material.EMERALD_ORE){
            e.getPlayer().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(Material.EMERALD_ORE, 2));
        }
    }
}
