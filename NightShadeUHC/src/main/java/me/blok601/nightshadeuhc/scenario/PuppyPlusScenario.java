package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PuppyPlusScenario extends Scenario implements StarterItems {


    public PuppyPlusScenario() {
        super("Puppy Power +", "Everyone starts with 3 stacks of Bones, 3 stacks of Rotten Flesh, and 3 stacks of Wolf Spawn Eggs\n", new ItemBuilder(Material.RED_ROSE).name("Puppy Power +").make());
    }


    @Override
    public List<ItemStack> getStarterItems() {
        return MUtil.list(new ItemStack(Material.BONE, 192),
                new ItemStack(Material.ROTTEN_FLESH, 192),
                new ItemStack(Material.MONSTER_EGG, 192, (short) 95));
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
