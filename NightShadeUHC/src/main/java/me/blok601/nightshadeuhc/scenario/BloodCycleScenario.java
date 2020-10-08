package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.ImmutableList;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BloodCycleScenario extends Scenario {

    private UHC plugin;
    private HashMap<Material, Double> chances = null;
    private Material currentItem = null;
    private List<Material> materials;
    private int rand;
    private int timer;

    public BloodCycleScenario(UHC plugin) {
        super("BloodCycle", "In this gamemode every 10 minutes it will cycle to a new ore, these ores consist of Emerald, Diamond, Gold, Iron, Coal, Lapis, and Redstone. If it cycles to one of these ores, when you mine it; it has a chance of damaging you by half-a-heart, but you could it lucky and it will cycle to no ore.", new ItemBuilder(Material.REDSTONE_ORE).name("Bloodcycle").make());
        this.plugin = plugin;
        rand = 0;
        timer = 600;
        materials = ImmutableList.of(Material.DIAMOND_ORE, Material.GOLD_ORE, Material.EMERALD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.COAL_ORE, Material.IRON_ORE);
    }

    @Override
    public void onToggle(boolean newState) {
        if (newState) {
            chances = new HashMap<>();
            chances.put(Material.DIAMOND_ORE, 0.75);
            chances.put(Material.GOLD_ORE, 0.50);
            chances.put(Material.EMERALD_ORE, 0.40);
            chances.put(Material.LAPIS_ORE, 0.30);
            chances.put(Material.REDSTONE_ORE, 0.35);
            chances.put(Material.COAL, 0.15);
            chances.put(Material.IRON_ORE, 0.15);
        } else {
            if (chances != null) {
                chances.clear();
            }
        }
    }

    @EventHandler
    public void onStart(GameStartEvent event) {
        if(!isEnabled()) return;

        rand = new Random().nextInt(7);
        //pick an item
        currentItem = materials.get(rand);
        PlayerUtils.broadcastSound(Sound.BAT_DEATH);
        broadcast("&eThe cycle is now on " + com.nightshadepvp.core.utils.ChatUtils.oreToString(currentItem) + "&e.");
        broadcast("&eUpon mining this item, there is a &b"  + (chances.get(currentItem) * 100) + "% &echance of being dealt half a heart! Be cautious...");


        new BukkitRunnable(){
            @Override
            public void run() {
                if(!isEnabled()){
                    cancel();
                    return;
                }
                timer-=30;
                if(timer == 0){
                    timer = 600;
                    //New cycle
                    rand = new Random().nextInt(7);
                    //pick an item
                    currentItem = materials.get(rand);
                    PlayerUtils.broadcastSound(Sound.BAT_DEATH);
                    broadcast("&eThe cycle is now on " + com.nightshadepvp.core.utils.ChatUtils.oreToString(currentItem) + "&e.");
                    broadcast("&eUpon mining this item, there is a &b"  + (chances.get(currentItem) * 100) + "% &echance of being dealt half a heart! Be cautious...");
                }
            }
        }.runTaskTimer(plugin, 0, 20*30);
    }

    @EventHandler (ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event){
        if(!isEnabled()) return;
        if(!GameState.gameHasStarted()) return;

        Material mat = event.getBlock().getType();
        Player player = event.getPlayer();
        if(mat.equals(currentItem)){
            if(MathUtils.getChance(chances.get(currentItem))){
                player.damage(1);
                PlayerUtils.playSound(Sound.GHAST_CHARGE, player);
                sendMessage(player, "&4You have been dealt half a heart of damage for breaking a " + ChatUtils.oreToString(mat));
            }
        }
    }
}
