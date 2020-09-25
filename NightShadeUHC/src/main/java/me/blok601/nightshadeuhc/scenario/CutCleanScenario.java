package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Blok on 6/17/2017.
 */
public class CutCleanScenario extends Scenario{
    

    public CutCleanScenario() {
        super("CutClean", "Everything is pre-smelted", new ItemBuilder(Material.COOKED_BEEF).name("CutClean").make());
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void on(ItemSpawnEvent event) {

        if (!isEnabled()) return;

        Item drop = event.getEntity();
        ItemStack item = drop.getItemStack();


        if (item.getType() == Material.GOLD_ORE) {
            drop.setItemStack(new ItemStack(Material.GOLD_INGOT, item.getAmount(), item.getDurability()));

            return;
        }

        if (item.getType() == Material.IRON_ORE) {
            drop.setItemStack(new ItemStack(Material.IRON_INGOT, item.getAmount(), item.getDurability()));
            return;
        }


        if (item.getType() == Material.POTATO_ITEM) {
            drop.setItemStack(new ItemStack(Material.BAKED_POTATO, item.getAmount(), item.getDurability()));
        }

        if (item.getType() == Material.RAW_FISH) {
            if (item.getDurability() == 0 || item.getDurability() == 1) {
                drop.setItemStack(new ItemStack(Material.COOKED_FISH, item.getAmount(), item.getDurability()));
            }
        }
    }


    /*
    Fucking ass cunt dick

    XP fixes
     */

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e){

        Player p = e.getPlayer();
        Block b = e.getBlock();
        Location loc = p.getLocation();


        if (b.getType() == Material.COAL_ORE) {
            if (Math.random() * 10 + 1 >= 6) {
                ExperienceOrb orb = e.getPlayer().getWorld().spawn(loc, ExperienceOrb.class);
                orb.setExperience(2);


            }
        }
        if (b.getType() == Material.REDSTONE_ORE || b.getType() == Material.GLOWING_REDSTONE_ORE) {
            ExperienceOrb orb = e.getPlayer().getWorld().spawn(loc, ExperienceOrb.class);
            orb.setExperience(2);

        }
        if (b.getType() == Material.LAPIS_ORE) {
            ExperienceOrb orb = e.getPlayer().getWorld().spawn(loc, ExperienceOrb.class);
            orb.setExperience(2);

        }
        if (!isEnabled()) return;

        if (b.getType() == Material.GOLD_ORE) {
            ExperienceOrb orb = e.getPlayer().getWorld().spawn(loc, ExperienceOrb.class);
            orb.setExperience(2);


        }


        if (b.getType() == Material.IRON_ORE) {
            ExperienceOrb orb = e.getPlayer().getWorld().spawn(loc, ExperienceOrb.class);
            orb.setExperience(2);


        }


    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onDeath(EntityDeathEvent e) {
        if (!isEnabled()) {
            return;
        }

        Entity en = e.getEntity();
        if (en.getType() == EntityType.PIG) {
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
        } else if (en.getType() == EntityType.COW) {
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(Material.COOKED_BEEF, 3));
            e.getDrops().add(new ItemStack(Material.LEATHER, 2));
        } else if (en.getType() == EntityType.SHEEP) {
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(Material.GRILLED_PORK, 3));
        } else if (en.getType() == EntityType.CHICKEN) {
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(Material.COOKED_CHICKEN, 3));
            e.getDrops().add(new ItemStack(Material.FEATHER, 2));
        }else if(en.getType() == EntityType.RABBIT){
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(Material.COOKED_MUTTON, 3));
            e.getDrops().add(new ItemStack(Material.LEATHER, 2));
        }else if(en.getType() == EntityType.HORSE){
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(Material.LEATHER, MathUtils.getRand(1, 4)));
        }
    }
}
