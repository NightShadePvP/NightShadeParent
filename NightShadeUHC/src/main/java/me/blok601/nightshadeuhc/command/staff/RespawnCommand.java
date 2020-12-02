package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerRespawn;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by Blok on 12/2/2017.
 */
public class RespawnCommand implements UHCCommand {
    @Override
    public String[] getNames() {
        return new String[]{
                "respawn"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if (args.length != 1) {
            p.sendMessage(ChatUtils.message("&cUsage: /respawn <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            String id = args[0].toLowerCase();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(id);
//            if (UHC.players.contains(offlinePlayer.getUniqueId())) {
//                p.sendMessage(ChatUtils.message("&cThat player hasn't died!"));
//                return;
//            }

            if (UHC.loggedOutPlayers.contains(offlinePlayer.getUniqueId())) {
                //They are offline but not dead
                p.sendMessage(ChatUtils.message("&cThat player hasn't died!"));
                return;
            }

            if (!GameManager.get().getInvs().containsKey(offlinePlayer.getUniqueId())) {
                p.sendMessage(ChatUtils.message("&cThat player hasn't died yet!"));
                return;
            }

            if (GameManager.get().getRespawnQueue().contains(id)) {
                p.sendMessage(ChatUtils.message("&cThat offline player is already in the respawn queue!"));
                return;
            }

            GameManager.get().getWhitelist().add(id);
            GameManager.get().getRespawnQueue().add(id);
            p.sendMessage(ChatUtils.message("&eThat offline player has been added to the respawn queue! When they join they will be issued a respawn!"));
            return;
        }

        UHCPlayer targetUHCPlayer = UHCPlayer.get(target);
        if (targetUHCPlayer.getPlayerStatus() == PlayerStatus.PLAYING) {
            p.sendMessage(ChatUtils.message("&cThat player hasn't died!"));
            return;
        }

        if (!GameManager.get().getInvs().containsKey(target.getUniqueId())) {
            p.sendMessage(ChatUtils.message("&cThat player hasn't died yet!"));
            return;
        }

        PlayerRespawn obj = GameManager.get().getInvs().get(target.getUniqueId());


        if (targetUHCPlayer.isVanished()) targetUHCPlayer.unVanish();
        if (targetUHCPlayer.isStaffMode()) {
            Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(p));
            target.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            targetUHCPlayer.setStaffMode(false);
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.chat("/rea");
        }

        target.setGameMode(GameMode.SURVIVAL);
        target.setCanPickupItems(false);
        target.teleport(obj.getLocation());
        List<Entity> nearby = target.getNearbyEntities(3, 3, 3);
        for (Entity entity : nearby){
            if(!(entity instanceof Item)){
                continue;
            }

            entity.remove(); //clear items in a 3 block radius
        }
        target.setCanPickupItems(true);
        if (targetUHCPlayer.isSpectator()) {
            targetUHCPlayer.unspec();
        }
        target.getInventory().setArmorContents(obj.getArmor());
        target.getInventory().setContents(obj.getItems());

        GameManager.get().getInvs().remove(target.getUniqueId());
        targetUHCPlayer.setPlayerStatus(PlayerStatus.PLAYING);
        targetUHCPlayer.setChangedLevel(0);
        target.sendMessage(ChatUtils.message("&eYou have been respawned by &a" + p.getName()));
        p.sendMessage(ChatUtils.message("&eYou have respawned &a" + target.getName()));
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.TRIAL;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
