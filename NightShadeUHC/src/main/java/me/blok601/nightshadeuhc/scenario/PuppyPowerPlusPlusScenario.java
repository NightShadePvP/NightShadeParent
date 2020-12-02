package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PuppyPowerPlusPlusScenario extends Scenario implements StarterItems {

  public PuppyPowerPlusPlusScenario() {
    super("Puppy Power ++", "Everyone starts with 5 stacks of Bones, 5 stacks of Rotten Flesh, and 5 stacks of Wolf Spawn Eggs\n", new ItemBuilder(Material.RED_ROSE).name("Puppy Power ++").make());
  }


  @Override
  public List<ItemStack> getStarterItems() {
    List<ItemStack> toReturn = new ArrayList<>();
    toReturn.add(new ItemStack(Material.BONE, 320));
    toReturn.add(new ItemStack(Material.ROTTEN_FLESH, 320));
    toReturn.add(new ItemStack(Material.MONSTER_EGG, 320, (short) 95));
    return toReturn;
  }

  @Override
  public int getStartingLevels() {
    return 0;
  }
}