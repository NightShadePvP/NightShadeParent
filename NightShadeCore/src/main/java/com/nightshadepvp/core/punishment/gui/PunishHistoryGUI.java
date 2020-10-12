package com.nightshadepvp.core.punishment.gui;

import com.google.common.collect.Lists;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.gui.GuiBuilder;
import com.nightshadepvp.core.utils.ItemBuilder;
import litebans.api.Database;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class PunishHistoryGUI {

    private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public PunishHistoryGUI(Player player, OfflinePlayer target, Core plugin) {

        GuiBuilder historyGUI = new GuiBuilder();
        historyGUI.name("Punishment History for: " + target.getName()).rows(6);

        ItemStack banBase = new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getWoolData());
        String banStatement = "SELECT * FROM litebans_bans WHERE uuid=\"" + target.getUniqueId().toString() + "\"";
        String muteStatment = "SELECT * FROM litebans_mutes WHERE uuid=\"" + target.getUniqueId().toString() + "\"";
        String warningsStatement = "SELECT * FROM litebans_warnings WHERE uuid=\"" + target.getUniqueId().toString() + "\"";
        ArrayList<ItemStack> banItems = Lists.newArrayList();
        ArrayList<ItemStack> muteItems = Lists.newArrayList();
        ArrayList<ItemStack> warningItems = Lists.newArrayList();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                ResultSet bans = Database.get().prepareStatement(banStatement).executeQuery();
                ResultSet mutes = Database.get().prepareStatement(muteStatment).executeQuery();
                ResultSet warnings = Database.get().prepareStatement(warningsStatement).executeQuery();
                while (bans.next()) {
                    banItems.add(new ItemBuilder(banBase)
                            .name("&ePunishment ID: " + bans.getInt("id"))
                            .lore("&bType: &fBan")
                            .lore("&bReason: &f" + bans.getString("reason"))
                            .lore("&bBanned On: &f" + format.format(new Date(bans.getLong("time"))))
                            .lore("&bBanned By: &f" + NSPlayer.get(UUID.fromString(bans.getString("banned_by_uuid"))).getName())
                            .lore(bans.getString("removed_by_name").equalsIgnoreCase("#expired") ? "&cExpired" : "&bExpires: &f" + (bans.getLong("until") == -1 ? "Never" : format.format(new Date(bans.getLong("until")))))
                            .make()
                    );
                }

                while (mutes.next()) {
                    muteItems.add(new ItemBuilder(banBase)
                            .name("&ePunishment ID: " + mutes.getInt("id"))
                            .lore("&bType: &fMute")
                            .lore("&bReason: &f" + mutes.getString("reason"))
                            .lore("&bBanned On: &f" + format.format(new Date(mutes.getLong("time"))))
                            .lore("&bBanned By: &f" + NSPlayer.get(UUID.fromString(mutes.getString("banned_by_uuid"))).getName())
                            .lore(mutes.getString("removed_by_name").equalsIgnoreCase("#expired") ? "&cExpired" : "&bExpires: &f" + (mutes.getLong("until") == -1 ? "Never" : format.format(new Date(mutes.getLong("until")))))
                            .make()
                    );
                }

                while (warnings.next()) {
                    warningItems.add(new ItemBuilder(banBase)
                            .name("&ePunishment ID: " + warnings.getInt("id"))
                            .lore("&bType: &fWarning")
                            .lore("&bReason: &f" + warnings.getString("reason"))
                            .lore("&bBanned On: &f" + format.format(new Date(warnings.getLong("time"))))
                            .lore("&bBanned By: &f" + NSPlayer.get(UUID.fromString(warnings.getString("banned_by_uuid"))).getName())
                            .lore(warnings.getString("removed_by_name").equalsIgnoreCase("#expired") ? "&cExpired" : "&bExpires: &f" + (warnings.getLong("until") == -1 ? "Never" : format.format(new Date(warnings.getLong("until")))))
                            .make()
                    );
                }

                //Create the gui
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    int banIndex = 0;
                    int muteIndex = 18;
                    int warningIndex = 36;

                    for (ItemStack stack : banItems) {
                        historyGUI.item(banIndex, stack);
                        banIndex++;
                    }

                    for (ItemStack stack : muteItems) {
                        historyGUI.item(muteIndex, stack);
                        muteIndex++;
                    }

                    for (ItemStack stack : warningItems) {
                        historyGUI.item(warningIndex, stack);
                        warningIndex++;
                    }

                    player.openInventory(historyGUI.make());

                    try {
                        bans.close();
                        mutes.close();
                        warnings.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }

                });


            } catch (SQLException throwables) {
                throwables.printStackTrace();
                plugin.getLogManager().log(Logger.LogType.SEVERE, "There was an error accessing ban database!");
            }

        });

    }
}
