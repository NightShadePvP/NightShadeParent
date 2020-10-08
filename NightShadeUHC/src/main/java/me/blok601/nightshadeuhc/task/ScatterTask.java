package me.blok601.nightshadeuhc.task;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ActionBarUtil;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ScatterUtil;
import me.blok601.nightshadeuhc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Created by Blok on 4/6/2017.
 */
public class ScatterTask extends BukkitRunnable {

    private ArrayList<Player> players;
    private World world;
    private int radius;
    private Player host;
    private int finalHealTime;
    private int pvpTime;
    private int borderTime;
    private boolean isTeam;
    private int firstShrink;
    private int meetupTime;

    private GameManager gameManager;
    private ScenarioManager scenarioManager;


    public ScatterTask(ArrayList<Player> players, World world, int radius, Player host, int finalHealTime, int pvpTime, int borderTime, boolean isTeam, int firstShrink, int meeutpTime, GameManager gameManager, ScenarioManager scenarioManager) {
        this.world = world;
        this.players = players;
        this.radius = radius;
        this.host = host;
        this.finalHealTime = finalHealTime;
        this.pvpTime = pvpTime;
        this.borderTime = borderTime;
        this.isTeam = isTeam;
        this.firstShrink = firstShrink;
        this.meetupTime = meeutpTime;
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        gameManager.IS_SCATTERING = true;
        Bukkit.getOnlinePlayers().stream().filter(o -> !gameManager.getWhitelist().contains(o.getName().toLowerCase())).forEach(o -> gameManager.getWhitelist().add(o.getName().toLowerCase()));
        gameManager.setWhitelistEnabled(true);
        Util.staffLog("Everyone has been added to the whitelist and whitelist has been enabled!");


    }

