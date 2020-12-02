package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PuppyPowerScenario extends Scenario implements StarterItems {


    public PuppyPowerScenario() {
        super("Puppy Power", "Everyone starts with 64 Bones, 64 Rotten Flesh, and 64 Wolf Spawn Eggs", new ItemBuilder(Material.BONE).name("Puppy Power").make());
    }

    @Override
    public List<ItemStack> getStarterItems() {
        return MUtil.list(new ItemStack(Material.BONE, 64), new ItemStack(Material.ROTTEN_FLESH, 64), new ItemStack(Material.MONSTER_EGG, 64, (short) 95));
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
