package com.nightshadepvp.core.engine;

import com.massivecraft.massivecore.Engine;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.entity.objects.PlayerColor;
import com.nightshadepvp.core.entity.objects.PlayerEffect;
import com.nightshadepvp.core.entity.objects.PlayerTag;
import com.nightshadepvp.core.gui.guis.ChatColorGUI;
import com.nightshadepvp.core.gui.guis.EffectGUI;
import com.nightshadepvp.core.gui.guis.PlayerTagGUI;
import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.gui.PunishHistoryGUI;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.IOException;

public class EngineInventory extends Engine {

    private static EngineInventory i = new EngineInventory();

    public static EngineInventory get() {
        return i;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            if (e.getInventory() == null) return;

            if (e.getCurrentItem() == null) return;

            if (e.getCurrentItem().getType() == Material.AIR) return;

            if (e.getClickedInventory() == null) return;

            Inventory inv = e.getInventory();
            ItemStack stack = e.getCurrentItem();
            Player p = (Player) e.getWhoClicked();

            if (inv.getName().equalsIgnoreCase("Apply Rank")) {
                NSPlayer u = NSPlayer.get(p.getUniqueId());

                e.setCancelled(true);

                Rank rank = Rank.getRank(stack);
                if (rank == null) {
                    Core.get().getLogManager().log(Logger.LogType.DEBUG, "The rank wasn't found!");
                    return;
                }

                if (rank.getValue() > u.getRank().getValue()) {
                    p.closeInventory();
                    p.sendMessage(ChatUtils.message("&cYou can't set a rank to higher than your own!"));
                    return;
                }
            }

            if (inv.getName().contains("Stats")) {
                e.setCancelled(true);
            }

            if (inv.getName().equalsIgnoreCase(ChatColor.YELLOW + "Preferences")) {
                e.setCancelled(true);
                NSPlayer user = NSPlayer.get(p.getUniqueId());

                if (e.getSlot() == 23) {
                    new ChatColorGUI(p);
                }

                if (e.getSlot() == 7) {
                    new EffectGUI(p);
                }


                if (e.getSlot() == 1) {
                    //Message toggler

                    ItemBuilder builder = new ItemBuilder(stack);
                    if (user.isReceivingPMs()) {
                        //Set to false
                        user.setReceivingPMs(false);
                        builder.name(ChatUtils.format("&cPrivate Messages"));
                        inv.setItem(1, builder.make());
                        p.updateInventory();
                    } else {
                        user.setReceivingPMs(true);
                        builder.name(ChatUtils.format("&aPrivate Messages"));
                        inv.setItem(1, builder.make());
                        p.updateInventory();
                    }

                }

                if (e.getSlot() == 4) {
                    new PlayerTagGUI(p);
                }

            }

            if (inv.getName().equalsIgnoreCase(ChatColor.YELLOW + "Color Selector")) {
                e.setCancelled(true);
                NSPlayer user = NSPlayer.get(p.getUniqueId());

                if (user == null) return;


                PlayerColor color = PlayerColor.getColor(stack);
                if (color == null) {
                    p.sendMessage(ChatUtils.message("&cThere was an error!"));
                    p.closeInventory();
                    return;
                }

                if (!user.getColors().contains(color)) {
                    p.sendMessage(ChatUtils.message("&cYou don't have that color!"));
                    return;
                }

                user.setColor(color);
                p.closeInventory();
                p.sendMessage(ChatUtils.message("&aYou have set your color to " + color.getColor() + color.toString() + "&e!"));
            }

            if (inv.getName().equalsIgnoreCase(ChatColor.YELLOW + "Effect Selector")) {
                e.setCancelled(true);
                NSPlayer user = NSPlayer.get(p.getUniqueId());

                if (user == null) return;

                PlayerEffect effect = PlayerEffect.getEffect(stack);
                if (effect == null) {
                    p.sendMessage(ChatUtils.message("&cThere was an error!"));
                    p.closeInventory();
                    return;
                }

                if (!user.getEffects().contains(effect)) {
                    p.sendMessage(ChatUtils.message("&cYou don't have that effect!"));
                    return;
                }

                user.setEffect(effect);
                p.closeInventory();
                p.sendMessage(ChatUtils.message("&aYou have set your effect to " + effect.getName() + "&a!"));
            }

            if (inv.getName().equalsIgnoreCase(ChatColor.YELLOW + "Tag Selector")) {
                e.setCancelled(true);
                NSPlayer user = NSPlayer.get(p.getUniqueId());

                if (user == null) return;


                PlayerTag tag = PlayerTag.getTag(stack);
                if (tag == null) {
                    p.sendMessage(ChatUtils.message("&cThere was an error!"));
                    p.closeInventory();
                    return;
                }

                if (!user.getUnlockedTags().contains(tag)) {
                    p.sendMessage(ChatUtils.message("&cYou don't have that tag!"));
                    p.sendMessage(ChatUtils.message("&cYou can purchase it in our server store at www.nightshadepvp.com/shop!"));
                    return;
                }

                user.setCurrentTag(tag);
                p.closeInventory();
                p.sendMessage(ChatUtils.message("&aYou have set your tag to &8[" + tag.getTitle() + "&8] &e!"));
            }

            if (inv.getName().equalsIgnoreCase(ChatColor.YELLOW + "Servers")) {
                NSPlayer user = NSPlayer.get(p.getUniqueId());


                if (user == null || !user.hasRank(Rank.ADMIN)) p.closeInventory();

                e.setCancelled(true);

                int slot = e.getSlot();
                if (slot == 21) {
                    try {
                        Runtime.getRuntime().exec("./start.sh", null, new File("/home/dhillon/minecraft/UHC"));
                        p.closeInventory();
                        p.sendMessage(ChatUtils.message("&eStarting the server: UHC1..."));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        p.closeInventory();
                        p.sendMessage(ChatUtils.message("&cThere was an error starting the server. Please report this bug to a develoepr!"));
                    }
                }
            }

            if (inv.getName().contains("Punishment Menu")) {//Will prob change this later on
                String punishing = PunishmentHandler.getInstance().getPunishing().get(p);
                e.setCancelled(true);
                if (stack.getType() == Material.AIR) return;

                if (e.getSlot() == 3) {
                    //History
                   // p.closeInventory();

                    new PunishHistoryGUI(p, Bukkit.getOfflinePlayer(punishing), Core.get());
                    p.sendMessage(ChatUtils.message("&bOpening punishment history for &f" + punishing));
                    return;
                } else if (e.getSlot() == 4) {
                    return;
                } else if (e.getSlot() == 5) {
                    //Freeze
                    p.sendMessage(ChatUtils.message("Froze the player."));
                    Core.get().getServer().dispatchCommand(Bukkit.getConsoleSender(), "freeze " + PunishmentHandler.getInstance().getPunishing().get(p));
                    return;
                }
                p.closeInventory();

                PunishmentHandler.getInstance().handleClick(stack, e);
                return;
            }

            if (PunishmentHandler.getInstance().getPunishing().containsKey(p)) {
                if (p.getOpenInventory() instanceof PlayerInventory) return;
                if (inv.getSize() != 54) return;
                e.setCancelled(true);
                if (stack.getType() == Material.WOOL) {
                    PunishmentHandler.getInstance().createGUI(p);
                    return;
                }

                AbstractPunishment abstractPunishment = PunishmentHandler.getInstance().getAbstractPunishment(e.getInventory().getName());
                if (abstractPunishment == null) {
                    p.closeInventory();
                    p.sendMessage(ChatUtils.message("&cThere was a problem loading that punishment!"));
                    return;
                }

                if (abstractPunishment.getChild(e.getSlot()) == null) {
                    p.closeInventory();
                    p.sendMessage(ChatUtils.message("&cThere was a problem loading that punishment!"));
                    return;
                }

                abstractPunishment.getChild(e.getSlot()).execute(p);
            }
        }
    }

}
