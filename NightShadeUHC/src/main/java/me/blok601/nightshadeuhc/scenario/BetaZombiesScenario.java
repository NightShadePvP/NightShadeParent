package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class BetaZombiesScenario extends Scenario{

    public BetaZombiesScenario() {
        super("Beta Zombies", "Zombies drop feathers.", new ItemBuilder(Material.FEATHER).name("Beta Zombies").make());
    }

    @EventHandler
    public void on(EntityDeathEvent e){
        if(!isEnabled()) return;

        e.getDrops().stream().filter(itemStack -> itemStack.getType() == Material.ROTTEN_FLESH).forEach(itemStack -> itemStack.setType(Material.AIR));

        if(e.getEntity() instanceof Zombie){
            e.getDrops().add(new ItemStack(Material.FEATHER, 2));
        }
    }
}
