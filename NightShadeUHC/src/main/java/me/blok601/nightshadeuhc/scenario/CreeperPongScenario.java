package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CreeperPongScenario extends Scenario implements StarterItems {
  public CreeperPongScenario() {
    super("CreeperPong", "Players get a Knockback 10 stick, and 20 Creeper Eggs", new ItemBuilder(Material.STICK).name("CreeperPong").make());
  }


  @Override
  public List<ItemStack> getStarterItems() {
    return MUtil.list(new ItemBuilder(Material.STICK).enchantment(Enchantment.KNOCKBACK, 10).make(), new ItemStack(Material.MONSTER_EGG, 20, (short) 50));
  }

  @Override
  public int getStartingLevels() {
    return 0;
  }
}
