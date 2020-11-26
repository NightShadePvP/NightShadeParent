package me.blok601.nightshadeuhc.listener.game;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.MConf;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.PlayerRespawn;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.event.CustomDeathEvent;
import me.blok601.nightshadeuhc.event.UHCStatUpdateEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Blok on 7/15/2018.
 */
public class GameDeathListener implements Listener {
    
    private GameManager gameManager;
    public GameDeathListener(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @EventHandler
    public void onPvPDeath(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        if (!GameState.gameHasStarted()) return;

        Player p = (Player) e.getEntity();
        UHCPlayer uhcPlayer = UHCPlayer.get(p); //player who died
        if (uhcPlayer.isInArena()) return;
        NSPlayer user = NSPlayer.get(p);
        Player damager = null;
        boolean shot = false;
        Projectile projectile = null;

        UHCPlayer gamePlayer1 = null;

        if (e.getDamager() instanceof Player) { //Sort out if player was shot or not
            damager = (Player) e.getDamager();
            gamePlayer1 = UHCPlayer.get(damager);
        } else if (e.getDamager() instanceof Arrow) {
            Arrow a = (Arrow) e.getDamager();
            if (a.getShooter() instanceof Player) {
                damager = (Player) a.getShooter();
                gamePlayer1 = UHCPlayer.get(damager);
                shot = true;
                projectile = a;
            } else {
                damager = null;
                shot = false;
            }
        } else {
            damager = null;
        }

        if (e.getFinalDamage() >= p.getHealth()) { //Check if player would have died on the hit
            ArrayList<ItemStack> items = new ArrayList<>(); //Get their loot
            for (ItemStack i : p.getInventory().getContents()) {
                if (i == null || i.getType() == Material.AIR) continue;
                items.add(i);
            }

            for (ItemStack i : p.getInventory().getArmorContents()) {
                if (i == null || i.getType() == Material.AIR) continue;
                items.add(i);
            }

            e.setDamage(0);
            e.setCancelled(true);
            //p.getWorld().strikeLightningEffect(p.getLocation());
            CustomDeathEvent customDeathEvent = new CustomDeathEvent(p, damager, items, p.getLocation(), true);
            Bukkit.getPluginManager().callEvent(customDeathEvent);

            PlayerRespawn inv = new PlayerRespawn();
            inv.setArmor(p.getInventory().getArmorContents());
            inv.setItems(p.getInventory().getContents());
            inv.setLocation(p.getLocation());
            gameManager.getInvs().put(p.getUniqueId(), inv);

            if (shot) {
                if (projectile != null) {
                    customDeathEvent.setUsedProjectile(true);
                    customDeathEvent.setProjectile(projectile);
                }
            }

            if (!customDeathEvent.isCancelled()) {
                String deathMessage = ""; //Figure out death message based on how they died
                if (damager != null) {
                    if (shot) {
                        deathMessage = "&b" + (uhcPlayer.isDisguised() ? uhcPlayer.getDisguisedName() : uhcPlayer.getName()) + " &fwas shot by &b" + (gamePlayer1.isDisguised() ? gamePlayer1.getDisguisedName() : gamePlayer1.getName()) + "&7(&b" + Math.floor(p.getLocation().distance(damager.getLocation())) + "m&7)";
                    } else {
                        if (gamePlayer1.isDisguised() && gamePlayer1.getDisguisedName() != null) {
                            deathMessage = "&b" + (uhcPlayer.isDisguised() ? uhcPlayer.getDisguisedName() : uhcPlayer.getName()) + " &fwas killed by &b" + (gamePlayer1.isDisguised() ? gamePlayer1.getDisguisedName() : gamePlayer1.getName()) + "";
                        } else {
                            deathMessage = "&b" + (uhcPlayer.isDisguised() ? uhcPlayer.getDisguisedName() : uhcPlayer.getName()) + " &fwas killed by &b" + (gamePlayer1.isDisguised() ? gamePlayer1.getDisguisedName() : gamePlayer1.getName()) + "";
                        }
                    }
                } else {
                    deathMessage = "&b" + (uhcPlayer.isDisguised() ? uhcPlayer.getDisguisedName() : uhcPlayer.getName()) + " &fdied.";
                }

                p.setHealth(p.getMaxHealth());
                p.setMaxHealth(20);
                p.setFoodLevel(20);
                p.getInventory().clear();
                p.getInventory().setArmorContents(null);
                p.setExp(0);
                if (customDeathEvent.isDropItems()) {
                    ItemStack skull1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    ItemStack newSkull1 = new ItemBuilder(skull1).skullOwner(p.getName()).name(p.getName()).make();

                    p.getLocation().getWorld().dropItemNaturally(p.getLocation(), newSkull1);
                    items.forEach(itemStack -> p.getWorld().dropItemNaturally(p.getLocation(), itemStack));
                }
                if (damager != null) damager.setExp(damager.getExp() + p.getExp());
                Bukkit.broadcastMessage(ChatUtils.format(deathMessage));
            }

            //Calculate points and changes
            //UHC.players.remove(p.getUniqueId());
            uhcPlayer.setPlayerStatus(PlayerStatus.DEAD);

            if (damager != null) {
                UHCStatUpdateEvent updateEvent = new UHCStatUpdateEvent(gamePlayer1);
                Bukkit.getPluginManager().callEvent(updateEvent);
                if(!updateEvent.isCancelled()){
                    gamePlayer1.addKill(1);
                    gamePlayer1.addPoints(1);
                }

//                double curr = gameManager.getPointChanges().get(gamePlayer1.getUuid());
//                gameManager.getPointChanges().put(gamePlayer1.getUuid(), curr + 1);

                if (gameManager.getKills().containsKey(damager.getUniqueId())) {
                    gameManager.getKills().replace(damager.getUniqueId(), gameManager.getKills().get(damager.getUniqueId()) + 1);
                } else {
                    gameManager.getKills().put(damager.getUniqueId(), 1);
                }

            }

            UHCStatUpdateEvent updateEvent = new UHCStatUpdateEvent(uhcPlayer);
            Bukkit.getPluginManager().callEvent(updateEvent);
            if(!updateEvent.isCancelled()){
                uhcPlayer.setDeaths(uhcPlayer.getDeaths() + 1);
                uhcPlayer.setGamesPlayed(uhcPlayer.getGamesPlayed() + 1);
                double points = -0.25;
                //if(gameManager.getPointChanges().containsKey(p.getUniqueId())){
                if (gameManager.getKills().containsKey(p.getUniqueId())) {
                    points += gameManager.getKills().get(p.getUniqueId());
                }
                //points += uhcPlayer.getChangedLevel();

//                double curr = gameManager.getPointChanges().get(uhcPlayer.getUuid());
//                gameManager.getPointChanges().put(uhcPlayer.getUuid(), curr + points);
                uhcPlayer.addPoints(points);
                uhcPlayer.changed();
            }
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
            String changed = decimalFormat.format(uhcPlayer.getChangedLevel());

                p.sendMessage(ChatUtils.message("&eYou have died! Thank you for playing on NightShadePvP!"));
                p.sendMessage(ChatUtils.message("&eJoin the Discord at discord.nightshadepvp.com for updates and more!"));
                p.sendMessage(ChatUtils.format("&f&m----------------------------"));
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 5F, 5F);
                p.sendMessage(ChatUtils.message("&bYour Game Stats:"));
            p.sendMessage(ChatUtils.format("      " + (uhcPlayer.getChangedLevel() >= 0 ? "&a&o+" + changed + " points" : "&c&o-" + changed + " points")));
                p.sendMessage(ChatUtils.format("      &bCurrent Points: &f" + decimalFormat.format(uhcPlayer.getPoints())));
                p.sendMessage(ChatUtils.format("      &bKills: " + gameManager.getKills().getOrDefault(uhcPlayer.getUuid(), 0)));
                p.sendMessage(ChatUtils.format("&f&m----------------------------"));
            //gameManager.getPointChanges().remove(p.getUniqueId());
            //}

            if (user.hasRank(Rank.TRIAL)) { //Dragon and above can spectate the games, otherwise kick
                p.setAllowFlight(true);
                p.setFlying(true);
                p.setFlySpeed(0.2F);
                uhcPlayer.spec();
            } else {
                p.spigot().respawn();
                p.teleport(MConf.get().getSpawnLocation().asBukkitLocation(true));
                gameManager.getWhitelist().remove(p.getName().toLowerCase());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(uhcPlayer.getPlayerStatus() == PlayerStatus.PLAYING){
                            cancel();
                            return;
                        }
                        if (p.isOnline()) {
                            p.kickPlayer("You have died! Follow us on twitter @NightShadePvPMC for more!");
                        }
                    }
                }.runTaskLater(UHC.get(), 20 * 60);
            }
        } else {
            if (shot) {
                double damage = e.getFinalDamage();
                double health = (p.getHealth() - damage);
                DecimalFormat format = new DecimalFormat("##.#");
                format.setRoundingMode(RoundingMode.HALF_UP);
                String f = format.format(health);
                damager.sendMessage(me.blok601.nightshadeuhc.util.ChatUtils.message("&9" + p.getName() + " &b now has &9" + f + "❤"));
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (UHCPlayer.get(event.getPlayer()).getPlayerStatus() != PlayerStatus.PLAYING) {
            event.getPlayer().teleport(MConf.get().getSpawnLocation().asBukkitLocation(true));
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        if (!GameState.gameHasStarted()) return;

        Player p = e.getEntity();
        UHCPlayer uhcPlayer = UHCPlayer.get(p);
        if (uhcPlayer.isInArena()) return;
        NSPlayer user = NSPlayer.get(p);
        Player damager = null;
        UHCPlayer gamePlayer1 = null;
        if (p.getKiller() != null){
            damager = p.getKiller();
            gamePlayer1 = UHCPlayer.get(damager);
        }

        e.getDrops().clear();
        PlayerRespawn inv = new PlayerRespawn();
        inv.setArmor(p.getInventory().getArmorContents());
        inv.setItems(p.getInventory().getContents());
        inv.setLocation(p.getLocation());
        gameManager.getInvs().put(p.getUniqueId(), inv);

        ArrayList<ItemStack> items = new ArrayList<>(); //Get their loot
        for (ItemStack i : p.getInventory().getContents()) {
            if (i == null || i.getType() == Material.AIR) continue;
            items.add(i);
        }

        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (i == null || i.getType() == Material.AIR) continue;
            items.add(i);
        }

        //p.getWorld().strikeLightningEffect(p.getLocation());
        CustomDeathEvent customDeathEvent = new CustomDeathEvent(p, damager, items, p.getLocation(), true);
        Bukkit.getPluginManager().callEvent(customDeathEvent);

        if (!customDeathEvent.isCancelled()) {
            String deathMessage = ""; //Figure out death message based on how they died
            if(damager == null){
                deathMessage = "&b" + p.getName() + " &fdied.";
            }else{
                deathMessage = "&b" + p.getName() + " &fwas killed by &b" + damager.getName() + "";
            }

            p.setHealth(p.getMaxHealth());
            p.setFoodLevel(20);
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.setExp(0);
            if (customDeathEvent.isDropItems()) {
                ItemStack skull1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                ItemStack newSkull1 = new ItemBuilder(skull1).skullOwner(p.getName()).name(p.getName()).make();

                p.getLocation().getWorld().dropItemNaturally(p.getLocation(), newSkull1);
                items.forEach(itemStack -> p.getWorld().dropItemNaturally(p.getLocation(), itemStack));
            }
            if (damager != null) damager.setExp(damager.getExp() + p.getExp());
            e.setDeathMessage(ChatUtils.format(deathMessage));
        }

        //Calculate points and changes
        //UHC.players.remove(p.getUniqueId());
        uhcPlayer.setPlayerStatus(PlayerStatus.DEAD);

        if (damager != null) {
            gamePlayer1.addKill(1);
            gamePlayer1.addPoints(1);
//            double curr = gameManager.getPointChanges().get(gamePlayer1.getUuid());
//            gameManager.getPointChanges().put(gamePlayer1.getUuid(), curr + 1);

            if (gameManager.getKills().containsKey(damager.getUniqueId())) {
                gameManager.getKills().replace(damager.getUniqueId(), gameManager.getKills().get(damager.getUniqueId()) + 1);
            } else {
                gameManager.getKills().put(damager.getUniqueId(), 1);
            }
        }

        uhcPlayer.setDeaths(uhcPlayer.getDeaths() + 1);
        uhcPlayer.setGamesPlayed(uhcPlayer.getGamesPlayed() + 1);
        double points = -0.25;
        if (gameManager.getKills().containsKey(p.getUniqueId())) {
            points += gameManager.getKills().get(p.getUniqueId());
        }
        //points += uhcPlayer.getChangedLevel();

        //if(gameManager.getPointChanges().containsKey(p.getUniqueId())){
            DecimalFormat decimalFormat = new DecimalFormat("##.##");
//            double curr = gameManager.getPointChanges().get(uhcPlayer.getUuid());
//            gameManager.getPointChanges().put(uhcPlayer.getUuid(), curr + points);
            uhcPlayer.addPoints(points);
        String changed = decimalFormat.format(uhcPlayer.getChangedLevel());
            uhcPlayer.changed();

            p.sendMessage(ChatUtils.message("&eYou have died! Thank you for playing on NightShadePvP!"));
            p.sendMessage(ChatUtils.message("&eJoin the Discord at discord.nightshadepvp.com for updates and more!"));
            p.sendMessage(ChatUtils.format("&f&m----------------------------"));
            p.playSound(p.getLocation(), Sound.LEVEL_UP, 5F, 5F);
            p.sendMessage(ChatUtils.message("&bYour Game Stats:"));
        //p.sendMessage(ChatUtils.format("      " + (uhcPlayer.getChangedLevel() >= 0 ? "&a&o+" + changed + " points" : "&c&o-" + changed + " points")));
        // p.sendMessage(ChatUtils.format("      &bCurrent Points: &f" + decimalFormat.format(uhcPlayer.getPoints())));
        p.sendMessage(ChatUtils.format("      &bKills: " + gameManager.getKills().getOrDefault(uhcPlayer.getUuid(), 0)));
            p.sendMessage(ChatUtils.format("&f&m----------------------------"));
            //gameManager.getPointChanges().remove(p.getUniqueId());
        //}


        if (user.hasRank(Rank.TRIAL)) { //YouTube and above can spectate the games, otherwise kick
            p.setAllowFlight(true);
            p.setFlying(true);
            p.setFlySpeed(0.2F);
            uhcPlayer.spec();
        } else {
            p.spigot().respawn();
            p.teleport(MConf.get().getSpawnLocation().asBukkitLocation(true));
            gameManager.getWhitelist().remove(p.getName().toLowerCase());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(uhcPlayer.getPlayerStatus() == PlayerStatus.PLAYING) return;
                    if (p.isOnline()) {
                        p.kickPlayer("You have died! Follow us on twitter @NightShadePvPMC for more!");
                    }
                }
            }.runTaskLater(UHC.get(), 20 * 60);
        }
    }
}
