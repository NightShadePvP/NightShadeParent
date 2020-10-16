package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class PlayerSwapScenario extends Scenario{

    private BukkitTask task;
    private UHC plugin;
    private Random random;
    public PlayerSwapScenario(UHC plugin) {
        super("Player Swap", "Every 5 minutes, 2 random players switch locations", new ItemBuilder(Material.COMPASS).name("Player Swap").make());
        this.plugin = plugin;
    }


    @EventHandler
    public void onStart(GameStartEvent event){
        if(!isEnabled()) return;
        this.random = new Random();
        task = new BukkitRunnable() {
            int counter = 300;
            @Override
            public void run() {
                if(!isEnabled()){
                    cancel();
                    return;
                }

                counter-=30;
                if(counter == 0){
                    //Find two players
                    List<UHCPlayer> coll = UHCPlayerColl.get().getAllPlaying();
                    if(coll.size() == 1){
                        broadcast("&cThere were not enough players to do swaps! Trying again in 30 seconds!");
                        counter=30;
                        return;
                    }
                    UHCPlayer uhcPlayer1 = coll.get(random.nextInt(coll.size()));
                    coll.remove(uhcPlayer1);
                    UHCPlayer uhcPlayer2 = coll.get(random.nextInt(coll.size()));

                    //Grab locations
                    Location location1 = uhcPlayer1.getPlayer().getLocation();
                    Location location2 = uhcPlayer2.getPlayer().getLocation();

                    //Teleport
                    uhcPlayer1.getPlayer().teleport(location2);
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            uhcPlayer2.getPlayer().teleport(location1);
                            sendMessage(uhcPlayer1.getPlayer(), "&bYou have swapped locations with &f" + uhcPlayer2.getName());
                            sendMessage(uhcPlayer2.getPlayer(), "&bYou have swapped locations with &f" + uhcPlayer1.getName());
                            broadcast("&f" + uhcPlayer1.getName() + " &band &f" + uhcPlayer2.getName() + " &bhave swapped locations!");
                            counter=300;
                        }
                    }.runTaskLater(plugin, 1);

                }
            }
        }.runTaskTimer(plugin, 20*30, 20*30);
    }
}
