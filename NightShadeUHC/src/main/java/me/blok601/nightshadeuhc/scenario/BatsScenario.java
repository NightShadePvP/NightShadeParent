package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * Created by Blok on 6/29/2017.
 */
public class BatsScenario extends Scenario{

    public BatsScenario() {
        super("Bats", "Upon the death of a bat, there is a 95% chance of a Golden Apple dropping, but 5% chance of death", new ItemBuilder(Material.GOLDEN_APPLE).name("Bats").make());
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e){
        if(!isEnabled()){
            return;
        }

        if(e.getEntity().getKiller() == null){
            return;
        }


        if(e.getEntity() instanceof Bat){
            if(MathUtils.getChance(95)){
                e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), new ItemBuilder(Material.GOLDEN_APPLE).make());
            }else{
                if(e.getEntity().getKiller() != null){
                    Player p = e.getEntity().getKiller();
                    p.damage(0);
                    p.setHealth(0);
                    Bukkit.broadcastMessage(ChatUtils.format(getPrefix() + "&b" + p.getName() + " was killed by bats!"));
                }
            }
        }
    }
}
