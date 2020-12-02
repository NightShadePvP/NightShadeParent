package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class HobbitScenario extends Scenario implements StarterItems {
  public HobbitScenario(){
    super("Hobbit", " At the start of the game, everyone will be given a golden nugget(which signifies as your own ring). Once you right click it, it will disappear from your inventory and will give invisibility for 30 seconds.", new ItemBuilder(Material.GOLD_NUGGET).name("Hobbit").make());
  }

  @EventHandler
  public void onClick(PlayerInteractEvent e){
    if(!isEnabled()) return;

    if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
      if(e.getItem().getType() == Material.GOLD_NUGGET){
        ItemMeta meta = e.getItem().getItemMeta();
        if(meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "The Ring")) {
          e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*30, 0));
          e.getPlayer().getInventory().remove(e.getItem());
          e.getPlayer().sendMessage(ChatUtils.format(getPrefix() + "&eYou hath Hobbit - ed?"));
        }
      }
    }
  }

  @Override
  public List<ItemStack> getStarterItems() {
    return MUtil.list(new ItemBuilder(Material.GOLD_NUGGET).name(ChatColor.YELLOW + "The Ring").lore("This is the Hobbit Ring! Rick click it to become invisible for 30 seconds!").make());
  }

  @Override
  public int getStartingLevels() {
    return 0;
  }
}
