package me.blok601.nightshadeuhc.command.staff;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.event.PlayerJoinGameLateEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ScatterUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Blok on 12/9/2017.
 */
public class LateStartCommand implements UHCCommand{
    @Override
    public String[] getNames() {
        return new String[]{
                "latestart"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(args.length != 1){
            p.sendMessage(ChatUtils.message("&cUsage: /latestart <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null){
            if (GameManager.get().getLateScatter().contains(args[0].toLowerCase())) {
                p.sendMessage(ChatUtils.message("&cThat player is already in the late scatter queue!"));
                return;
            }

            String name = args[0];
            GameManager.get().getLateScatter().add(name.toLowerCase());
            GameManager.get().getWhitelist().add(name.toLowerCase());
            p.sendMessage(ChatUtils.message("&a" + name + " &ehas been whitelisted and added to the late scatter queue!"));
            return;
        }

        UHCPlayer targetUHCPlayer = UHCPlayer.get(target);

//        if(UHC.players.contains(target.getUniqueId())){
//            p.sendMessage(ChatUtils.message("&cThat player is already in the game!"));
//            return;
//        }

        if(targetUHCPlayer.getPlayerStatus() != PlayerStatus.LOBBY){
            p.sendMessage(ChatUtils.message("&cThe player must be dead and in the lobby to be respawned!"));
            return;
        }

        //UHC.players.add(target.getUniqueId());
        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
        target.setLevel(0);
        target.setExp(0F);
        target.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
        ScatterUtil.scatterPlayer(GameManager.get().getWorld(), (int) GameManager.get().getBorderSize(), target);
        targetUHCPlayer.setPlayerStatus(PlayerStatus.PLAYING);
        target.playSound(target.getLocation(), Sound.CHICKEN_EGG_POP, 5, 5);
        p.sendMessage(ChatUtils.message("&aYou have scattered &e" + target.getName()));
        target.sendMessage(ChatUtils.message("&eYou were scattered!"));
        ChatUtils.sendAll("&f" + target.getName() + " &bhas been late scattered!");
        Bukkit.getPluginManager().callEvent(new PlayerJoinGameLateEvent(target));

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
