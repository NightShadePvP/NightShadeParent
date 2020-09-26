package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Blok on 1/28/2018.
 */
public class BowlessScenario extends Scenario{

    public BowlessScenario(){
        super("Bowless", "Bows can't be crafted", new ItemBuilder(Material.BOW).name("Bowless").make());
    }

    @EventHandler(ignoreCancelled = true)
    public void on(CraftItemEvent event)  {
        if(!isEnabled()) return;
        ItemStack item = event.getCurrentItem();

        if (item == null) {
            return;
        }

        if (item.getType() != Material.BOW) {
            return;
        }

        sendMessage((Player) event.getWhoClicked(), "&cBows are disabled in bowless!");
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(PlayerPickupItemEvent e) {
        if (!isEnabled()) return;

        Item item = e.getItem();
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getType() != Material.BOW) return;

        e.setCancelled(true);
        item.remove();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!isEnabled()){
            return;
        }

        if(e.getInventory() == null) return;
        if(e.getWhoClicked() == null) return;
        if(e.getClick() == null) return;
        if(e.getCurrentItem() == null) return;

        if(e.getCurrentItem().getType() == Material.BOW){
            e.getCurrentItem().setType(Material.AIR); //Chests, Villagers, etc
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent e){
        if(!isEnabled()){
            return;
        }

        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(p.getItemInHand().getType() == Material.BOW){
                e.setCancelled(true);
                p.sendMessage(ChatUtils.message(getPrefix() + "&cYou can't use a bow in bowless!"));
            }
        }
    }

    @EventHandler
    public void onhit (EntityDamageByEntityEvent e) {
        if (!isEnabled()) return;
        if (e.getDamager() instanceof Arrow) {
            e.setCancelled(true);
        }
    }

}
