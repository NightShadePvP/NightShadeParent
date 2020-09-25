package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Blok on 4/21/2019.
 */
public class BaldChickenScenario extends Scenario {

    public BaldChickenScenario() {
        super("Bald Chicken", "Chicken's drop no feathers. Skeletons drop 15-20 arrows.", new ItemBuilder(Material.ARROW).amount(5).name("Bald Chicken").make());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChicken(EntityDeathEvent event) {
        if (!isEnabled()) return;

        Entity entity = event.getEntity();
        if (entity instanceof Chicken) {
            //event.getDrops().stream().filter(itemStack -> itemStack.getType() == Material.FEATHER).forEach(itemStack -> itemStack.setType(Material.AIR));
            event.getDrops().removeIf(itemStack -> itemStack.getType() == Material.FEATHER);
        } else if (entity instanceof Skeleton) {
            //event.getDrops().stream().filter(itemStack -> itemStack.getType() == Material.ARROW).forEach(itemStack -> itemStack.setType(Material.AIR));
            event.getDrops().removeIf(itemStack -> itemStack.getType() == Material.ARROW);
            event.getDrops().add(new ItemStack(Material.ARROW, MathUtils.getRand(15, 20)));
        }
    }

}
