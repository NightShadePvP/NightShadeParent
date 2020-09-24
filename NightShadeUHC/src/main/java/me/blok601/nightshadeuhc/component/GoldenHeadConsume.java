package me.blok601.nightshadeuhc.component;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Blok on 7/22/2017.
 */
public class GoldenHeadConsume extends Component {

    public GoldenHeadConsume() {
        super("Golden Heads", Material.GOLDEN_APPLE, true, "Toggle whether Golden Heads should be enabled");
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent e){
        if(!isEnabled()) return;
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if(item.getType() == Material.GOLDEN_APPLE){

            if(!item.hasItemMeta()){
                return;
            }

            if(item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.GOLD + "Golden Head")){
                p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25*8, 1));
            }
        }
    }
}
