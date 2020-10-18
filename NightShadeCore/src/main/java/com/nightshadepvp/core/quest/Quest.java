package com.nightshadepvp.core.quest;

import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Blok on 12/10/2017.
 */
public abstract class Quest implements Listener {

    private String name;
    private Material material;
    private String description;
    private int id;
    private int coinReward;

    public Quest(String name, String description, Material material, int id, int coinReward) {
        this.name = name;
        this.material = material;
        this.description = description;
        this.id = id;
        this.coinReward = coinReward;
    }

    public Quest(String name, String description, Material material, int coinReward) {
        this.name = name;
        this.material = material;
        this.description = description;
        this.coinReward = coinReward;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }


    public void complete(NSPlayer nsPlayer){
        nsPlayer.alterCoins(coinReward);
        if(nsPlayer.getPlayer().isOnline()){
           nsPlayer.msg(ChatUtils.format("&f&m-----------------------------------------------"));
           nsPlayer.msg(ChatUtils.format("&bYou have completed the quest: " + this.name));
           nsPlayer.msg(ChatUtils.format("&a+" + this.coinReward + " coins"));
           nsPlayer.msg(ChatUtils.format("&f&m-----------------------------------------------"));
           nsPlayer.getPlayer().playSound(nsPlayer.getPlayer().getLocation(), Sound.LEVEL_UP, 5, 5);
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCoinReward() {
        return coinReward;
    }
}
