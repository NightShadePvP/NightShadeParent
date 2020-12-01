package me.blok601.nightshadeuhc.component;

import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.manager.GameManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class CobbleComponent extends Component{


    public CobbleComponent() {
        super("1.8 Stone", Material.COBBLESTONE, false, "Toggle whether 1.8 stone blocks drop cobblestone");
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(isEnabled()){
            return;
        }

        if(!GameState.gameHasStarted()){
            e.setCancelled(true);
            return;
        }

        if (GameManager.get().getWorld() == null) {
            e.setCancelled(true);
            return;
        }

        if (!GameManager.get().getWorld().getName().equalsIgnoreCase(e.getBlock().getWorld().getName())) {
            e.setCancelled(true);
            return;
        }

        if(e.getBlock().getType() == Material.STONE){ //All 1.8 stones are considered stone and have mat data
            e.setCancelled(true);
            e.getBlock().setType(Material.AIR);
            e.getBlock().getWorld().dropItem(e.getBlock().getLocation().add(0.5, 0.5, 0.5), new ItemStack(Material.COBBLESTONE));
        }
    }
}
