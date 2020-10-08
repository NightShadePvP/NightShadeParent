package me.blok601.nightshadeuhc.scenario.cmd.captains;

import com.google.common.collect.Lists;
import com.massivecraft.massivecore.nms.NmsChat;
import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.CaptainsScenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.MathUtils;
import com.nightshadepvp.core.utils.PlayerUtils;
import me.blok601.nightshadeuhc.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class CaptainCommand implements UHCCommand {

    private UHC plugin;
    private ScenarioManager scenarioManager;
    private CaptainsScenario captainsScenario;
    private HashMap<UUID, Team> captains;
    private GameManager gameManager;

    public CaptainCommand(UHC plugin, ScenarioManager scenarioManager, GameManager gameManager) {
        this.plugin = plugin;
        this.scenarioManager = scenarioManager;
        this.gameManager = gameManager;
        this.captainsScenario = (CaptainsScenario) scenarioManager.getScen("Captains");
        captains = new HashMap<>();
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "captain"
        };
    }

    public int captainAmount = -1;

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if (args.length == 0) {
            p.sendMessage(ChatUtils.format("&f&m--------------------------------------"));
            p.sendMessage(ChatUtils.format("&b- /captains amount <int>"));
            p.sendMessage(ChatUtils.format("&b- /captains assign"));
            p.sendMessage(ChatUtils.format("&b- /captains add <name>"));
            p.sendMessage(ChatUtils.format("&b- /captains remove <name>"));
            p.sendMessage(ChatUtils.format("8b- /captains start"));
            p.sendMessage(ChatUtils.format("&b- /captains reset"));
            p.sendMessage(ChatUtils.format("&f&m--------------------------------------"));
        }

        if (args.length == 1) {
            if (!captainsScenario.isEnabled()) {
                p.sendMessage(ChatUtils.message("&cYou can only access Captains commands when the scenario is enabled!"));
                return;
            }
            if (args[0].equalsIgnoreCase("assign")) {
                captainsScenario.sendMessage(p, "&eAssigning captains in 5 seconds...make sure all staff and spectators are in spectator mode!");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Random random = new Random();
                        ArrayList<UHCPlayer> potentialCaptains = UHCPlayerColl.get().getAllOnlinePlayers().stream().filter(UHCPlayer::isSpectator).collect(Collectors.toCollection(ArrayList::new));

                        UHCPlayer captain;
                        for (int i = 0; i < captainAmount; i++) {
                            captain = potentialCaptains.get(random.nextInt(potentialCaptains.size()));
                            int ts = TeamManager.getInstance().getTeams().size();
                            Team team = new Team("UHC" + ts + captain.getName().substring(0, 3), captain.getPlayer(), ChatUtils.generateTeamColor());
                            captains.put(captain.getUuid(), team);
                            captainsScenario.sendMessage(p, "&bYou have set &f" + captain.getName() + " &bas a captain");
                            captainsScenario.broadcast("&f" + captain.getName() + " &bis now a captain!");
                            NmsChat.get().sendTitleMessage(captain,
                                    10,
                                    10,
                                    5,
                                    ChatColor.AQUA + "" + ChatColor.BOLD + "You are a captain!",
                                    ChatColor.WHITE + "Prepare to pick your team...");
                        }

                        captainsScenario.sendMessage(p, "&bCaptains have been assigned!");
                        return;
                    }
                }.runTaskLater(plugin, 100);
                return;
            } else if (args[0].equalsIgnoreCase("reset")) {
                captainsScenario.sendMessage(p, "&eCleaning all captains and removing their teams...");
                this.captains.values().forEach(team -> TeamManager.getInstance().removeTeam(team));
                this.captains.clear();
                captainsScenario.sendMessage(p, "&eCaptains and their teams have been cleared!");
            } else if (args[0].equalsIgnoreCase("start")) {
                ArrayList<UUID> captainList = Lists.newArrayList();
                captainList.addAll(this.captains.keySet());
                Collections.shuffle(captainList);

                ArrayList<UUID> players = Lists.newArrayList();
                UHCPlayerColl.get().getAllOnlinePlayers().stream().filter(uhcPlayer -> !uhcPlayer.isSpectator()).forEach(uhcPlayer -> players.add(uhcPlayer.getUuid()));
                Collections.shuffle(players);

                UHCPlayerColl.get().getAllOnlinePlayers().forEach(UHCPlayer::leaveArena);
                gameManager.setArenaOpen(false);
                Util.staffLog("Arena has been disabled!");
                ChatUtils.setChatFrozen(true);
                captainsScenario.setPositionIndex(0);
                captainsScenario.setTurnToPick(captainList.get(captainsScenario.getPositionIndex()));
                captainsScenario.sendMessage(p, "&aStarting picking process...");
                captainsScenario.broadcast("&bTeam Selection beginning now...");
                new BukkitRunnable() {
                    int counter = 10;

                    @Override
                    public void run() {
                        Player captain = Bukkit.getPlayer(captainsScenario.getTurnToPick());
                        if (counter == 0) {
                            Random random = new Random();
                            if (!captainsScenario.hasPicked()) {
                                if (captain != null) {
                                    captainsScenario.broadcast("&f" + captain.getName() + " &bdid not pick in time, he will be randomly assigned a player!");
                                    UUID targetUUID = players.get(random.nextInt(players.size()));
                                    Player targetPlayer = Bukkit.getPlayer(targetUUID);
                                    Team team = captains.get(captain.getUniqueId());
                                    if (targetPlayer != null) {
                                        //online
                                        team.addMember(targetPlayer.getName());
                                        captainsScenario.broadcast("&f" + targetPlayer.getName() + " &bhas been added to &f" + captain.getName() + "'s &bteam!");
                                        captainsScenario.setPositionIndex(captainsScenario.getPositionIndex() + 1);
                                        captainsScenario.setTurnToPick(captain.getUniqueId());
                                        return;
                                    } else {
                                        //They're offline
                                        OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(targetUUID);
                                        team.addMember(targetOfflinePlayer.getName());
                                        captainsScenario.broadcast("&f" + targetOfflinePlayer.getName() + " &bhas been added to &f" + captain.getName() + "'s &bteam!");
                                        captainsScenario.setPositionIndex(captainsScenario.getPositionIndex() + 1);
                                        captainsScenario.setTurnToPick(captain.getUniqueId());
                                        return;
                                    }
                                } else {
                                    OfflinePlayer offlineCaptain = Bukkit.getOfflinePlayer(captainsScenario.getTurnToPick());
                                    captainsScenario.broadcast("&f" + offlineCaptain.getName() + " &bdid not pick in time, he will be randomly assigned a player!");
                                    UUID targetUUID = players.get(random.nextInt(players.size()));
                                    Player targetPlayer = Bukkit.getPlayer(targetUUID);
                                    Team team = captains.get(offlineCaptain.getUniqueId());
                                    if (targetPlayer != null) {
                                        //online
                                        team.addMember(targetPlayer.getName());
                                        captainsScenario.broadcast("&f" + targetPlayer.getName() + " &bhas been added to &f" + offlineCaptain.getName() + "'s &bteam!");
                                        captainsScenario.setPositionIndex(captainsScenario.getPositionIndex() + 1);
                                        captainsScenario.setTurnToPick(offlineCaptain.getUniqueId());
                                        return;
                                    } else {
                                        //They're offline
                                        OfflinePlayer targetOfflinePlayer = Bukkit.getOfflinePlayer(targetUUID);
                                        team.addMember(targetOfflinePlayer.getName());
                                        captainsScenario.broadcast("&f" + targetOfflinePlayer.getName() + " &bhas been added to &f" + offlineCaptain.getName() + "'s &bteam!");
                                        captainsScenario.setPositionIndex(captainsScenario.getPositionIndex() + 1);
                                        captainsScenario.setTurnToPick(offlineCaptain.getUniqueId());
                                        return;
                                    }
                                }
                            }

                            if(captainsScenario.getPositionIndex() == captainAmount-1){
                                if(captainsScenario.isGoingBackwards()){
                                    captainsScenario.setGoingBackwards(false);
                                }
                            }
                        }
                        if (captain != null) {
                            if (counter != 10) {
                                return;
                            }
                            PlayerUtils.playSound(Sound.ANVIL_USE, captain);
                            captainsScenario.broadcast("&f" + captain.getName() + " &bnow has 10 seconds to pick a player!");
                            captainsScenario.sendMessage(captain, "&ePick a player for your team by doing /pick [player]");
                            captainsScenario.sendMessage(captain, "&eIf no player is supplied, a gui will open.");
                        } else {
                            if (counter != 10) return;
                            OfflinePlayer offlineCaptain = Bukkit.getPlayer(captainsScenario.getTurnToPick());
                            captainsScenario.broadcast("&f" + offlineCaptain.getName() + " &bhas 10 seconds to login and pick a player!");
                        }

                        counter--;
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }

        if (args.length == 2) {
            if (!captainsScenario.isEnabled()) {
                p.sendMessage(ChatUtils.message("&cYou can only access Captains commands when the scenario is enabled!"));
                return;
            }
            if (args[0].equalsIgnoreCase("amount")) {
                if (!MathUtils.isInt(args[1])) {
                    captainsScenario.sendMessage(p, "&cUsage: /captains amount <int>");
                    return;
                }

                this.captainAmount = Integer.parseInt(args[1]);
                captainsScenario.sendMessage(p, "&eCaptain amount set to " + captainAmount);
                return;
            } else if (args[0].equalsIgnoreCase("add")) {
                if (this.captains.size() == this.captainAmount) {
                    captainsScenario.sendMessage(p, "&cThere are already &4" + this.captainAmount + "&ccaptains!");
                    return;
                }

                String name = args[1];
                Player onlineCaptain = Bukkit.getPlayer(name);
                if (onlineCaptain == null) {
                    captainsScenario.sendMessage(p, "&cOnly online players can be captains!");
                    return;
                } else {
                    int ts = TeamManager.getInstance().getTeams().size();
                    Team team = new Team("UHC" + ts + onlineCaptain.getName().substring(0, 3), onlineCaptain, ChatUtils.generateTeamColor());
                    this.captains.put(onlineCaptain.getUniqueId(), team);
                    captainsScenario.sendMessage(p, "&bYou have set &f" + onlineCaptain.getName() + " &bas a captain");
                    captainsScenario.broadcast("&f" + onlineCaptain.getName() + " &bis now a captain!");
                    return;
                }
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (this.captains.size() == 0) {
                    captainsScenario.sendMessage(p, "&cThere are no captains to remove!");
                    return;
                }

                String name = args[0];
                Player targetCaptain = Bukkit.getPlayer(name);
                if (targetCaptain != null) {
                    if (this.captains.containsKey(targetCaptain.getUniqueId())) {
                        TeamManager.getInstance().removeTeam(this.captains.remove(targetCaptain.getUniqueId()));
                        captainsScenario.broadcast("&f" + targetCaptain.getName() + " &bis no longer a captain");
                        return;
                    }

                    captainsScenario.sendMessage(p, "&cThat player is not a captain!");
                    return;
                } else {
                    OfflinePlayer offlineCaptain = Bukkit.getOfflinePlayer(name);
                    if (offlineCaptain == null) {
                        captainsScenario.sendMessage(p, "&cThe player &4" + name + " &ccouldn't be found!");
                        return;
                    }

                    if (this.captains.containsKey(offlineCaptain.getUniqueId())) {
                        TeamManager.getInstance().removeTeam(this.captains.remove(offlineCaptain.getUniqueId()));
                        captainsScenario.broadcast("&f" + offlineCaptain.getName() + " &bis no longer a captain");
                        return;
                    }

                    captainsScenario.sendMessage(p, "&cThat player is not a captain!");
                    return;
                }

            }
        }


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
        return false;
    }

}
