package me.blok601.nightshadeuhc.stat.listener;

import com.google.common.base.Joiner;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.*;
import me.blok601.nightshadeuhc.event.PlayerJoinGameLateEvent;
import me.blok601.nightshadeuhc.manager.FakePlayerManager;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.LoggerManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scoreboard.ScoreboardHandler;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import me.blok601.nightshadeuhc.util.ScatterUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.stream.Collectors;

/**
 * Created by Blok on 9/4/2017.
 */
public class JoinListener implements Listener {

    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private UHC uhc;

    public JoinListener(GameManager gameManager, ScenarioManager scenarioManager, UHC uhc) {
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.uhc = uhc;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        UHCPlayer gamePlayer = UHCPlayer.get(player);

        gamePlayer.setSpectator(false);
        gamePlayer.setReceiveHelpop(true);
        player.sendMessage(ChatUtils.message("&5Welcome &5back to the NightShadePvP Network!"));

        Scenario scen = scenarioManager.getScen("Secret Teams");
        if(scen != null && !scen.isEnabled()){
            new BukkitRunnable(){
                @Override
                public void run() {
                    for (me.blok601.nightshadeuhc.entity.object.Team team : TeamManager.getInstance().getTeams()){
                        UHC.getScoreboardManager().getPlayerBoards().forEach((uuid, playerBoard) -> {
                            Scoreboard scoreboard = playerBoard.getScoreboard();
                            if (scoreboard.getTeam(team.getName()) != null) {
                                scoreboard.getTeam(team.getName()).unregister();
                            }

                            org.bukkit.scoreboard.Team t = scoreboard.registerNewTeam(team.getName());
                            t.setPrefix(team.getColor());
                            for (String mem : team.getMembers()) {
                                t.addEntry(mem);
                            }
                        });
                    }



                }
            }.runTaskAsynchronously(uhc);
        }


        if (GameState.getState() == GameState.INGAME || GameState.getState() == GameState.MEETUP) {
            if (LoggerManager.getInstance().hasLogger(player.getUniqueId())) {
                LoggerManager.getInstance().getLogger(player.getUniqueId()).remove(false);
            }
        }

        //Hopefully fixed that stupid hidden player bullshit here
        UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> {
            uhcPlayer.getPlayer().showPlayer(player);
            player.showPlayer(uhcPlayer.getPlayer());
        }); //Show all the ingame players to the player, and update for the game player

        UHCPlayerColl.get().getSpectators().forEach(uhcPlayer -> {
            player.hidePlayer(uhcPlayer.getPlayer());
            uhcPlayer.getPlayer().showPlayer(player);
        });


        if (gameManager.getRespawnQueue().contains(player.getName().toLowerCase())) {
            // They should be respawned
            //TODO: Stopped here
            if (gamePlayer.isStaffMode()) {
                gamePlayer.unspec();
                Bukkit.getOnlinePlayers().forEach(o -> o.showPlayer(player));
                player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
                gamePlayer.setStaffMode(false);
                gamePlayer.getPlayer().getInventory().clear();
                gamePlayer.getPlayer().getInventory().setArmorContents(null);
                gamePlayer.getPlayer().chat("/rea");
            }

            gameManager.getRespawnQueue().remove(player.getName().toLowerCase());
            PlayerRespawn obj = gameManager.getInvs().get(player.getUniqueId());
            player.teleport(obj.getLocation());
            gamePlayer.setPlayerStatus(PlayerStatus.PLAYING);
            player.getInventory().setArmorContents(obj.getArmor());
            player.getInventory().setContents(obj.getItems());


            if (gamePlayer.isVanished()) gamePlayer.unVanish();

            player.setGameMode(GameMode.SURVIVAL);
            //UHC.players.add(player.getUniqueId());
            gameManager.getInvs().remove(player.getUniqueId());
            player.sendMessage(ChatUtils.message("&aYou have been respawned!"));
            Bukkit.getPluginManager().callEvent(new PlayerJoinGameLateEvent(player));
        }

