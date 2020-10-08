package me.blok601.nightshadeuhc.scenario;

import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class VeinminerScenario extends Scenario {

    public VeinminerScenario() {
        super("Veinminer", "When an ore is mined, the other ores are broken around it", new ItemBuilder(Material.DIAMOND_PICKAXE).name("Veinminer").make());
    }

    @EventHandler
    public void on(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block block = e.getBlock();

        if (!isEnabled()) {
            return;
        }

        if (!p.isSneaking()) {
            return;
        }

        if (block.getType() == Material.COAL_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.COAL_ORE);
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;
            PlayerUtils.giveItem(new ItemStack(Material.COAL, blocks.size()), p);
            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

        if (block.getType() == Material.IRON_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.IRON_ORE);
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;
            if (getScenarioManager().getScen("CutClean").isEnabled()) {
                PlayerUtils.giveItem(new ItemStack(Material.IRON_INGOT, blocks.size()), p);
            } else {
                PlayerUtils.giveItem(new ItemStack(Material.IRON_ORE, blocks.size()), p);
            }

            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

        if (block.getType() == Material.GOLD_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.GOLD_ORE);
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;

            if (getScenarioManager().getScen("CutClean").isEnabled()) {
                PlayerUtils.giveItem(new ItemStack(Material.GOLD_INGOT, blocks.size()), p);
            } else {
                PlayerUtils.giveItem(new ItemStack(Material.GOLD_ORE, blocks.size()), p);
            }
            ChatUtils.sendMiningMessage(false, p, blocks.size());


            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

        if (block.getType() == Material.DIAMOND_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.DIAMOND_ORE);
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;
            PlayerUtils.giveItem(new ItemStack(Material.DIAMOND, blocks.size()), p);
            ChatUtils.sendMiningMessage(true, p, blocks.size());

            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

        if (block.getType() == Material.REDSTONE_ORE || block.getType() == Material.GLOWING_REDSTONE_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.REDSTONE_ORE);
            blocks.addAll(getBlocks(block, 2, Material.GLOWING_REDSTONE_ORE));
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;
            PlayerUtils.giveItem(new ItemStack(Material.REDSTONE, blocks.size() * 4), p);
            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

        if (block.getType() == Material.LAPIS_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.LAPIS_ORE);
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;
            PlayerUtils.giveItem(new ItemStack(Material.INK_SACK, blocks.size() * 4, DyeColor.BLUE.getDyeData()), p);

            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

        if (block.getType() == Material.EMERALD_ORE) {
            ArrayList<Block> blocks = getBlocks(block, 2, Material.EMERALD_ORE);
            for (Block b : blocks) {
                b.setType(Material.AIR);
            }

            if (blocks.size() == 0) return;
            PlayerUtils.giveItem(new ItemStack(Material.EMERALD, blocks.size()), p);

            ((ExperienceOrb) block.getWorld().spawn(block.getLocation(), ExperienceOrb.class)).setExperience(2 * blocks.size());
        }

    }


    public ArrayList<Block> getBlocks(Block start, int radius, Material filter) {
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (double x = start.getLocation().getX() - radius; x <= start.getLocation().getX() + radius; x++) {
            for (double y = start.getLocation().getY() - radius; y <= start.getLocation().getY() + radius; y++) {
                for (double z = start.getLocation().getZ() - radius; z <= start.getLocation().getZ() + radius; z++) {
                    Location loc = new Location(start.getWorld(), x, y, z);
                    if (loc.getBlock().getType() == filter)
                        blocks.add(loc.getBlock());
                }
            }
        }
        return blocks;

    }
}
