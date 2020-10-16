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

    private final SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

    public PunishHistoryGUI(Player player, OfflinePlayer target, Core plugin) {

        GuiBuilder historyGUI = new GuiBuilder();
        historyGUI.name("Punishment History for: " + target.getName()).rows(6);

        ArrayList<ItemStack> banItems = Lists.newArrayList();
        ArrayList<ItemStack> muteItems = Lists.newArrayList();
        ArrayList<ItemStack> warningItems = Lists.newArrayList();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

            String uuid = target.getUniqueId().toString();
            String query = "SELECT * FROM {bans} WHERE uuid=?";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, uuid);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String reason = rs.getString("reason");
                        String bannedByUuid = rs.getString("banned_by_uuid");
                        long time = rs.getLong("time");
                        long until = rs.getLong("until");
                        long id = rs.getLong("id");
                        boolean active = rs.getBoolean("active");
                        boolean perm = until == -1L;

                        ItemBuilder b = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getWoolData()))
                                .name("&bPunishment ID: &f" + id)
                                .lore("&bPunishment Type: &fBan")
                                .lore("&bReason: &f" + reason)
                                .lore("&bStaff Member: &f" + NSPlayer.get(UUID.fromString(bannedByUuid)).getName())
                                .lore("&bIssued: &f" + format.format(new Date(time)));
                        if(active){
                            if(perm){
                                b.lore("&bExpires: &fNever");
                            }else{
                                b.lore("&bExpires: &f" + format.format(new Date(until)));
                            }
                        }else{
                            b.lore("&cExpired: " + format.format(new Date(until)));
                        }

                        banItems.add(b.make());

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            query = "SELECT * FROM {mutes} WHERE uuid=?";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, uuid);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String reason = rs.getString("reason");
                        String bannedByUuid = rs.getString("banned_by_uuid");
                        long time = rs.getLong("time");
                        long until = rs.getLong("until");
                        long id = rs.getLong("id");
                        boolean active = rs.getBoolean("active");
                        boolean perm = until == -1L;

                        ItemBuilder b = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getWoolData()))
                                .name("&bPunishment ID: &f" + id)
                                .lore("&bPunishment Type: &fMute")
                                .lore("&bReason: &f" + reason)
                                .lore("&bStaff Member: &f" + NSPlayer.get(UUID.fromString(bannedByUuid)).getName())
                                .lore("&bIssued: &f" + format.format(new Date(time)));
                        if(active){
                            if(perm){
                                b.lore("&bExpires: &fNever");
                            }else{
                                b.lore("&bExpires: &f" + format.format(new Date(until)));
                            }
                        }else{
                            b.lore("&cExpired: " + format.format(new Date(until)));
                        }

                        muteItems.add(b.make());

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            query = "SELECT * FROM {warnings} WHERE uuid=?";
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                st.setString(1, uuid);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        String reason = rs.getString("reason");
                        String bannedByUuid = rs.getString("banned_by_uuid");
                        long time = rs.getLong("time");
                        long until = rs.getLong("until");
                        long id = rs.getLong("id");
                        boolean active = rs.getBoolean("active");
                        boolean perm = until == -1L;

                        ItemBuilder b = new ItemBuilder(new ItemStack(Material.WOOL, 1, DyeColor.PURPLE.getWoolData()))
                                .name("&bPunishment ID: &f" + id)
                                .lore("&bPunishment Type: &fWarning")
                                .lore("&bReason: &f" + reason)
                                .lore("&bStaff Member: &f" + NSPlayer.get(UUID.fromString(bannedByUuid)).getName())
                                .lore("&bIssued: &f" + format.format(new Date(time)));
                        if(active){
                            if(perm){
                                b.lore("&bExpires: &fNever");
                            }else{
                                b.lore("&bExpires: &f" + format.format(new Date(until)));
                            }
                        }else{
                            b.lore("&cExpired: " + format.format(new Date(until)));
                        }

                        warningItems.add(b.make());

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
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
                });
        });

    }
}