    @Override
    public void run() {
        if (isTeam) {
            if (TeamManager.getInstance().isRvB()) {
                if (TeamManager.getInstance().getRvBScatterType() == 0) {
                    //Scatter like solos
                    Bukkit.getOnlinePlayers().forEach(o -> {
                        for(PotionEffect effect: o.getActivePotionEffects()){
                            o.removePotionEffect(effect.getType());
                            o.setMaxHealth(20);
                        }
                        o.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                        o.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
                    });
                    if (players.size() == 0) {
                        GameState.setState(GameState.STARTING);
//                Util.staffLog("Scatter has finished");
                        Bukkit.getOnlinePlayers().forEach(o -> ActionBarUtil.sendActionBarMessage(o, "§5The scatter has finished!", 1, UHC.get()));
                        Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.WATER_BREATHING));
                        Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
                        gameManager.IS_SCATTERING = false;
                        Bukkit.getOnlinePlayers().stream().filter(o -> !NSPlayer.get(o.getUniqueId()).hasRank(Rank.TRIAL)).forEach(o -> o.setGameMode(GameMode.SURVIVAL));

                        new GameStartTask(host, finalHealTime, pvpTime, borderTime, world, firstShrink, meetupTime, gameManager, scenarioManager).runTaskTimer(UHC.get(), 0, Util.TICKS);
                        this.cancel();
                        return;
                    } else {
                        Player p = players.get(0);
                        ScatterUtil.scatterPlayer(world, radius - 1, p);
                        UHCPlayer.get(p).setPlayerStatus(PlayerStatus.PLAYING);
                        p.setMaxHealth(20);
                        p.setHealth(p.getMaxHealth());
                        p.setFoodLevel(10);
                        p.setFlying(false);
                        p.setAllowFlight(false);
                        p.sendMessage(ChatUtils.message("&bYou have been scattered!"));
                        players.remove(p);
                    }
                } else {

                    Bukkit.getOnlinePlayers().forEach(o -> {
                        for(PotionEffect effect: o.getActivePotionEffects()){
                            o.removePotionEffect(effect.getType());
                            o.setMaxHealth(20);
                        }
                        o.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                        o.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
                    });
                    int tms = 0;
                    int solos = 0;
                    for (Team team : TeamManager.getInstance().getTeams()) {
                        ArrayList<Player> players = new ArrayList<>();
                        Player toScatter;
                        for (String string : team.getMembers()) {
                            toScatter = Bukkit.getPlayer(string);
                            if (toScatter == null) continue;

                            players.add(toScatter);
                        }

                        ScatterUtil.scatter(world, radius, players);
                        players.forEach(player -> {
                            player.setMaxHealth(20);
                            player.setHealth(player.getMaxHealth());
                            player.setFoodLevel(10);
                            player.setFlying(false);
                            player.setAllowFlight(false);

                            UHCPlayer.get(player).setPlayerStatus(PlayerStatus.PLAYING);
                        });
                        tms++;
                    }

                    Util.staffLog("&3Finished scattering &e" + tms + " &3teams");
                    Util.staffLog("&3Starting to scatter solos!");

                    UHCPlayer gamePlayer;
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        gamePlayer = UHCPlayer.get(player.getUniqueId());
                        if (TeamManager.getInstance().getTeam(player) != null) {
                            continue;
                        }

                        if (gamePlayer.isSpectator()) continue;

                        ScatterUtil.scatterPlayer(world, radius, player);
                        gamePlayer.setPlayerStatus(PlayerStatus.PLAYING);
                        solos++;
                    }

                    Util.staffLog("&3Finished scattering &e" + solos + " &3solos");
                    // Util.staffLog("Scatter has finished");
                    Bukkit.getOnlinePlayers().forEach(o -> ActionBarUtil.sendActionBarMessage(o, "§5The scatter has finished!", 1, UHC.get()));
                    Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.WATER_BREATHING));
                    Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
                    gameManager.IS_SCATTERING = false;
                    new GameStartTask(host, finalHealTime, pvpTime, borderTime, world, firstShrink, meetupTime, gameManager, scenarioManager).runTaskTimer(UHC.get(), 0, Util.TICKS);
                    this.cancel();
                }
            } else {

                Bukkit.getOnlinePlayers().forEach(o -> {
                    for(PotionEffect effect: o.getActivePotionEffects()){
                        o.removePotionEffect(effect.getType());
                        o.setMaxHealth(20);
                    }
                    o.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                    o.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
                });
                int tms = 0;
                int totalTeams = TeamManager.getInstance().getTeams().size();
                int solos = 0;

                for (Team team : TeamManager.getInstance().getTeams()) {
                    ArrayList<Player> players = new ArrayList<>();
                    Player toScatter;
                    for (String string : team.getMembers()) {
                        toScatter = Bukkit.getPlayer(string);
                        if (toScatter == null) continue;
                        UHCPlayer.get(toScatter).setPlayerStatus(PlayerStatus.PLAYING);

                        players.add(toScatter);
                    }

                    ScatterUtil.scatter(world, radius, players);
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        ActionBarUtil.sendActionBarMessage(player, ("§bScattered §8» §f" + tms + "§b/" + totalTeams + " teams"), 1, UHC.get());
                    }
                    tms++;
                }

                Util.staffLog("&3Finished scattering &e" + tms + " &3teams");
                Util.staffLog("&3Starting to scatter solos!");

                UHCPlayer gamePlayer;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    gamePlayer = UHCPlayer.get(player.getUniqueId());
                    if (TeamManager.getInstance().getTeam(player) != null) {
                        continue;
                    }

                    if (gamePlayer.isSpectator()) continue;

                    ScatterUtil.scatterPlayer(world, radius, player);
                    gamePlayer.setPlayerStatus(PlayerStatus.PLAYING);
                    solos++;
                    for (Player ps : Bukkit.getOnlinePlayers()) {
                        ActionBarUtil.sendActionBarMessage(ps, ("§bScattered §8» §f" + solos + " §bsolos"), 1, UHC.get());
                    }
                }

                Util.staffLog("&3Finished scattering &e" + solos + " &3solos");
                // Util.staffLog("Scatter has finished");
                Bukkit.getOnlinePlayers().forEach(o -> ActionBarUtil.sendActionBarMessage(o, "§5The scatter has finished!", 1, UHC.get()));
                Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.WATER_BREATHING));
                Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
                gameManager.IS_SCATTERING = false;
                new GameStartTask(host, finalHealTime, pvpTime, borderTime, world, firstShrink, meetupTime, gameManager, scenarioManager).runTaskTimer(UHC.get(), 0, Util.TICKS);
                this.cancel();
            }


        } else {
            int scattered = 0;
            int totalSolos = players.size();
            Bukkit.getOnlinePlayers().forEach(o -> {
                for(PotionEffect effect: o.getActivePotionEffects()){
                    o.removePotionEffect(effect.getType());
                }
                o.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                o.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0));
            });
            if (players.size() == 0) {
                GameState.setState(GameState.STARTING);
//                Util.staffLog("Scatter has finished");
                Bukkit.getOnlinePlayers().forEach(o -> ActionBarUtil.sendActionBarMessage(o, "§5The scatter has finished!", 1, UHC.get()));
                Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.WATER_BREATHING));
                Bukkit.getOnlinePlayers().forEach(o -> o.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE));
                gameManager.IS_SCATTERING = false;
                Bukkit.getOnlinePlayers().stream().filter(o -> !NSPlayer.get(o.getUniqueId()).hasRank(Rank.TRIAL)).forEach(o -> o.setGameMode(GameMode.SURVIVAL));

                new GameStartTask(host, finalHealTime, pvpTime, borderTime, world, firstShrink, meetupTime, gameManager, scenarioManager).runTaskTimer(UHC.get(), 0, Util.TICKS);
                this.cancel();
                return;
            } else {
                Player p = players.get(0);
                ScatterUtil.scatterPlayer(world, radius, p);
                scattered++;
                UHCPlayer.get(p).setPlayerStatus(PlayerStatus.PLAYING);
                p.sendMessage(ChatUtils.message("&bYou have been scattered!"));
                players.remove(p);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ActionBarUtil.sendActionBarMessage(player, ("§bScattered §8» §f" + scattered + "§b/" + totalSolos + " solos"), 1, UHC.get());
                }
            }
        }
    }

}
