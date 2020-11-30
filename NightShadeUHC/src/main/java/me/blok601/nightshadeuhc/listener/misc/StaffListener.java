package me.blok601.nightshadeuhc.listener.misc;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Blok on 6/19/2017.
 */
public class StaffListener implements Listener {

    ArrayList<UUID> interactCooldown = new ArrayList<>();

    @EventHandler
    public void onClick(PlayerInteractEvent e){
        Player p = e.getPlayer();
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        if(uhcPlayer.isStaffMode()){
            if (p.getItemInHand() == null || e.getItem() == null) {
                e.setCancelled(true);
                return;
            }

            Block clickedBlock = e.getClickedBlock();
            if (clickedBlock != null && clickedBlock.getType() == Material.CHEST) {
                Chest chest = (Chest) clickedBlock.getState();
                p.openInventory(chest.getBlockInventory());
                return;
            }

            ItemStack item  = e.getItem();
            if(item.getType() == null){
                return;
            }


            if(item.getType() == Material.AIR){
                return;
            }


            if(item.getType() == Material.TORCH){
                //Toggle Vanish
                if(uhcPlayer.isVanished()){
                    uhcPlayer.unVanish();
                    p.sendMessage(ChatUtils.message("&eYou are now &avisible!"));
                }else{
                    uhcPlayer.vanish(false);
                    p.sendMessage(ChatUtils.message("&eYou are now &avanished!"));
                }

                return;

            }

            if(item.getType() == Material.BOOK){
                if(e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR){
                    //Swap to diamond chestplate
                    p.setItemInHand(new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("&cPlayer Armor").make());
                    return;
                }
            }

            if(item.getType() == Material.DIAMOND_CHESTPLATE){
                if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR){
                    e.setCancelled(true);
                    p.updateInventory();
                }
                if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
                    p.setItemInHand(new ItemBuilder(Material.BOOK).name("&cPlayer Inventory").make());
                    return;
                }
            }

            if (item.getType() == Material.SKULL_ITEM) { // Random TP
                if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) { //Cycle
                    if (e.getItem().getDurability() == 3) { // All -> Miners
                        ItemStack miners = new ItemStack(Material.SKULL_ITEM, 1, (short) 2);
                        e.getPlayer().setItemInHand(new ItemBuilder(miners).name("&cMining Teleport").make());
                        return;
                    } else if (e.getItem().getDurability() == 2) { // Miners -> Nether
                        ItemStack nether = new ItemStack(Material.SKULL_ITEM, 1, (short) 1);
                        e.getPlayer().setItemInHand(new ItemBuilder(nether).name("&cNether Teleport").make());
                        return;
                    } else { // Nether -> All
                        ItemStack all = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                        e.getPlayer().setItemInHand(new ItemBuilder(all).name("&cRandom Teleport").make());
                        return;
                    }
                }

                if(interactCooldown.contains(p.getUniqueId())){
                    p.sendMessage(ChatUtils.message("&cStop spamming me! You can only do this every 1 second!"));
                    return;
                }

