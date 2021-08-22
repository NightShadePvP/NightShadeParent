package me.blok601.nightshadeuhc.command.player.disguise;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Blok on 7/20/2017.
 */
public class RandomDisguiseCommand implements UHCCommand {


    private static HashMap<UUID, DisguiseObject> dis = new HashMap<>();


    Random r1 = new Random();
    Random r2 = new Random();
    Random r3 = new Random();
    List<String> l1 = UHC.get().getConfig().getStringList("l1");
    List<String> l2 = UHC.get().getConfig().getStringList("l2");
    List<String> l3 = UHC.get().getConfig().getStringList("l3");

    @Override
    public String[] getNames() {
        return new String[]{
                "randomd"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        UHCPlayer gamePlayer = UHCPlayer.get(p.getUniqueId());
        NSPlayer u = NSPlayer.get(p.getUniqueId());


        DisguiseObject object = new DisguiseObject();
        object.setPreRank(u.getRank());
        String name = getFullName();


        gamePlayer.setDisguised(true);
        gamePlayer.setDisguisedName(name);

        object.setDisguisedName(gamePlayer.getDisguisedName());

//        UHC.getApi().disguise(p, new PlayerDisguise(name));
        p.sendMessage(ChatUtils.message("&6Disguised as: " + name));
        u.setRank(Rank.PLAYER);
        if (p.isOp()) {
            p.setOp(false);
            p.sendMessage(ChatUtils.message("&bYou have been de-opped because you disguised!"));
            object.setOp(true);
        }

        dis.put(p.getUniqueId(), object);
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.YOUTUBE;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }

    private String getName1() {
        return l1.get(r1.nextInt(l1.size()));
    }

    private String getName2() {
        return l2.get(r2.nextInt(l2.size()));
    }

    private String getName3() {
        return l3.get(r3.nextInt(l3.size()));
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName1());
        sb.append(getName2());
        sb.append(getName3());
        return sb.toString();
    }

    public static HashMap<UUID, DisguiseObject> getDis() {
        return dis;
    }
}
