package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class DoubleOrNothingScenario extends Scenario {

    public DoubleOrNothingScenario() {
        super("Double or Nothing", "On mine of iron, diamond, emerald or gold ore you have a 50% chance of 2 of the ore dropping or no ores dropping", new ItemBuilder(Material.DIAMOND_PICKAXE).name("&eDouble or Nothing").make());
    }

    private final Random rand = new Random();

    @EventHandler
    public void on(BlockBreakEvent event) {
        if(!isEnabled()) return;
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getDrops(player.getItemInHand()).isEmpty()) {
            return;
        }

        Location location = block.getLocation();

        switch (block.getType()) {
            case IRON_ORE:
                if (rand.nextBoolean()) {
                    PlayerUtils.dropItem(new ItemStack(Material.IRON_ORE, 1), location);
                    event.setExpToDrop(event.getExpToDrop() * 2);
                } else {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                    event.setExpToDrop(0);
                }
                break;
            case GOLD_ORE:
                if (rand.nextBoolean()) {
                    PlayerUtils.dropItem(new ItemStack(Material.GOLD_ORE, 1), location);
                    event.setExpToDrop(event.getExpToDrop() * 2);
                } else {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                    event.setExpToDrop(0);
                }
                break;
            case DIAMOND_ORE:
                if (rand.nextBoolean()) {
                    PlayerUtils.dropItem(new ItemStack(Material.DIAMOND, 1), location);
                    event.setExpToDrop(event.getExpToDrop() * 2);
                } else {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                    event.setExpToDrop(0);
                }
                break;
            case EMERALD_ORE:
                if (rand.nextBoolean()) {
                    PlayerUtils.dropItem(new ItemStack(Material.EMERALD, 1), location);
                    event.setExpToDrop(event.getExpToDrop() * 2);
                } else {
                    event.setCancelled(true);
                    block.setType(Material.AIR);
                    event.setExpToDrop(0);
                }
                break;
            default:
                break;
        }
    }
}
