package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Created by Blok on 6/24/2018.
 */
public class AurophobiaScenario extends Scenario{


    public AurophobiaScenario() {
        super("Aurophobia", "Whenever you mine Gold you have a chance of getting damaged by a heart, getting spawned 2 silverfish on you, losing the gold, getting blindness for 10 seconds, or getting a cave spider spawned on you.", "AP", new ItemBuilder(Material.GOLD_ORE).name("Aurophobia").make());
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(!isEnabled()) return;

        Player p = e.getPlayer();

        if(e.getBlock().getType() == Material.GOLD_ORE){
            if(MathUtils.getChance(5)){
                p.damage(2);
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou were unlucky and took 1 heart of damage!"));
            }else if(MathUtils.getChance(5)){
                p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.SILVERFISH);
                p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.SILVERFISH);
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou were unlucky and 2 silverfish were spawned!"));
            }else if(MathUtils.getChance(5)){
                e.setCancelled(true);
                e.getBlock().setType(Material.AIR);
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou were unlucky lost the gold!"));
            }else if(MathUtils.getChance(5)){
                p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 2));
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou were unlucky and are now blinded!"));
            }else if(MathUtils.getChance(3)){
                p.getLocation().getWorld().spawnEntity(p.getLocation(), EntityType.CAVE_SPIDER);
                p.sendMessage(ChatUtils.format(getPrefix() + "&eYou were unlucky and a cave spider appeared!"));
            }
        }
    }
}
