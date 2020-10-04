package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;

public class GapZapScenario extends Scenario {
  public GapZapScenario(){
    super("GapZap", "If you take damage while regenerating from a Golden Apple or Golden Head, the effect is removed, along with the Absorption", new ItemBuilder(Material.GOLDEN_APPLE).name("Gap Zap").make());
  }
  @EventHandler
  public void onDamage(EntityDamageEvent e) {
    if(!isEnabled()) return;

    if(!(e.getEntity() instanceof Player)){
      return;
    }
    Player p = (Player) e.getEntity();
    if (p.hasPotionEffect(PotionEffectType.REGENERATION)) {
      p.removePotionEffect(PotionEffectType.REGENERATION);
      p.removePotionEffect(PotionEffectType.ABSORPTION);
    }
  }

}
