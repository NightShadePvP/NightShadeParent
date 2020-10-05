package me.blok601.nightshadeuhc.util;

import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.MConf;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Master on 7/24/2017.
 */
public class PlayerUtils {

    private static ArrayList<UUID> frozen = new ArrayList<>();

    public static HashMap<String /* Country*/, Integer /* People tally*/> locations = new HashMap<>();

    public static void freezePlayer(Player punisher, Player target) {
        frozen.add(target.getUniqueId());
        punisher.sendMessage(ChatUtils.message("&6You have frozen &9" + target.getName() + "!"));
        target.sendMessage(ChatUtils.message("&4&lYou have been frozen. Logging out will result in a ban!"));
    }

    public static void unFreeze(Player target) {
        if (!frozen.contains(target.getUniqueId())) {
            return;
        }

        frozen.remove(target.getUniqueId());

        target.sendMessage(ChatUtils.message("&aYou have been unfrozen!"));
    }

    public static boolean wearingArmor(Player player) {
        for (ItemStack itemStack : player.getInventory().getArmorContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                return true;
            }
        }
        return false;
    }

    public static void giveBulkItems(Player player, Collection<ItemStack> items) {
        for (ItemStack itemStack : items) {
            giveItem(itemStack, player);
        }
    }

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

    public static boolean inGameWorld(Player player) {
        World world = player.getWorld();
        if (world.getName().equalsIgnoreCase(MConf.get().getSpawnLocation().asBukkitLocation().getWorld().getName()) ||
                world.getName().equalsIgnoreCase(MConf.get().getArenaLocation().asBukkitLocation().getWorld().getName())) {
            return false;
        }

        return true;
    }

    public static boolean isPlaying(Player player) {
        return UHCPlayer.get(player).getPlayerStatus() == PlayerStatus.PLAYING && isPlaying(player);
    }

    public static boolean isPlaying(UUID uuid) {
        if (UHC.loggedOutPlayers.contains(uuid)) return true;

        UHCPlayer uhcPlayer = UHCPlayer.get(uuid);
        return uhcPlayer.getPlayerStatus() == PlayerStatus.PLAYING;
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
        player.getActivePotionEffects().clear();
        player.setExp(0F);
        player.setLevel(0);
        if(inv){
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }
    }


}