                if (e.getItem().getDurability() == 3) { //All players
                    if (UHCPlayerColl.get().getAllPlaying().size() == 0) {
                        p.sendMessage(ChatUtils.message("&cThere are not enough players online to do this!"));
                        return;
                    }
                    Random random = ThreadLocalRandom.current();
                    //Player rand = Bukkit.getPlayer(UHC.players.get(random.nextInt(UHC.players.size())));

                    int element = random.nextInt(UHCPlayerColl.get().getAllPlaying().size());
                    Player target = UHCPlayerColl.get().getAllPlaying().get(element).getPlayer();

                    if (target == null) {
                        p.sendMessage(ChatUtils.message("&cThere was a problem trying to find a random player!"));
                        return;
                    }

                    p.teleport(target.getLocation());
                    p.sendMessage(ChatUtils.message("&aTeleported to " + target.getName()));
                    this.interactCooldown.add(p.getUniqueId());
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            interactCooldown.remove(p.getUniqueId());
                        }
                    }.runTaskLater(UHC.get(), 20);
                }else if(e.getItem().getDurability() == 2){
                    //Miners
                    Random random = ThreadLocalRandom.current();
                    ArrayList<Player> players = new ArrayList<>();
                    UHCPlayerColl.get().getAllOnline().stream().filter(up -> !up.isSpectator()).filter(up -> up.getPlayer().getWorld().getName().equalsIgnoreCase(GameManager.get().getWorld().getName())).filter(up -> up.getPlayer().getLocation().getY() <= 40).forEach(up -> players.add(up.getPlayer()));
                    if(players.size() == 0){
                        p.sendMessage(ChatUtils.message("&cThere are no players mining right now!"));
                        return;
                    }
                    Player target = players.get(random.nextInt(players.size()));
                    if(target == null){
                        p.sendMessage(ChatUtils.message("&cThere was a problem locating a player mining. Please report this to Blok :("));
                        return;
                    }

                    p.teleport(target);
                    p.sendMessage(ChatUtils.message("&aTeleported to " + target.getName()));
                    this.interactCooldown.add(p.getUniqueId());
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            interactCooldown.remove(p.getUniqueId());
                        }
                    }.runTaskLater(UHC.get(), 20);
                }else if(e.getItem().getDurability() == 1){
                    //Nether
                    Random random = ThreadLocalRandom.current();
                    ArrayList<Player> players = new ArrayList<>();
                    UHCPlayerColl.get().getAllOnline().stream().filter(up -> !up.isSpectator()).filter(up -> up.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER).forEach(up -> players.add(up.getPlayer()));
                    if(players.size() == 0){
                        p.sendMessage(ChatUtils.message("&cThere are no players in the nether right now!"));
                        return;
                    }
                    Player target = players.get(random.nextInt(players.size()));
                    if(target == null){
                        p.sendMessage(ChatUtils.message("&cThere was a problem locating a player in the nether. Please report this to Blok :("));
                        return;
                    }

                    p.teleport(target);
                    p.sendMessage(ChatUtils.message("&aTeleported to " + target.getName()));
                    this.interactCooldown.add(p.getUniqueId());
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            interactCooldown.remove(p.getUniqueId());
                        }
                    }.runTaskLater(UHC.get(), 20);
                }
            }
        }
    }

    @EventHandler
    public void onEClick(PlayerInteractAtEntityEvent e){
        Player p = e.getPlayer();
        Entity entity = e.getRightClicked();
        if(!(entity instanceof  Player)){
            return;
        }

        UHCPlayer uhcPlayer = UHCPlayer.get(p);

        Player target = (Player) entity;
        if(uhcPlayer.isStaffMode()) {
            ItemStack item = p.getItemInHand();
            if (item.getType() == Material.BOOK) {
                System.out.println("[INVENTORY SEE] " + uhcPlayer.getPlayer().getName() + " Has Opened the inventory of " + e.getRightClicked().getName());
                Inventory inv = Bukkit.createInventory(null, 54, ChatUtils.format("&6" + target.getName() + "'s Inventory"));

                for (ItemStack stack : target.getInventory()) {
                    if (stack == null) {
                        continue;
                    }

                    inv.addItem(stack);
                }


                p.sendMessage(ChatUtils.message("&6Opening " + target.getName() + "'s Inventory..."));
                p.openInventory(inv);

            }else if(item.getType() == Material.DIAMOND_CHESTPLATE){
                e.setCancelled(true);
                p.updateInventory();
                Inventory playerArmor = Bukkit.createInventory(null, 9, target.getName() + "'s Armor");
                int i = 0;
                for (ItemStack stack : target.getInventory().getArmorContents()){
                    if(stack == null || stack.getType() == Material.AIR){
                        i++;
                        continue;
                    }
                    playerArmor.setItem(i, stack);
                    i++;
                }

                p.sendMessage(ChatUtils.message("&6Viewing " + target.getName() + "'s armor..."));
                p.openInventory(playerArmor);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getInventory() == null){
            return;
        }

        if(e.getClickedInventory() == null){
            return;
        }

        if(e.getWhoClicked() == null){
            return;
        }

        if(e.getCurrentItem() == null){
            return;
        }

        if(e.getClickedInventory().getName().equalsIgnoreCase("Alert Toggles")){
            e.setCancelled(true);
            Player player = (Player) e.getWhoClicked();
            NSPlayer user= NSPlayer.get(player.getUniqueId());
            UHCPlayer uhcPlayer = UHCPlayer.get(player);
            if(user.getRank().getValue() >= Rank.TRIAL.getValue()){
                if (e.getCurrentItem().getType() == Material.TORCH) { //Spec info
                    if(uhcPlayer.isReceivingSpectatorInfo()){
                        uhcPlayer.setReceivingSpectatorInfo(false);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are no longer receiving spectator info alerts."));
                    }else{
                        uhcPlayer.setReceivingSpectatorInfo(true);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are now receiving spectator info alerts."));
                    }
                } else if (e.getCurrentItem().getType() == Material.DIAMOND_ORE) { //Mining Alerts
                    if(uhcPlayer.isReceivingMiningAlerts()){
                        uhcPlayer.setReceivingMiningAlerts(false);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are no longer receiving mining alerts."));
                    }else{
                        uhcPlayer.setReceivingMiningAlerts(true);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are now receiving mining alerts."));
                    }
                } else if (e.getCurrentItem().getType() == Material.SIGN) { //ToggleSneak alerts
                    if (user.isReceivingToggleSneak()) {
                        user.setReceivingToggleSneak(false);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are no longer receiving sneak alerts"));
                        return;
                    }

                    user.setReceivingToggleSneak(false);
                    player.sendMessage(ChatUtils.message("&eYou are now receiving toggle sneak alerts."));
                } else if (e.getCurrentItem().getType() == Material.PAPER) { //Staff chat
                    if (user.isReceivingStaffChat()) {
                        user.setReceivingStaffChat(false);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are no longer receiving staff chat."));
                        return;
                    }

                    user.setReceivingStaffChat(true);
                    player.sendMessage(ChatUtils.message("&eYou are now receiving staff chat."));
                } else if (e.getCurrentItem().getType() == Material.COMPASS) {
                    if (uhcPlayer.isReceivingCommandSpy()) {
                        uhcPlayer.setReceivingCommandSpy(false);
                        player.closeInventory();
                        player.sendMessage(ChatUtils.message("&eYou are no longer receiving command spy."));
                        return;
                    }
                    uhcPlayer.setReceivingCommandSpy(true);
                    player.sendMessage(ChatUtils.message("&eYou are now receiving command spy."));
                }else if(e.getCurrentItem().getType() == Material.DIAMOND_SWORD){
                    player.closeInventory();
                    player.sendMessage(ChatUtils.message("&cThis feature has not been enabled. Tell Blok to get to work!"));
                }
            }else{
                player.closeInventory();
            }
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        if (uhcPlayer.isStaffMode()) {
            uhcPlayer.unspec();
            Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(p));
            p.performCommand("rea");
            p.getActivePotionEffects().forEach(potionEffect -> p.removePotionEffect(potionEffect.getType()));
            uhcPlayer.setStaffMode(false);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
        }
    }

    private ArrayList<String> commands = new ArrayList<String>(Arrays.asList("/bukkit:me", "/rl", "/reload"));

    @EventHandler(ignoreCancelled = true)
    public void cmd(PlayerCommandPreprocessEvent event){
        Player p = event.getPlayer();

        if(event.getMessage().startsWith("/cmdspy")) return;

        if(commands.contains(event.getMessage())){
            p.sendMessage(ChatUtils.message("&cYou can't use that command!"));
            return;
        }

        ChatUtils.sendCommandSpyMessage(event.getMessage(), p);
    }
}
