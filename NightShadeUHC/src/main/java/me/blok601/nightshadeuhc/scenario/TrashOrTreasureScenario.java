package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Blok on 2/10/2018.
 */
public class TrashOrTreasureScenario extends Scenario {


    public TrashOrTreasureScenario() {
        super("Trash or Treasure", "On mine of Coal ore, there's a 1% chance that a Diamond will drop. On mine of Iron Ore, there's a 2% chance that a diamond will drop. On mine of Redstone, there's a 3% chance that a diamond will drop. On mine of Lapiz, there's a 5% chance that a diamond will drop. On Mine of Gold Ore, there's a 7% chance that a diamond will drop. On mine of Emerald Ore, there's a 9% chance that a diamond will drop. You also cannot mine diamonds.", new ItemBuilder(Material.COAL_BLOCK).name("Trash or Treasure").make());
    }


    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!isEnabled()) {
            return;
        }

        Player p = e.getPlayer();
        Material type = e.getBlock().getType();

        switch (type){
            case REDSTONE_ORE:
                if(MathUtils.getChance(3)){
                    com.nightshadepvp.core.utils.PlayerUtils.giveItem(new ItemStack(Material.DIAMOND), p);
                }
                break;
        }

        if (e.getBlock().getType() == Material.REDSTONE_ORE) {
            if (MathUtils.getChance(3)) {
                e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
            }
        }

        if (e.getBlock().getType() == Material.LAPIS_ORE) {
            if (MathUtils.getChance(5)) {
                e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
            }
        }

        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                e.getPlayer().sendMessage(ChatUtils.format(getPrefix() + "&cYou can't mine diamonds in &6Trash or Treasure!"));
                return;
        }

            if (e.getBlock().getType() == Material.COAL_ORE) {
                if (MathUtils.getChance(1)) {
                    e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
                }
            }

            if (e.getBlock().getType() == Material.IRON_ORE) {
                if (MathUtils.getChance(2)) {
                    e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
                }
            }

            if (e.getBlock().getType() == Material.REDSTONE_ORE) {
                e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
            }

        if (e.getBlock().getType() == Material.EMERALD_ORE) {
            if (MathUtils.getChance(9)) {
                e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND));
            }
        }
    }
}
