package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.ImmutableList;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

//Created By Ozzy, All by himself. July 20th, 2028
public class BomberScenario extends Scenario implements StarterItems {
  public BomberScenario(){
    super("Bombers", "Mobs and Animals drop TNT. Everyone receives an unbreakable Flint and Steel on start.", new ItemBuilder(Material.EXPLOSIVE_MINECART).name("Bombers").make());
  }

  @EventHandler
  public void on(EntityDeathEvent e){
    if(!isEnabled()) return;

    if(e.getEntity() instanceof Monster){
      e.getDrops().add(new ItemStack(Material.TNT, 3));
    }
    if(e.getEntity() instanceof Sheep) {
      e.getDrops().add(new ItemStack(Material.TNT, 2));
    }
    if(e.getEntity() instanceof Cow) {
      e.getDrops().add(new ItemStack(Material.TNT, 2));
    }
    if(e.getEntity() instanceof Pig) {
      e.getDrops().add(new ItemStack(Material.TNT, 2));
    }
    if(e.getEntity() instanceof Chicken) {
      e.getDrops().add(new ItemStack(Material.TNT, 2));
    }
    if(e.getEntity() instanceof Rabbit) {
      e.getDrops().add(new ItemStack(Material.TNT, 2));

    }
  }


  @EventHandler
  public void onBreak (PlayerItemBreakEvent e) {
    Player p = e.getPlayer();
    if (e.getBrokenItem().getType() == Material.FLINT_AND_STEEL) {
      p.getInventory().addItem(new ItemStack(Material.FLINT_AND_STEEL));
    }
  }


  @Override
  public List<ItemStack> getStarterItems() {

    ItemStack itemStack = new ItemStack(Material.FLINT_AND_STEEL);
    ItemMeta meta = itemStack.getItemMeta();
    meta.spigot().setUnbreakable(true);

    itemStack.setItemMeta(meta);

    return ImmutableList.of(new ItemStack(Material.FLINT_AND_STEEL));
  }
}
