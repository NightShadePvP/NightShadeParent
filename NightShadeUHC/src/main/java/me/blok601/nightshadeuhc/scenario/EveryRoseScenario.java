package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EveryRoseScenario extends Scenario implements StarterItems {
  public EveryRoseScenario() {
    super("EveryRose", "EVERRRYYYYY ROSEEE HAS ITS THORNSSSSSSS (Players get a Thorns 3 Golden Chestplate On game Start)", new ItemBuilder(Material.RED_ROSE).name("Every Rose").make());
  }

  @Override
  public List<ItemStack> getStarterItems() {
    return MUtil.list(new ItemBuilder(Material.GOLD_CHESTPLATE).enchantment(Enchantment.THORNS, 3).make());
  }

  @Override
  public int getStartingLevels() {
    return 0;
  }
}
