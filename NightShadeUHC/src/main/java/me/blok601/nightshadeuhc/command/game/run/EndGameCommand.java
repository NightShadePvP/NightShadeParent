package me.blok601.nightshadeuhc.command.game.run;

import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.quest.HalfHeartQuest;
import com.nightshadepvp.core.quest.QuestHandler;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.MConf;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.event.GameEndEvent;
import me.blok601.nightshadeuhc.event.UHCStatUpdateEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.MolesScenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.PlayerUtils;
import me.blok601.nightshadeuhc.util.Util;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Blok on 7/20/2017.
 */
public class EndGameCommand implements UHCCommand {

    private ScenarioManager scenarioManager;
    private QuestHandler questHandler;

    public EndGameCommand(ScenarioManager scenarioManager, QuestHandler questHandler) {
        this.scenarioManager = scenarioManager;
        this.questHandler = questHandler;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "endgame"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if (args.length != 1) {
            p.sendMessage(ChatUtils.message("&cUsage: /endgame <player/player on team/moles>"));
            return;
        }

        boolean isTeam = GameManager.get().isIsTeam();

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            p.sendMessage(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }

        if (isTeam) {

            if (args[0].equalsIgnoreCase("moles")) {
                //The moles won
                if (!scenarioManager.getScen("Moles").isEnabled()) {
                    p.sendMessage(ChatUtils.message("&cMoles must be enabled to do this!"));
                    return;
                }

                StringBuilder sb = new StringBuilder();
                Player pl;
                ArrayList<UUID> winners = new ArrayList<>();
                UHCPlayer gamePlayer;
                ArrayList<Player> onlineMoles = new ArrayList<>();
                for (UUID uuid : MolesScenario.moles.keySet()) {
                    pl = Bukkit.getPlayer(uuid);
                    gamePlayer = UHCPlayer.get(uuid);
                    winners.add(uuid);
                    if (pl == null) {
                        gamePlayer.addPoints(10 / TeamManager.getInstance().getTeamSize() * 1.0);
                        continue;
                    }

                    onlineMoles.add(pl);
                    gamePlayer.changed();
                    gamePlayer.msg(ChatUtils.message("&eYour stats have been updated! Do /stats to check them."));
                    sb.append(pl.getName()).append(", ");

                }

                Bukkit.getPluginManager().callEvent(new GameEndEvent(winners));

                String f = sb.toString().trim();

                PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Congratulations!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 60, 0);
                onlineMoles.forEach(player -> {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
                });


                PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + f.substring(0, f.length() - 1) + " has won!\",\"color\":\"dark_red\"}"), 0, 60, 0);
                PacketPlayOutTitle newTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Game Over!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 60, 0);


                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) lamba -> {
                    ((CraftPlayer) lamba).getHandle().playerConnection.sendPacket(subtitle);
                    if (!MolesScenario.moles.containsKey(lamba.getUniqueId())) {
                        ((CraftPlayer) lamba).getHandle().playerConnection.sendPacket(newTitle);
                    }
                });
            } else {

                if (TeamManager.getInstance().getTeam(target) == null) {
                    p.sendMessage(ChatUtils.message("&cThat player doesn't have a team!"));
                    return;
                }

                Player pl;

                StringBuilder sb = new StringBuilder();

                ArrayList<UUID> winners = new ArrayList<>();
                NSPlayer user;
                UHCPlayer gamePlayer;
                Team targetTeam = TeamManager.getInstance().getTeam(target);
                for (String uuid : targetTeam.getMembers()) {
                    pl = Bukkit.getPlayer(uuid);
                    if (pl == null) {
                        continue;
                    }

                    user = NSPlayer.get(pl);
                    gamePlayer = UHCPlayer.get(pl);
                    if (gamePlayer.getPlayerStatus() != PlayerStatus.PLAYING) continue;
                    winners.add(pl.getUniqueId());
                    if (pl.getHealth() == 1) {
                        if (user.getCompletedQuests().add(questHandler.getQuest(HalfHeartQuest.class).getId())) {
                            questHandler.getQuest(HalfHeartQuest.class).complete(user);
                        }
                    }

//                user.setPrefix(ChatColor.RED + "[Winner] ");
                    UHCStatUpdateEvent updateEvent = new UHCStatUpdateEvent(gamePlayer);
                    Bukkit.getPluginManager().callEvent(updateEvent);
                    if (!updateEvent.isCancelled()) {
                        double toAdd = 0;
                        gamePlayer.setGamesWon(gamePlayer.getGamesWon() + 1);
                        gamePlayer.setGamesPlayed(gamePlayer.getGamesPlayed() + 1);
                        toAdd += (10 / targetTeam.getMembers().size());
                        gamePlayer.addPoints(toAdd);
//                    double curr = GameManager.get().getPointChanges().get(gamePlayer.getUuid());
//                    GameManager.get().getPointChanges().put(gamePlayer.getUuid(), curr + toAdd);
                        if (user.getRank() == Rank.PLAYER) {
                            if (gamePlayer.getGamesWon() == 1) {
                                user.setPrefix(ChatUtils.format("&8[&c★&8]"));
                            }
                            if (gamePlayer.getGamesWon() == 2) {
                                user.setPrefix(ChatUtils.format("&8[&c★★&8]"));
                            }
                            if (gamePlayer.getGamesWon() == 3) {
                                user.setPrefix(ChatUtils.format("&8[&c★★★&8]"));
                            }
                            if (gamePlayer.getGamesWon() == 4) {
                                user.setPrefix(ChatUtils.format("&8[&c★★★★&8]"));
                            }
                            if (gamePlayer.getGamesWon() >= 5 && gamePlayer.getGamesWon() < 10) {
                                user.setPrefix(ChatUtils.format("&8[&c-=★=-&8]"));
                            }
                            if (gamePlayer.getGamesWon() >= 10) {
                                user.setPrefix(ChatUtils.format("&8[&c-=(★)=-&8]"));
                            }
                        }
                        gamePlayer.changed();
                        gamePlayer.msg(ChatUtils.message("&eYour stats have been updated! Do /stats to check them."));
                    }
                    sb.append(pl.getName()).append(", ");
                }

                Bukkit.getPluginManager().callEvent(new GameEndEvent(winners));

                String f = sb.toString().trim();

                PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Congratulations!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 60, 0);
                ((CraftPlayer) target).getHandle().playerConnection.sendPacket(title);
                targetTeam.getMembers().stream().filter(s1 -> Bukkit.getPlayer(s1) != null).forEach(s1 -> {
                    ((CraftPlayer) Bukkit.getPlayer(s1)).getHandle().playerConnection.sendPacket(title);
                });


                PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + f.substring(0, f.length() - 1) + " has won!\",\"color\":\"dark_red\"}"), 0, 60, 0);
                PacketPlayOutTitle newTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Game Over!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 60, 0);


                Bukkit.getOnlinePlayers().forEach((Consumer<Player>) lambda -> {
                    ((CraftPlayer) lambda).getHandle().playerConnection.sendPacket(subtitle);
                    if (!targetTeam.getMembers().contains(lambda.getName())) {
                        ((CraftPlayer) lambda).getHandle().playerConnection.sendPacket(newTitle);
                    }

                });
            }
        } else {
            NSPlayer user = NSPlayer.get(target.getUniqueId());
            UHCPlayer gamePlayer = UHCPlayer.get(target.getUniqueId());
            UHCStatUpdateEvent updateEvent = new UHCStatUpdateEvent(gamePlayer);
            Bukkit.getPluginManager().callEvent(updateEvent);
            if (!updateEvent.isCancelled()) {

                double points = 0;
                gamePlayer.setGamesWon(gamePlayer.getGamesWon() + 1);
                gamePlayer.setGamesPlayed(gamePlayer.getGamesPlayed() + 1);
                if (GameManager.get().getKills().containsKey(p.getUniqueId())) {
                    points += GameManager.get().getKills().get(p.getUniqueId());
                }
                gamePlayer.addPoints(10 + points);
                if (target.getHealth() == 1) {
                    if (user.getCompletedQuests().add(questHandler.getQuest(HalfHeartQuest.class).getId())) {
                        questHandler.getQuest(HalfHeartQuest.class).complete(user);
                    }
                }
//            double curr = GameManager.get().getPointChanges().get(gamePlayer.getUuid());
//            GameManager.get().getPointChanges().put(gamePlayer.getUuid(), curr + 10);
                if (user.getRank() == Rank.PLAYER) {
                    if (gamePlayer.getGamesWon() == 1) {
                        user.setPrefix(ChatUtils.format("&8[&c★&8]"));
                    }
                    if (gamePlayer.getGamesWon() == 2) {
                        user.setPrefix(ChatUtils.format("&8[&c★★&8]"));
                    }
                    if (gamePlayer.getGamesWon() == 3) {
                        user.setPrefix(ChatUtils.format("&8[&c★★★&8]"));
                    }
                    if (gamePlayer.getGamesWon() == 4) {
                        user.setPrefix(ChatUtils.format("&8[&c★★★★&8]"));
                    }
                    if (gamePlayer.getGamesWon() >= 5 && gamePlayer.getGamesWon() < 10) {
                        user.setPrefix(ChatUtils.format("&8[&c-=★=-&8]"));
                    }
                    if (gamePlayer.getGamesWon() >= 10) {
                        user.setPrefix(ChatUtils.format("&8[&c-=(★)=-&8]"));

                    }
                }
                gamePlayer.changed();
            }
            Bukkit.getPluginManager().callEvent(new GameEndEvent(Collections.singletonList(target.getUniqueId())));

            PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Congratulations!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 60, 0);
            ((CraftPlayer) target).getHandle().playerConnection.sendPacket(title);


            PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + target.getName() + " has won!\",\"color\":\"dark_red\"}"), 0, 60, 0);
            PacketPlayOutTitle newTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"Game Over!\",\"color\":\"dark_aqua\",\"bold\":true}"), 0, 60, 0);


            Bukkit.getOnlinePlayers().forEach((Consumer<Player>) pl -> {
                ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(subtitle);
                if (!pl.getName().equalsIgnoreCase(target.getName())) {
                    ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(newTitle);
                }

            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
                    onlinePlayer.teleport(Core.get().getSpawn());
                    onlinePlayer.getInventory().clear();
                    onlinePlayer.getInventory().setArmorContents(null);
                });
                Util.staffLog("&4The Server Will Restart in 30 seconds!");

                World nether = GameManager.get().getNetherWorld();
                if (nether != null) {
                    UHC.getMultiverseCore().getMVWorldManager().removeWorldFromConfig(nether.getName());
                    Bukkit.unloadWorld(nether, false);
                    Util.deleteWorldFolder(nether);
                }


                UHC.getMultiverseCore().getMVWorldManager().removeWorldFromConfig(GameManager.get().getWorld().getName());
                Bukkit.unloadWorld(GameManager.get().getWorld(), false);
                Util.deleteWorldFolder(GameManager.get().getWorld());


            }
        }.runTaskLater(UHC.get(), 20 * 10);


        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream().filter(o -> !NSPlayer.get(o.getUniqueId()).hasRank(Rank.TRIAL)).forEach(o -> o.kickPlayer("The game has concluded! Thanks for playing! \n Follow us on twitter @NightShadePvPMC \n Join our Discord @ discord.nightshadepvp.com"));
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
            }
        }.runTaskLater(UHC.get(), 30 * 20);
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.TRIAL;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
