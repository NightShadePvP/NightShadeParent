package com.nightshadepvp.tournament.engine;

import com.massivecraft.massivecore.Engine;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.entity.objects.PlayerTag;
import com.nightshadepvp.core.utils.ItemBuilder;
import com.nightshadepvp.tournament.Settings;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.TPlayerColl;
import com.nightshadepvp.tournament.entity.enums.PlayerStatus;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.entity.handler.KitHandler;
import com.nightshadepvp.tournament.entity.handler.MatchHandler;
import com.nightshadepvp.tournament.entity.objects.data.Kit;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.entity.objects.player.PlayerInv;
import com.nightshadepvp.tournament.utils.ChatUtils;
import com.nightshadepvp.tournament.utils.InventoryUtils;
import com.nightshadepvp.tournament.utils.PlayerUtils;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Blok on 7/21/2018.
 */
public class EnginePlayer extends Engine {

    private static EnginePlayer i = new EnginePlayer();

    public static EnginePlayer get() {
        return i;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(player);
        tPlayer.setUsingOldVersion(false);
        FileConfiguration config;
        File file;
        if (Settings.getSettings().playerFileExists(player, Tournament.get())) {
            file = new File(Tournament.get().getDataFolder() + File.separator + "player", player.getUniqueId().toString() + ".yml");
            config = YamlConfiguration.loadConfiguration(file);

            PlayerInv inv;
            for (Kit kit : KitHandler.getInstance().getKits()) {
                if (config.contains(kit.getName())) {
                    inv = InventoryUtils.playerInventoryFromString(config.getString(kit.getName() + ".inventory"));
                    tPlayer.setKit(kit, inv); //Load their kit
                    continue;
                }

                inv = new PlayerInv();
                inv.setArmorContents(kit.getArmor());
                inv.setContents(kit.getItems());
                config.set(kit.getName() + ".inventory", InventoryUtils.playerInvToString(inv));
                tPlayer.setKit(kit, inv);
            }

            //Made sure they are updated with all kits
        } else {
            try {
                Settings.getSettings().createPlayerFile(player, Tournament.get());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            file = new File(Tournament.get().getDataFolder() + File.separator + "player", player.getUniqueId().toString() + ".yml");
            config = YamlConfiguration.loadConfiguration(file);
            PlayerInv inv;
            for (Kit kit : KitHandler.getInstance().getKits()) {
                inv = new PlayerInv();
                inv.setArmorContents(kit.getArmor());
                inv.setContents(kit.getItems());
                config.set(kit.getName() + ".inventory", InventoryUtils.playerInvToString(inv));
                tPlayer.setKit(kit, inv);
            }
            //Created their kits and everything
        }

        Settings.getSettings().savePlayerFile(player, Tournament.get());
        try {
            config.save(file);
        } catch (IOException e1) {
            Core.get().getLogManager().log(Logger.LogType.SEVERE, "Couldn't save " + player.getName() + "'s config file!");
        }

        iMatch match = MatchHandler.getInstance().getActiveMatch(player);
        if (match == null) {
            tPlayer.sendSpawn();
        } else {
            //TODO: Logger check
        }

        player.sendMessage(ChatUtils.message("&bWelcome to &5NightShade Tourney's &fv" + Tournament.get().getDescription().getVersion()));
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if ((e.getEntity() instanceof Player)) {
            Player p = (Player) e.getEntity();
            TPlayer tPlayer = TPlayer.get(p);
            if (tPlayer.getStatus() == PlayerStatus.LOBBY) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHealth(EntityDamageEvent e) {
        if ((e.getEntity() instanceof Player)) {
            Player p = (Player) e.getEntity();
            TPlayer tPlayer = TPlayer.get(p);
            if (tPlayer.getStatus() == PlayerStatus.LOBBY) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        if ((e.getEntity() instanceof Player)) {
            Player p = (Player) e.getEntity();
            TPlayer tPlayer = TPlayer.get(p);
            if (tPlayer.isInSpawn()) e.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(player);

        File file = new File(Tournament.get().getDataFolder() + File.separator + "player", player.getUniqueId().toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Map.Entry<Kit, PlayerInv> invEntry : tPlayer.getPlayerKits().entrySet()) {
            config.set(invEntry.getKey().getName() + ".inventory", InventoryUtils.playerInvToString(invEntry.getValue()));
        }

        Settings.getSettings().savePlayerFile(player, Tournament.get());
        try {
            config.save(file);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(player);

        File file = new File(Tournament.get().getDataFolder() + File.separator + "player", player.getUniqueId().toString() + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (Map.Entry<Kit, PlayerInv> invEntry : tPlayer.getPlayerKits().entrySet()) {
            config.set(invEntry.getKey().getName() + ".inventory", InventoryUtils.playerInvToString(invEntry.getValue()));
        }

        Settings.getSettings().savePlayerFile(player, Tournament.get());
        try {
            config.save(file);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.isCancelled()) {
            return;
        }

        Player p = e.getPlayer();
        NSPlayer nsPlayer = NSPlayer.get(p);
        TPlayer tPlayer = TPlayer.get(p);

        if (e.getMessage().contains("&k") || e.getMessage().contains("&m")) {
            if (nsPlayer.hasRank(Rank.TRIAL)) {
                e.setCancelled(true);
                p.sendMessage(ChatUtils.message("&cNormal players can't use those characters!"));
                return;
            }
        }

        if (e.getMessage().startsWith("@")) {
            iMatch match = MatchHandler.getInstance().getActiveMatch(tPlayer);
            if (match != null) {
                e.setCancelled(true);
                match.broadcastAllFormat("&8[&5Match&8] &7" + p.getName() + "&8: " + nsPlayer.getColor().getColor() + e.getMessage().replaceAll("@", ""));
                return;
            }
        }

        if (GameHandler.getInstance().isChatFrozen()) {
            e.setCancelled(true);
            if (!nsPlayer.hasRank(Rank.TRIAL)) {
                e.setCancelled(true);
                p.sendMessage(ChatUtils.message("&cThe chat is currently frozen!"));
                return;
            }
        }


        if (nsPlayer.hasRank(Rank.FRIEND)) { //Rank
            e.setCancelled(true);
            if (nsPlayer.getPrefix() != null && !nsPlayer.getPrefix().equalsIgnoreCase("")) {//Prefix
                if (nsPlayer.getCurrentTag() != PlayerTag.DEFAULT) { //Tag
                    //They have all 3 here
                    for (Player rec : e.getRecipients()) {
                        rec.sendMessage(ChatUtils.format(nsPlayer.getPrefix() + nsPlayer.getRank().getNameColor() + " " + p.getName() + " " + nsPlayer.getCurrentTag().getFormatted() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
                    }
                    return;
                }


                //Only a prefix here
                for (Player rec : e.getRecipients()) {
                    rec.sendMessage(ChatUtils.format(nsPlayer.getPrefix() + nsPlayer.getRank().getNameColor() + " " + p.getName() + " " + nsPlayer.getCurrentTag().getFormatted() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
                }
                return;

            }

            //Just a rank here
            if (nsPlayer.getCurrentTag() != PlayerTag.DEFAULT) {
                for (Player rec : e.getRecipients()) {
                    rec.sendMessage(ChatUtils.format(nsPlayer.getRank().getPrefix() + nsPlayer.getRank().getNameColor() + p.getName() + " " + nsPlayer.getCurrentTag().getFormatted() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
                }
            } else {
                for (Player rec : e.getRecipients()) {
                    rec.sendMessage(ChatUtils.format(nsPlayer.getRank().getPrefix() + nsPlayer.getRank().getNameColor() + p.getName() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
                }
            }

            return;
        }

        if (nsPlayer.getPrefix() != null && !nsPlayer.getPrefix().equalsIgnoreCase("")) { //No rank but a prefix
            e.setCancelled(true);
            for (Player rec : e.getRecipients()) {
                if (nsPlayer.getCurrentTag() != PlayerTag.DEFAULT) {//Prefix and tag
                    rec.sendMessage(ChatUtils.format(nsPlayer.getPrefix() + nsPlayer.getRank().getNameColor() + " " + p.getName() + " " + nsPlayer.getCurrentTag().getFormatted() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
                    continue;
                }
                //Just prefix
                rec.sendMessage(ChatUtils.format(nsPlayer.getPrefix() + nsPlayer.getRank().getNameColor() + " " + p.getName() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
            }
            return; //Cool
        }

        e.setCancelled(true);
        for (Player rec : e.getRecipients()) { //Possibly just tag
            if (nsPlayer.getCurrentTag() != PlayerTag.DEFAULT) {
                rec.sendMessage(ChatUtils.format(nsPlayer.getCurrentTag().getFormatted() + nsPlayer.getRank().getNameColor() + " " + p.getName() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
                continue;
            }

            rec.sendMessage(ChatUtils.format(p.getName() + ": " + nsPlayer.getColor().getColor() + e.getMessage()));
        }
        return;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        NSPlayer user = NSPlayer.get(p);
        int amount = Bukkit.getOnlinePlayers().size();
        for (TPlayer t : TPlayerColl.get().getAllOnline()) {
            if (t.isSpectator()) amount--;
        }
        //Got the real number of players;
        if (GameHandler.getInstance().isWhitelistOn()) {
            if (user.hasRank(Rank.YOUTUBE)) {
                e.allow();
                return;
            }

            //Let in if they are staff

            if (!GameHandler.getInstance().getWhitelist().contains(p.getName().toLowerCase())) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You are not on the whitelist!");
            } else {
                e.allow();
            }

            return;
        }

        if (amount >= GameHandler.getInstance().getSlots()) {
            if (!user.hasRank(Rank.YOUTUBE)) {
                e.disallow(PlayerLoginEvent.Result.KICK_FULL, "The server is full!");
            } else {
                e.allow();
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == null) return;
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if (e.getItem() == null || e.getItem().getType() == Material.AIR) return;
            if (e.getItem().getType() == Material.BOOK) {
                TPlayer tPlayer = TPlayer.get(p);
                if (tPlayer.getStatus() == PlayerStatus.LOBBY) {
                    p.chat("/editkit");
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (MatchHandler.getInstance().getActiveMatch(p) != null) return; //in game
        TPlayer tPlayer = TPlayer.get(p);
        if (tPlayer.getStatus() == PlayerStatus.LOBBY) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (MatchHandler.getInstance().getActiveMatch(p) != null) return; //in game
        TPlayer tPlayer = TPlayer.get(p);
        if (tPlayer.getStatus() == PlayerStatus.LOBBY) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(p);
        if (tPlayer.getStatus() != PlayerStatus.PLAYING) {
            if (NSPlayer.get(p).hasRank(Rank.ADMIN)) {
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(p);
        NSPlayer nsPlayer = NSPlayer.get(p);
        if (MatchHandler.getInstance().getActiveMatch(p) == null) {
            //They are not in the match
            if (!nsPlayer.hasRank(Rank.ADMIN)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
            return;
        }

        Player player = e.getPlayer();
        TPlayer tPlayer = TPlayer.get(player);
        if(tPlayer.getStatus() != PlayerStatus.EDITING) return;

        Location from = e.getFrom();
        Location to = e.getTo();
        Selection selection = Tournament.get().getEditLocationSelection();
        if (selection.contains(from) && !selection.contains(to)) {
            EngineInventory.edtingMap.remove(player.getUniqueId());
            tPlayer.setStatus(PlayerStatus.LOBBY);
            PlayerUtils.clearPlayer(player, true);
            ItemBuilder item = new ItemBuilder(Material.BOOK).name("&5Kit Editor");
            player.getInventory().setItem(4, item.make());
        }
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event){
        Entity entity = event.getEntity();
        if(!(entity instanceof Player)){
            return;
        }

        Player player = (Player) entity;
        TPlayer tPlayer = TPlayer.get(player);
        if(tPlayer.getStatus() == PlayerStatus.EDITING){
            event.setCancelled(true);
        }
    }

}
