package com.nightshadepvp.tournament.engine;

import com.massivecraft.massivecore.Engine;
import com.nightshadepvp.core.utils.ItemBuilder;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.enums.MatchState;
import com.nightshadepvp.tournament.entity.handler.MatchHandler;
import com.nightshadepvp.tournament.entity.objects.game.SoloMatch;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.entity.objects.player.PlayerInv;
import com.nightshadepvp.tournament.entity.objects.player.ViewableInventory;
import com.nightshadepvp.tournament.utils.ChatUtils;
import com.nightshadepvp.tournament.utils.NumberUtils;
import com.nightshadepvp.tournament.utils.PagedInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Blok on 7/28/2018.
 */
public class EngineSpectator extends Engine {

    private static EngineSpectator i = new EngineSpectator();
    public static EngineSpectator get() {return i;}

    public HashMap<UUID, PagedInventory> users = new HashMap<>();

    public HashMap<UUID, PagedInventory> getUsers() {
        return users;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        if(p.getItemInHand() == null || p.getItemInHand().getType() == Material.AIR) return;
        TPlayer tPlayer = TPlayer.get(p);

        if(!tPlayer.isSpectator()) return;

        if(p.getItemInHand().getType() == Material.PAPER){

            List<iMatch> matches = MatchHandler.getInstance().getActiveMatches();
            if(matches.size() == 0){
                p.sendMessage(ChatUtils.message("&cThere are no current matches!"));
                return;
            }

            ArrayList<ItemStack> items = new ArrayList<>();

            SoloMatch soloMatch;
            for (iMatch match : matches){
                if(match instanceof SoloMatch){
                    soloMatch = (SoloMatch) match;
                    items.add(new ItemBuilder(Material.PAPER).name("&6#" + soloMatch.getMatchID()).lore("&e" + soloMatch.getPlayer1().getName() + " &5vs. &e" + soloMatch.getPlayer2().getName()).make());
                }

                //TODO: Team Matches
            }

            new PagedInventory(items, ChatUtils.format("&eMatches"), p);


        }else if(p.getItemInHand().getType() == Material.REDSTONE_TORCH_ON){
            tPlayer.unspec();
        }else if(p.getItemInHand().getType() == Material.COMPASS){
            List<iMatch> matches = MatchHandler.getInstance().getActiveMatches();
            if(matches.size() == 0){
                p.sendMessage(ChatUtils.message("&cThere are no current matches!"));
                return;
            }

            Random random = ThreadLocalRandom.current();
            iMatch match = matches.get(random.nextInt(matches.size()));
            if(match == null){
                p.sendMessage(ChatUtils.message("&cThere was a problem finding a random match! Please report this bug to the administrators!"));
                return; // Shoudn't happen at all
            }

            match.addSpectator(tPlayer);

            if(match instanceof SoloMatch){
                SoloMatch mat = (SoloMatch) match;
                tPlayer.msg(ChatUtils.message("&bYou are now spectating &f" + mat.getPlayer1().getName() + " &bvs. &f" + mat.getPlayer2().getName()));
            }


        }
    }


    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player p = (Player) event.getWhoClicked();
        //Get the current scroller inventory the player is looking at, if the player is looking at one.
        if (!users.containsKey(p.getUniqueId())) return;

        if (event.getInventory().getName() == null) return;
        if (event.getInventory().getName().equalsIgnoreCase(ChatColor.stripColor(event.getInventory().getName())))
            return;

        PagedInventory inv = users.get(p.getUniqueId());

