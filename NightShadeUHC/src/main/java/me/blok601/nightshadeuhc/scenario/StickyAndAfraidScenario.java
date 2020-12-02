package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.Lists;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Blok on 2/2/2019.
 */
public class StickyAndAfraidScenario extends Scenario implements StarterItems {


    public StickyAndAfraidScenario() {
        super("Sticky and Afraid", "Every Player will start with silk touch shears and a stack of cobwebs and will start with three cave spider eggs, Cave spiders can move faster in cobwebs so this can be useful for making traps.", new ItemStack(Material.MONSTER_EGG, 1, (short) 59));
    }

    @Override
    public List<ItemStack> getStarterItems() {
        return Lists.newArrayList(new ItemBuilder(Material.SHEARS).enchantment(Enchantment.SILK_TOUCH).make(),
                new ItemStack(Material.WEB, 64), new ItemStack(Material.MONSTER_EGG, 3, (short) 59));
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