        if (gameManager.getLateScatter().contains(player.getName().toLowerCase())) {
            //They should be late started
            ScatterUtil.scatterPlayer(gameManager.getWorld(), (int) gameManager.getBorderSize(), player);
            //UHC.players.add(player.getUniqueId());
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setLevel(0);
            player.setExp(0F);
            player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 10));
            ScatterUtil.scatterPlayer(gameManager.getWorld(), (int) gameManager.getBorderSize(), player);
            gamePlayer.setPlayerStatus(PlayerStatus.PLAYING);
            player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 5, 5);
            player.sendMessage(ChatUtils.message("&eYou were scattered!"));
            gameManager.getLateScatter().remove(player.getName().toLowerCase());
            Bukkit.getPluginManager().callEvent(new PlayerJoinGameLateEvent(player));
        }


        if (GameState.gameHasStarted()) {
            if (PlayerUtils.inGameWorld(player) && UHC.loggedOutPlayers.contains(player.getUniqueId())) {
                UHC.loggedOutPlayers.remove(player.getUniqueId());
                gamePlayer.setPlayerStatus(PlayerStatus.PLAYING);
            } else {
                gamePlayer.setPlayerStatus(PlayerStatus.LOBBY);
            }

            StringBuilder builder = new StringBuilder();
            scenarioManager.getEnabledScenarios().forEach(scenario -> builder.append(scenario.getName()).append(", "));
            player.sendMessage(ChatUtils.format("&f&m-----------------------------------"));
            player.sendMessage(ChatUtils.format("&fHost: &5" + gameManager.getHost().getName()));
            if (scenarioManager.getEnabledScenarios().size() == 0) {
                player.sendMessage(ChatUtils.format("&fScenarios: &5None"));
            } else {
                Scenario scenario = scenarioManager.getScen("Mystery Scenarios");
                if (scenario != null && scenario.isEnabled()) {
                    player.sendMessage(ChatUtils.format("&fScenarios: &5" + scenario.getName()));
                } else {
                    player.sendMessage(ChatUtils.format("&fScenarios: &5" + Joiner.on("&7, &5").join(scenarioManager.getEnabledScenarios().stream().map(Scenario::getName).collect(Collectors.toList()))));
                }
            }

            player.sendMessage(" ");
            player.sendMessage(ChatUtils.format("&fFinal Heal Time: &5" + gameManager.getFinalHealTime() / 60 + " minutes"));
            player.sendMessage(ChatUtils.format("&fPvP Time: &5" + gameManager.getPvpTime() / 60 + " minutes"));
            player.sendMessage(ChatUtils.format("&fMeetup Time: &5" + gameManager.getMeetupTime() / 60 + " minutes"));
            player.sendMessage(ChatUtils.format("&fFirst Shrink Time: &5" + gameManager.getBorderTime() / 60 + " minutes"));

            player.sendMessage(ChatUtils.format("&f&m-----------------------------------"));

        } else {
            player.getEnderChest().clear();
            gamePlayer.setPlayerStatus(PlayerStatus.LOBBY);
            UHC.getScoreboardManager().applyBoard(player).addUpdates(Bukkit.getOnlinePlayers());
            //UHC.get().getScoreboardManager().getPlayerScoreboards().put(player, new PlayerScoreboard(new LobbyProvider(uhc, gameManager, scenarioManager), player));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent e) {
        //UHC.get().getScoreboardManager().removeFromPlayerCache(e.getPlayer());
        Player p = e.getPlayer();
        UHCPlayer gamePlayer = UHCPlayer.get(p.getUniqueId());
        FakePlayerManager.getInstance().getNpcs().forEach(fakePlayer -> fakePlayer.despawnFor(p));
        if (gamePlayer.isSpectator() || gamePlayer.isStaffMode()) {
            return;
        }

        if (gamePlayer.getPlayerStatus() == PlayerStatus.PLAYING && PlayerUtils.inGameWorld(p)) {

            if (GameState.getState() == GameState.INGAME || GameState.getState() == GameState.MEETUP) {
                if (1 > 10) {
                    //Kill them
                    for (ItemStack itemStack : p.getInventory().getContents()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                        p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
                    }

                    for (ItemStack itemStack : p.getInventory().getArmorContents()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                        p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
                    }

                    p.getLocation().getWorld().strikeLightningEffect(p.getLocation().add(0, 10, 0));

                    ItemStack skull1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    ItemStack newSkull1 = new ItemBuilder(skull1).skullOwner(p.getName()).name(p.getName()).make();
                    p.getLocation().getWorld().dropItemNaturally(p.getLocation(), newSkull1);

                    ((ExperienceOrb) p.getLocation().getWorld().spawnEntity(p.getLocation(),
                            EntityType.EXPERIENCE_ORB)).setExperience((int) p.getExp()); // Might be buggy, test

                    gameManager.getWhitelist().remove(p.getName().toLowerCase());
                    LoggerManager.getInstance().getDeadLoggers().add(new LoggedOutPlayer(p.getUniqueId(), p.getName(), uhc));
                    return;
                }
                if (!LoggerManager.getInstance().hasLogger(e.getPlayer().getUniqueId())) {
                    LoggedOutPlayer loggedOutPlayer = new LoggedOutPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName(), uhc);
                    loggedOutPlayer.setArmor(e.getPlayer().getInventory().getArmorContents());
                    loggedOutPlayer.setItems(e.getPlayer().getInventory().getContents());
                    loggedOutPlayer.setLocation(e.getPlayer().getLocation());
                    loggedOutPlayer.setXp(e.getPlayer().getExp());
                    LoggerManager.getInstance().getLoggedOutPlayers().add(loggedOutPlayer);
                    loggedOutPlayer.start();
                }
            }

            UHC.loggedOutPlayers.add(p.getUniqueId());
        }
    }


    @EventHandler
    public void onKick(PlayerKickEvent e) {
        //UHC.get().getScoreboardManager().removeFromPlayerCache(e.getPlayer());
        Player p = e.getPlayer();
        UHCPlayer gamePlayer = UHCPlayer.get(p.getUniqueId());
        FakePlayerManager.getInstance().getNpcs().forEach(fakePlayer -> fakePlayer.despawnFor(p));


        if (gamePlayer.isSpectator() || gamePlayer.isStaffMode()) {
            return;
        }

        if (gamePlayer.getPlayerStatus() == PlayerStatus.PLAYING && PlayerUtils.inGameWorld(p)) {
            if (GameState.getState() == GameState.INGAME || GameState.getState() == GameState.MEETUP) {
                if (gamePlayer.getCombatLogTimer() <= 30) {
                    //Kill them
                    for (ItemStack itemStack : p.getInventory().getContents()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                        p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
                    }

                    for (ItemStack itemStack : p.getInventory().getArmorContents()) {
                        if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                        p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
                    }

                    p.getLocation().getWorld().strikeLightningEffect(p.getLocation().add(0, 10, 0));

                    ItemStack skull1 = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                    ItemStack newSkull1 = new ItemBuilder(skull1).skullOwner(p.getName()).name(p.getName()).make();
                    p.getLocation().getWorld().dropItemNaturally(p.getLocation(), newSkull1);

                    ((ExperienceOrb) p.getLocation().getWorld().spawnEntity(p.getLocation(),
                            EntityType.EXPERIENCE_ORB)).setExperience((int) p.getExp()); // Might be buggy, test

                    gameManager.getWhitelist().remove(p.getName().toLowerCase());
                    LoggerManager.getInstance().getDeadLoggers().add(new LoggedOutPlayer(p.getUniqueId(), p.getName(), uhc));
                    return;
                }
                if (!LoggerManager.getInstance().hasLogger(e.getPlayer().getUniqueId())) {
                    LoggedOutPlayer loggedOutPlayer = new LoggedOutPlayer(e.getPlayer().getUniqueId(), e.getPlayer().getName(), uhc);
                    loggedOutPlayer.setArmor(e.getPlayer().getInventory().getArmorContents());
                    loggedOutPlayer.setItems(e.getPlayer().getInventory().getContents());
                    loggedOutPlayer.setLocation(e.getPlayer().getLocation());
                    loggedOutPlayer.setXp(e.getPlayer().getExp());
                    LoggerManager.getInstance().getLoggedOutPlayers().add(loggedOutPlayer);
                    loggedOutPlayer.start();
                }
            }
            UHC.loggedOutPlayers.add(p.getUniqueId());
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        NSPlayer user = NSPlayer.get(p);
        if (gameManager.isWhitelistEnabled()) {

            System.out.println(user.getRank().toString());
            if (user.getRank().getValue() >= Rank.TRIAL.getValue()) {
                e.allow();
                return;
            }

            if (gameManager.getRespawnQueue().contains(e.getPlayer().getName().toLowerCase())) {
                e.allow();
                LoggerManager.getInstance().getDeadLoggers().remove(LoggerManager.getInstance().getDeadLogger(p.getUniqueId()));
                return;
            }

            if (!gameManager.getWhitelist().contains(e.getPlayer().getName().toLowerCase()) && !LoggerManager.getInstance().getDeadLoggers().contains(p.getUniqueId())) {
                if (gameManager.getRespawnQueue().contains(e.getPlayer().getName().toLowerCase())) {
                    e.allow();
                    return;
                }
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You are not on the whitelist!");
            } else {
                e.allow();
            }

            if (gameManager.getDeathBans().contains(p.getUniqueId())) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You have already died!");
                return;
            }

            if (LoggerManager.getInstance().isDeadLogger(p.getUniqueId())) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You have died! Follow us on twitter @NightShadePvPMC for more!");
            }

        }

        if ((Bukkit.getOnlinePlayers().size() - UHCPlayerColl.get().getSpectators().size()) == gameManager.getMaxPlayers()) {
            if (!user.hasRank(Rank.YOUTUBE)) {
                e.disallow(PlayerLoginEvent.Result.KICK_FULL, "The server is full!");
            } else {
                e.allow();
            }
        }
    }


}
