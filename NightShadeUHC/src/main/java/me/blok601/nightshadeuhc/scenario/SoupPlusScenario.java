package me.blok601.nightshadeuhc.scenario;

import com.massivecraft.massivecore.util.MUtil;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Blok on 7/19/2018.
 */
public class SoupPlusScenario extends Scenario implements StarterItems {

    public SoupPlusScenario() {
        super("Soup+", "Eating a soup will heal you 2.5 hearts, and you start with materials to make soup", new ItemBuilder(Material.MUSHROOM_SOUP).name("Soup+").make());
    }

    @EventHandler
    public void onEat(PlayerInteractEvent e){
        if(!isEnabled()) return;

        Player p = e.getPlayer();

        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {

            if (p.getItemInHand().getType() == Material.MUSHROOM_SOUP){
                if(p.getHealth() >= p.getMaxHealth()-5){
                    p.setHealth(p.getMaxHealth());
                }else{
                    p.setHealth(p.getHealth()+5);
                }
                p.getItemInHand().setType(Material.BOWL);
            }


        }
    }


    @Override
    public List<ItemStack> getStarterItems() {
        return MUtil.list(new ItemStack(Material.RED_MUSHROOM, 64), new ItemStack(Material.BROWN_MUSHROOM, 64), new ItemStack(Material.BOWL, 64));
    }

    @Override
    public int getStartingLevels() {
        return 0;
    }
}
