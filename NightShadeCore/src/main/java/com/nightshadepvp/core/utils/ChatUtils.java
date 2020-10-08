package com.nightshadepvp.core.utils;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayerColl;
import com.nightshadepvp.core.entity.objects.PlayerColor;
import com.nightshadepvp.core.entity.objects.PlayerEffect;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by Master on 8/27/2017.
 */
public class ChatUtils {

    public static final String PREFIX = format("&5NightShade&8 » ");

    public static String message(String msg){
        return ChatColor.translateAlternateColorCodes('&', PREFIX + msg);
    }

    public static String format(String msg){
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/YYYY");


    public static String arrayListSerializer(ArrayList<PlayerColor> list){
        StringBuilder string = new StringBuilder();
        for (PlayerColor color : list){
            string.append(color.toString().toLowerCase()).append(":");
        }

        return string.substring(0, string.length());
    }

    public static ArrayList<PlayerColor> deserializeArrayList(String string){
        if(!string.contains(":")){
            return new ArrayList<PlayerColor>();
        }
        //Empty arraylist
        String[] strings = string.split(":");
        ArrayList<PlayerColor> colors = new ArrayList<>();
        for (String s : strings){
            PlayerColor c = PlayerColor.valueOf(s.toUpperCase());
            if(c == null) continue;

            colors.add(c);
        }

        return colors;
    }


    public static ArrayList<PlayerEffect> deseralizeEffectList(String string){
        if(!string.contains(":")){
            return new ArrayList<PlayerEffect>();
        }
        //Empty arraylist
        String[] strings = string.split(":");
        ArrayList<PlayerEffect> colors = new ArrayList<>();
        for (String s : strings){
            PlayerEffect c = PlayerEffect.valueOf(s.toUpperCase());
            if(c == null) continue;

            colors.add(c);
        }

        return colors;
    }

    public static String arrayListEffectSerializer(ArrayList<PlayerEffect> list){
        StringBuilder string = new StringBuilder();
        for (PlayerEffect color : list){
            string.append(color.toString().toLowerCase()).append(":");
        }

        return string.substring(0, string.length());
    }

    public static void sendAll(String message, Rank allowed){
        NSPlayerColl.get().getAllOnline().stream().filter(nsPlayer -> nsPlayer.hasRank(allowed)).forEach(nsPlayer -> nsPlayer.msg(ChatUtils.message(message)));
    }

    public static void sortCollections(List... collection) {
        for (List list : collection) {
            Collections.sort(list);
        }
    }

    public static void broadcast(String message, Rank requiredRank){
        NSPlayerColl.get().getAllOnline().stream().filter(nsPlayer -> nsPlayer.hasRank(requiredRank)).forEach(nsPlayer -> nsPlayer.msg(ChatUtils.format(message)));
    }

    public static String formatMillisToDate(long time){
        Date date = new Date(time);
        return DATE_FORMAT.format(date);
    }


    public static String uppercaseFirstLetter(String input){
        String firstLetter = input.substring(0, 1).toUpperCase();
        return firstLetter + input.substring(1).toLowerCase();
    }

    public static String oreToString(Material material){
        String name = material.toString();
        if(!name.contains("ORE")){
            return null;
        }

        String[] words = name.split("_");
        String prefix;
        if(words[0].contains("diamond")){
            prefix = "&b";
        }else if(words[0].contains("gold")){
            prefix = "&6";
        }else if(words[0].contains("lapis")){
            prefix = "&9";
        }else if(words[0].contains("redstone")){
            prefix = "&c";
        }else if(words[0].contains("iron")){
            prefix = "&f";
        }else if(words[0].contains("emerald")){
            prefix = "&a";
        }else{
            prefix = "&e"; //highlight
        }
        return prefix + uppercaseFirstLetter(words[0].toLowerCase()) + uppercaseFirstLetter(words[1].toLowerCase());

    }

    public static String materialToString(Material material){
        String name = material.toString();
        if(oreToString(material) != null){
            return oreToString(material);
        }

        if(name.contains("_")){
            String[] words = name.split("_");
            StringBuilder builder = new StringBuilder();
            for (String word : words){
                builder.append(uppercaseFirstLetter(word.toLowerCase())).append(" ");
            }

            return builder.toString().trim();
        }
        return uppercaseFirstLetter(name.toLowerCase());
    }
}