        if (!event.getInventory().getName().equalsIgnoreCase(inv.name)) return;

        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (!event.getCurrentItem().hasItemMeta()) return;
        if (!event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        //If the pressed item was a nextpage button
        if (event.getCurrentItem().getItemMeta().getDisplayName().equals(PagedInventory.nextPageName)) {

            //If there is no next page, don't do anything
            if (inv.currpage >= inv.pages.size() - 1) {
                return;
            } else {
                //Next page exists, flip the page
                inv.currpage += 1;
                p.openInventory(inv.pages.get(inv.currpage));
            }
            //if the pressed item was a previous page button
        } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(PagedInventory.previousPageName)) {
            //If the page number is more than 0 (So a previous page exists)
            if (inv.currpage > 0) {
                //Flip to previous page
                inv.currpage -= 1;
                p.openInventory(inv.pages.get(inv.currpage));
            }
        } else {

            if(event.getInventory().getName().contains(ChatColor.stripColor("Hall of Fame"))){
                event.setCancelled(true);
                return;
            }

            ItemStack stack = event.getCurrentItem();
            if(stack.getType() != Material.PAPER) return;
            if(!stack.hasItemMeta()) return;
            String displayName = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            String[] split = displayName.split("#");
            if(NumberUtils.isInt(split[0])){
                int id = Integer.parseInt(split[0]);
                iMatch match = MatchHandler.getInstance().getMatch(id);
                if(match.getMatchState() == MatchState.DONE){
                    p.sendMessage(ChatUtils.message("&cThat match has finished!"));
                    return;
                }

                match.addSpectator(TPlayer.get(p));
                if(match instanceof SoloMatch){
                    SoloMatch soloMatch = (SoloMatch) match;
                    p.sendMessage(ChatUtils.message("&bYou are now spectating &f" + soloMatch.getPlayer1().getName() + " &bvs. &f" + soloMatch.getPlayer2().getName()));
                }

            }

        }

        if (!users.containsKey(p.getUniqueId())) {
            users.put(p.getUniqueId(), inv);
        }

    }

    @EventHandler
    public void pickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        TPlayer gamePlayer = TPlayer.get(p.getUniqueId());
        if (gamePlayer.isSpectator()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void hit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player p = (Player) e.getEntity();

            TPlayer gamePlayer = TPlayer.get(p.getUniqueId());
            if (gamePlayer.isSpectator()) {
                e.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player) {
            TPlayer gamePlayer = TPlayer.get(e.getEntity().getUniqueId());
            if (gamePlayer.isSpectator()) {
                e.setCancelled(true);
            }
        }

        if (e.getDamager() instanceof Player) {
            TPlayer gamePlayer = TPlayer.get(e.getDamager().getUniqueId());
            if (gamePlayer.isSpectator()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!(e.getRightClicked() instanceof Player)) return;
        Player clicker = e.getPlayer();
        Player clicked = (Player) e.getRightClicked();
        TPlayer tClicked = TPlayer.get(clicked);

        if (tClicked.isSpectator() && MatchHandler.getInstance().getActiveMatch(clicked) != null) {
            clicker.openInventory(clicked.getInventory());
            List<String> effects = new ArrayList<>();
            for (PotionEffect ef : clicked.getActivePotionEffects()) {
                effects.add(ef.getType().getName() + " " + ef.getAmplifier() + 1 + " (" + NumberUtils.formatSecs(ef.getDuration() / 20) + ")");
            }
            ViewableInventory inventory = new ViewableInventory(clicked.getName(), clicked.getHealth(), clicked.getFoodLevel(), effects, PlayerInv.fromPlayerInventory(clicked.getInventory()));
            clicker.sendMessage(ChatUtils.message("&bOpening " + clicked.getName() + "'s inventory..."));
            clicker.openInventory(inventory.getInventory());
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        TPlayer gamePlayer = TPlayer.get(e.getPlayer());
        if (gamePlayer.isSpectator()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        TPlayer gamePlayer = TPlayer.get(p.getUniqueId());
        if (gamePlayer.isSpectator()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        TPlayer gamePlayer = TPlayer.get(p.getUniqueId());
        if (gamePlayer.isSpectator()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        TPlayer tPlayer = TPlayer.get(e.getPlayer());
        tPlayer.unspec();
    }
}
