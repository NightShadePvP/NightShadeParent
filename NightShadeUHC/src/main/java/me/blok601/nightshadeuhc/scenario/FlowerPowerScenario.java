package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Blok on 4/7/2018.
 */
public class FlowerPowerScenario extends Scenario {


    private ArrayList<Material> flowerTypes = new ArrayList<>(Arrays.asList(Material.YELLOW_FLOWER, Material.DOUBLE_PLANT, Material.RED_ROSE, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.DOUBLE_PLANT));
    private ArrayList<Material> blacklistedMaterials = new ArrayList<>(Arrays.asList(Material.MONSTER_EGG, Material.MONSTER_EGGS, Material.GRASS, Material.COMMAND_MINECART, Material.HOPPER_MINECART, Material.COMMAND, Material.GLOWSTONE, Material.GLOWSTONE_DUST, Material.BARRIER, Material.BEDROCK, Material.BED, Material.ENDER_PORTAL_FRAME));

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    public FlowerPowerScenario() {
        super("FlowerPower", "Breaking flowers gets you random flowerTypes", new ItemBuilder(Material.YELLOW_FLOWER).name("Flower Power").make());
    }


    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        if (!isEnabled()) return;

        Block block = e.getBlock();

        if (flowerTypes.contains(block.getType())) {



            e.setCancelled(true);
            if(block.getType() == Material.DOUBLE_PLANT){
               Block other =  block.getLocation().getWorld().getBlockAt(block.getLocation().add(0, 1, 0));
                other.setType(Material.AIR);
            }
            block.setType(Material.AIR);

            ItemStack item = generateRandomItem();

            block.getWorld().dropItemNaturally(block.getLocation(), item);

        }
    }

    // Always prevent Glowstone (Cuz thats what Jolly told me so :P)
    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {

        if (e.getEntity() instanceof Item) {
            Item item = (Item) e.getEntity();
            if (item.getItemStack().getType() == Material.GLOWSTONE || item.getItemStack().getType() == Material.GLOWSTONE_DUST) {
                item.remove();
            }
        }
    }

    private ItemStack generateRandomItem() {

        ItemStack item = new ItemStack(Material.AIR);

        while (item.getType() == Material.AIR || blacklistedMaterials.contains(item.getType()) || item.getTypeId() == 60) { // The 60 is for farmland, couldn't find a Materiial fori t
            item = new ItemStack(Material.values()[random.nextInt(Material.values().length)]);
        }

        if (item.getMaxStackSize() > 2) {
            item.setAmount(random.nextInt(63) + 1);
        }

        return item;
    }


}
