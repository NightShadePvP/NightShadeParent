package me.blok601.nightshadeuhc.component;

import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class FriendlyFireComponent extends Component{

    public FriendlyFireComponent() {
        super("Friendly Fire", Material.STONE_SWORD, false, "Whether teams can hurt each other or not");
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e) {
        if(isEnabled()) return; //team ff is on


        if (e.getEntity() instanceof Player) {
            Player damaged = (Player) e.getEntity(), damager = null;

            if (e.getDamager() instanceof Player) {
                damager = (Player) e.getDamager();
            } else if (e.getDamager() instanceof Projectile) {
                if (((Projectile) e.getDamager()).getShooter() instanceof Player) {
                    damager = (Player) ((Projectile) e.getDamager()).getShooter();
                }
            }

            if (TeamManager.getInstance().getTeam(damaged) == null || TeamManager.getInstance().getTeam(damaged) == null) {
                return;
            }

            if (damager != null) {
                Team a = TeamManager.getInstance().getTeam(damaged), b = TeamManager.getInstance().getTeam(damager);

                if (a.equals(b)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
