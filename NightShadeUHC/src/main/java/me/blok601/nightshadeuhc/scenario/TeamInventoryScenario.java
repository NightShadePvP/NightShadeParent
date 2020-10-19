package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.CustomDeathEvent;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Blok on 9/28/2018.
 */
public class TeamInventoryScenario extends Scenario{

    public static HashMap<Team, Inventory> teamInventories = null;
    public static HashMap<UUID, Inventory> soloInventories = null;

    public TeamInventoryScenario() {
        super("Team Inventory", "Each team gets their own shared inventory", "TI", new ItemBuilder(Material.ENDER_CHEST).name("Team Inventory").make());
    }

    @EventHandler
    public void onStart(GameStartEvent e){
        if (!isEnabled()) return;
        if(teamInventories == null){
            teamInventories = new HashMap<>();
        }

        if(soloInventories == null){
            soloInventories = new HashMap<>();
        }

        if(teamInventories.size() != 0) teamInventories.clear();
        if(soloInventories.size() != 0) soloInventories.clear();

        for (Team team : TeamManager.getInstance().getTeams()){
            teamInventories.put(team, Bukkit.createInventory(null, 27, "Team Inventory"));
        }

        UHCPlayerColl.get().getAllOnline().stream()
                .filter(uhcPlayer -> !uhcPlayer.isSpectator())
                .filter(uhcPlayer -> TeamManager.getInstance().getTeam(uhcPlayer.getPlayer()) == null)
                .forEach(uhcPlayer -> soloInventories.put(uhcPlayer.getUuid(), Bukkit.createInventory(null, 27, "Team Inventory")));
    }

//    @EventHandler
//    public void onDeath(CustomDeathEvent event){
//        if(!isEnabled()) return;
//
//        Player dead = event.getKilled();
//
//        if(TeamManager.getInstance().getTeam(dead) == null){
//            for (ItemStack stack : soloInventories.get(dead.getUniqueId()).getContents()){
//                if(stack == null || stack.getType() == Material.AIR) continue;
//                dead.getWorld().dropItemNaturally(dead.getLocation(), stack);
//            }
//        }else{
//            Team team = TeamManager.getInstance().getTeam(dead);
//            if(team.getOnlineAliveMembers())
//        }
//
//    }
}
