package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Blok on 2/10/2018.
 */
public class VillagerMadnessScenario extends Scenario implements StarterItems {

    public VillagerMadnessScenario() {
        super("Villager Madness", "At the start of the game every player receives a stack of emerald blocks to trade with villagers", new ItemBuilder(Material.EMERALD_BLOCK).name("Villager Madness").make());
    }


    @Override
    public List<ItemStack> getStarterItems() {
        return MUtil.list(new ItemStack(Material.EMERALD_BLOCK, 64), new ItemStack(Material.MONSTER_EGG, 64, (short) 120));
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
