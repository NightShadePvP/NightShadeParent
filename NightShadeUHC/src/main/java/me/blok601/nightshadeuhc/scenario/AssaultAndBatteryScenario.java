package me.blok601.nightshadeuhc.scenario;

import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.event.ScenarioEnableEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Collections;

/**
 * Created by Blok on 6/24/2018.
 */
public class AssaultAndBatteryScenario extends Scenario {


    public AssaultAndBatteryScenario() {
        super("Assault and Battery", "To2 Where one person can only do meelee damage to players, while the other one can only do ranged attacks. If a teammate dies, you can do both meelee and ranged attacks.", "AAB", new ItemBuilder(Material.WOOD_SWORD).name("Assault and Battery").make());
    }

    @EventHandler
    public void onEnable(ScenarioEnableEvent e) {
        if (e.getScenario().getName().equalsIgnoreCase(getName())) {
            if (TeamManager.getInstance().getTeamSize() != 2 || !GameManager.get().isIsTeam()) {
                e.setCancelled(true);
                e.setMessage("&cAssault and Battery can only be enabled in Teams of 2!");
                e.getPlayer().closeInventory();
                return;
            }

            if (GameState.gameHasStarted()) {
                e.getPlayer().sendMessage(ChatUtils.message("&eAssigned Assault and Battery teams now!"));
                assign();
            }

        }
    }

    @EventHandler
    public void onStart(GameStartEvent e) {
        if (!isEnabled()) return;
        Bukkit.broadcastMessage(ChatUtils.format(getPrefix() + "&eTeams have been set!"));
        assign();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {

        if (!isEnabled()) return;

        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getEntity();

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Team team = TeamManager.getInstance().getTeam(damager);
            if (team != null) {
                if (!team.getMelee().equals(damager.getUniqueId())) {
                    e.setCancelled(true);
                    e.setDamage(0);
                    damager.sendMessage(ChatUtils.format(getPrefix() + "&cYou can't use melee attacks!"));
                    return;
                }
            }
        }

        if (e.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) e.getDamager();
            if (projectile instanceof FishHook) return;
            if (projectile.getShooter() instanceof Player) {
                Player shooter = (Player) projectile.getShooter();
                Team team = TeamManager.getInstance().getTeam(shooter);
                if (team != null) {
                    if (!PlayerUtils.isPlaying(team.getBow())) {
                        return; //Allow
                    }

                    if (!team.getBow().equals(shooter.getUniqueId())) {
                        e.setCancelled(true);
                        e.setDamage(0);
                        shooter.sendMessage(ChatUtils.format(getPrefix() + "&cYou can't use projectile attacks!"));
                        return;
                    }
                }
            }
        }

    }

    private void assign() {
        Player tempPlayer;
        for (Team team : TeamManager.getInstance().getTeams()) {
            Collections.shuffle(team.getMembers());
            if (team.getMembers().size() == 0) {
                tempPlayer = Bukkit.getPlayer(team.getMembers().get(0));
                if (tempPlayer == null) {
                    team.setBow(Bukkit.getOfflinePlayer(team.getMembers().get(0)).getUniqueId());
                    team.setMelee(Bukkit.getOfflinePlayer(team.getMembers().get(0)).getUniqueId());
                    continue;
                } else {
                    tempPlayer.sendMessage(ChatUtils.format(getPrefix() + "&eYou can do both melee and ranged attacks since you are solo!"));
                    continue;
                }
            }
            tempPlayer = Bukkit.getPlayer(team.getMembers().get(0));
            if (tempPlayer == null) {
                team.setMelee(Bukkit.getOfflinePlayer(team.getMembers().get(0)).getUniqueId());
            } else {
                team.setMelee(tempPlayer.getUniqueId());
                tempPlayer.sendMessage(ChatUtils.format(getPrefix() + "&eYou can only use melee attacks!"));
            }
            tempPlayer = Bukkit.getPlayer(team.getMembers().get(1));
            if (tempPlayer == null) {
                team.setBow(Bukkit.getOfflinePlayer(team.getMembers().get(1)).getUniqueId());
            } else {
                tempPlayer.sendMessage(ChatUtils.format(getPrefix() + "&eYou can only do ranged attacks!"));
                team.setBow(tempPlayer.getUniqueId());
            }
        }
    }


}
