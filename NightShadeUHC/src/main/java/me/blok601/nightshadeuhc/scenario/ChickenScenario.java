package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChickenScenario extends Scenario implements StarterItems {
  public ChickenScenario() {
    super("Chicken", "Everyone starts on half a heart, with a god apple.", new ItemBuilder(Material.RAW_CHICKEN).name("Chicken").make());
  }
  @EventHandler
  public void onStart(GameStartEvent e) {
    if (!isEnabled()) {
      return;
    }

    if (getScenarioManager().getScen("OneHundredHearts").isEnabled()) {
      UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> uhcPlayer.getPlayer().setHealth(1.0));
      UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> uhcPlayer.getPlayer().setMaxHealth(200D));

    }

    else {


      UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> uhcPlayer.getPlayer().setHealth(1.0));
    }
  }


  @Override
  public List<ItemStack> getStarterItems() {
    return MUtil.list(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1));
  }

  @Override
  public int getStartingLevels() {
    return 0;
  }
}
