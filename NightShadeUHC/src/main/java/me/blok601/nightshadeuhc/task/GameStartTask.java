package me.blok601.nightshadeuhc.task;

import com.google.common.base.Joiner;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayerColl;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.staff.PvPCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.interfaces.StarterItems;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.FreezeUtil;
import me.blok601.nightshadeuhc.util.Util;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;


/**
 * Created by Blok on 3/16/2017.
 */
public class GameStartTask extends BukkitRunnable {

    private int finalHealTime;
    private int pvpTime;
    private World world;
    private int borderTime;
    private int firstShrink;
    private int counter;
    private int meetupTime;
    private GameManager gameManager;
    private ScenarioManager scenarioManager;

    public GameStartTask(Player p, int finalHealTime, int pvpTime, int borderTime, World world, int firstShrink, int meetupTime, GameManager gameManager, ScenarioManager scenarioManager) {
        this.finalHealTime = finalHealTime;
        this.pvpTime = pvpTime;
        this.world = world;
        this.borderTime = borderTime;
        this.firstShrink = firstShrink;
        this.meetupTime = meetupTime;
        this.counter = 120;
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        TeamManager.getInstance().setTeamManagement(false);
    }


    @Override
    public void run() {
        if (counter >= 0) {
            if (counter % 60 == 0 && counter != 0) {
                ChatUtils.sendAll("The game will start in " + counter / 60 + " minute&8(s&8)&b!");
            } else if (counter <= 10) {
                if (counter > 0) {
                    PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + counter + "\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 20, 0);
                    for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllOnline()) {
                        if (uhcPlayer.isUsingOldVersion()) {
                            uhcPlayer.msg(ChatUtils.message("&3The game will start in &e" + counter + " &3seconds"));
                            continue;
                        }
                        ((CraftPlayer) uhcPlayer.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }
                } else {
                    PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Go!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 20, 0);
                    for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllOnline()) {
                        if (uhcPlayer.isUsingOldVersion()) {
                            uhcPlayer.msg(ChatUtils.message("&3The game has started!"));
                            continue;
                        }
                        ((CraftPlayer) uhcPlayer.getPlayer()).getHandle().playerConnection.sendPacket(packet);
                    }
                    PvPCommand.disablePvP();
                    FreezeUtil.stop();

                    UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> {
                        uhcPlayer.setChangedLevel(0);
                        Player o = uhcPlayer.getPlayer();
                        o.setMaxHealth(20.0);
                        o.setHealth(o.getMaxHealth());
                        o.setFoodLevel(20);
                        o.setLevel(0);
                        o.setExp(0);
                        o.getActivePotionEffects().forEach(potionEffect -> o.removePotionEffect(potionEffect.getType()));
                    });

                    Core.get().getLogManager().log(Logger.LogType.INFO, "Everyone has been healed and fed!");
                    Bukkit.getOnlinePlayers().forEach(o -> o.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, gameManager.getStarterFood())));
                    Bukkit.getServer().getPluginManager().callEvent(new GameStartEvent());
                    UHCPlayerColl.get().getAllOnlinePlayers().stream().filter(uhcPlayer -> uhcPlayer.getPlayerStatus() == PlayerStatus.PLAYING).forEach(uhcPlayer -> {
                        for (Scenario scenario : scenarioManager.getEnabledScenarios()) {
                            if (scenario instanceof StarterItems) {
                                StarterItems starterItems = (StarterItems) scenario;

                                for (ItemStack i : starterItems.getStarterItems()) {
                                    if (uhcPlayer.getPlayer().getInventory().firstEmpty() == -1) {
                                        Location lock = uhcPlayer.getPlayer().getLocation();
                                        world.dropItem(lock, i);
                                    }
                                    else {
                                        uhcPlayer.getPlayer().getInventory().addItem(i);
                                    }
                                }

                            }
                        }
                    });

                    Bukkit.getOnlinePlayers().forEach(p -> {
                        UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> {
                            uhcPlayer.getPlayer().showPlayer(p);
                            p.showPlayer(uhcPlayer.getPlayer());
                        }); //Show all the ingame players to the player, and update for the game player

                        UHCPlayerColl.get().getSpectators().forEach(uhcPlayer -> {
                            p.hidePlayer(uhcPlayer.getPlayer());
                            uhcPlayer.getPlayer().showPlayer(p);
                        });


                        p.sendMessage(ChatUtils.format("&f&m-----------------------------------"));
                        p.sendMessage(ChatUtils.format("&fHost: &5" + gameManager.getHost().getName()));
                        if (scenarioManager.getEnabledScenarios().size() == 0) {
                            p.sendMessage(ChatUtils.format("&fScenarios: &5None"));
                        } else {
                            Scenario scenario = scenarioManager.getScen("Mystery Scenarios");
                            if (scenario != null && scenario.isEnabled()) {
                                p.sendMessage(ChatUtils.format("&fScenarios: &5" + scenario.getName()));
                            } else {
                                p.sendMessage(ChatUtils.format("&fScenarios: &5" + Joiner.on("&7, &5").join(scenarioManager.getEnabledScenarios().stream().map(Scenario::getName).collect(Collectors.toList()))));
                            }
                        }

                        p.sendMessage(" ");
                        p.sendMessage(ChatUtils.format("&fFinal Heal Time: &5" + gameManager.getFinalHealTime() / 60 + " minutes"));
                        p.sendMessage(ChatUtils.format("&fPvP Time: &5" + gameManager.getPvpTime() / 60 + " minutes"));
                        p.sendMessage(ChatUtils.format("&fMeetup Time: &5" + gameManager.getMeetupTime() / 60 + " minutes"));
                        p.sendMessage(ChatUtils.format("&fFirst Shrink Time: &5" + gameManager.getBorderTime() / 60 + " minutes"));

                        p.sendMessage(ChatUtils.format("&f&m-----------------------------------"));

                        p.playSound(p.getLocation(), Sound.BAT_DEATH, 5, 5);
                    });

                    FinalHealTask task = new FinalHealTask(finalHealTime);
                    if (finalHealTime > 0) {
                        task.runTaskTimer(UHC.get(), 0, Util.TICKS);
                    }
                    gameManager.setFinalHealTask(task);

                    PvPTask pvpTask = new PvPTask(pvpTime, world);
                    if (pvpTime > 0) {
                        pvpTask.runTaskTimer(UHC.get(), 0, Util.TICKS);
                    }
                    gameManager.setPvpTask(pvpTask);


                    WorldBorderTask worldBorderTask = new WorldBorderTask(borderTime, world, firstShrink, gameManager);
                    if (borderTime > 0) {
                        worldBorderTask.runTaskTimer(UHC.get(), 0, Util.TICKS);
                    }
                    gameManager.setWorldBorderTask(worldBorderTask);

                    MeetupTask meetupTask = new MeetupTask(meetupTime);
                    if (meetupTime > 0) {
                        meetupTask.runTaskTimer(UHC.get(), 0, Util.TICKS);
                    }
                    gameManager.setMeetupTask(meetupTask);
                    TimerTask timerTask = gameManager.getTimer();
                    timerTask.start();


                    GameState.setState(GameState.INGAME);
                    Bukkit.getOnlinePlayers().forEach(player -> UHC.getScoreboardManager().applyBoard(player));
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        for (Player p2 : Bukkit.getOnlinePlayers()) {
                            UHCPlayer gamePlayer = UHCPlayer.get(p2.getUniqueId());
                            if (!gamePlayer.isSpectator()) {
                                p.showPlayer(p2);
                            } else {
                                p.hidePlayer(p2);
                            }
                        }
                    }
                    counter = -1;
                    cancel();
                    return;
                }
            }
        }
        counter--;
    }
}

