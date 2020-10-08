package com.nightshadepvp.core.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Blok on 6/20/2019.
 */
public class PlayerUtils {

    @Getter
    private static HashMap<UUID, Runnable> toConfirm = new HashMap<>();

    public static boolean inventoryFull(Player player) {
        return player.getInventory().firstEmpty() == -1;
    }

    public static void giveItem(ItemStack itemStack, Player player) {
        if (inventoryFull(player)) {
            player.getWorld().dropItemNaturally(player.getLocation().add(0.5, 0.5, 0.5), itemStack);
            player.sendMessage(ChatUtils.message("&eYour inventory was full..dropping &b" + itemStack.getAmount() + " " + ChatUtils.materialToString(itemStack.getType())));
            return;
        }

        player.getInventory().addItem(itemStack);
    }

    public static void giveBulkItems(Player player, Collection<ItemStack> items) {
        for (ItemStack itemStack : items) {
            giveItem(itemStack, player);
        }
    }


    public static void playSound(Sound sound, Player player){
        player.playSound(player.getLocation(), sound, 5f, 5f);
    }

    public static void broadcastSound(Sound sound){
        Bukkit.getOnlinePlayers().forEach(o -> playSound(sound, o));
    }

    public static void dropItem(ItemStack itemStack, Location location){
        location.getWorld().dropItemNaturally(location, itemStack);
    }

    public static void givePotionEffect(Player player, PotionEffectType type, int duration, int level){
        player.addPotionEffect(new PotionEffect(type, duration, level, true, true));
    }

    public static void clearPlayer(Player player, boolean inv){
        player.setMaxHealth(20.0);
        player.setHealth(player.getMaxHealth());
        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        player.setExp(0F);
        player.setLevel(0);
        if(inv){
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }
    }

    public static boolean wearingArmor(Player player) {
        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }
}
