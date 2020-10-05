package me.blok601.nightshadeuhc.entity.object;

import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

/**
 * Created by Blok on 7/10/2018.
 */
public class SpecInfoData {

    public static String translate(Player player, double health, Player damager, String string){
        if(damager!= null){
            return ChatUtils.format(string.replaceAll("%player%", player.getName()).replaceAll("%health%", format.format(health/2).replaceAll("%d%", damager.getName())));
        }else{
            return ChatUtils.format(string.replaceAll("%player%", player.getName()).replaceAll("%health%", format.format(health/2)).replaceAll("%d%", ""));
        }

    }

    public static final String DAMAGE_FALL = "&5PvE&8»&c%player%&8<-&bFall &8[&a%health% &c❤&8]";
    public static final String DAMAGE_BURN = "&5PvE&8»&c%player%&8<-&bBurn &8[&a%health%&c❤&8]";
    public static final String DAMAGE_OTHER = "&5PvE&8»&c%player%&8<-&bPvE &8[&a%health% &c❤&8]";
    public static final String DAMAGE_MOB = "&5PvE&8»&c%player%&8<-&bMob &8[&a%health% &c❤&8]";
    public static final String DAMAGE_PLAYER = "&5PvP&8»&c%player%&8<-&b%d% &8[&a%health% &c❤&8]";
    public static final String DAMAGE_PLAYER_BOW = "&5PvP&8»&c%player%&8<-&b%d% &8[&a%health% &c❤&8] &8[&7Bow&7]";
    public static final DecimalFormat format = new DecimalFormat("##.##");

}
