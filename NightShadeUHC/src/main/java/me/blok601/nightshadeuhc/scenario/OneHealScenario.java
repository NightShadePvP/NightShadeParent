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

import java.util.List;

/**
 * Created by Blok on 6/21/2018.
 */
public class OneHealScenario extends Scenario implements StarterItems {


    public OneHealScenario() {
        super("OneHeal", "Player's get a golden hoe that they can right click to heal themselves with once throughout the game", new ItemBuilder(Material.GOLD_HOE).name("OneHeal").make());
    }


    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(!isEnabled()) return;

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(e.getItem().getType() == Material.GOLD_HOE){
                ItemMeta meta = e.getItem().getItemMeta();
                if(meta.getDisplayName().equalsIgnoreCase(ChatColor.YELLOW + "Magical Tool")){
                    e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
                    e.getPlayer().setItemInHand(null);
                    e.getPlayer().sendMessage(ChatUtils.format(getPrefix() + "&eYou have redeemed your one heal!"));
                }
            }
        }
    }

    @Override
    public List<ItemStack> getStarterItems() {
        return MUtil.list(new ItemBuilder(Material.GOLD_HOE).name("&eMagical Tool").lore("&6Right click to receive a full heal! Only works once in the game").make());
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
