package com.nightshadepvp.tournament.task;

import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.utils.PacketUtils;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.TPlayerColl;
import com.nightshadepvp.tournament.entity.handler.ArenaHandler;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.entity.handler.MatchHandler;
import com.nightshadepvp.tournament.entity.handler.RoundHandler;
import com.nightshadepvp.tournament.entity.objects.data.Arena;
import com.nightshadepvp.tournament.entity.objects.game.SoloMatch;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Blok on 7/18/2018.
 */
public class StartRoundTask extends BukkitRunnable {

    private int counter;
    private int round;
    private RoundHandler roundHandler;

    public StartRoundTask() {
        counter = 25; //30 second total countdown
        this.roundHandler = RoundHandler.getInstance();
        this.round = roundHandler.getRound(); //Round always from same place
    }

    @Override
    public void run() {
        if(counter == 0){
            roundHandler.getMatchesByRoundNumber(round).forEach(iMatch::start);
            cancel();
            new RoundCheckTask().runTaskTimer(Tournament.get(), 0, 40);
            return;
        }else if(counter == 5){
            ChatUtils.broadcast("&bRound &f" + round + " &bwill start in &f10 &bseconds...");
            //Teleport now
            Arena arena;
            Core.get().getLogManager().log(Logger.LogType.DEBUG, "Number of Matches Per round: " + roundHandler.getMatchesByRoundNumber(round).size());
            for (TPlayer tPlayer : TPlayerColl.get().getAllOnline()){

                if(!tPlayer.isPlayer()) continue;
                if(tPlayer.isSpectator() || tPlayer.getSeed() == -1){
                    continue; //They're spectating or dead
                }

                if(MatchHandler.getInstance().getActiveMatch(tPlayer) != null){
                    continue; //They have a match set
                }

                PacketUtils.sendTitle(tPlayer.getPlayer(), 20, 40, 20, ChatUtils.format("&bBye Round"), ChatUtils.format("&fYou have a bye this round"));
            }
            for (iMatch match : roundHandler.getMatchesByRoundNumber(round)) {
                if (match instanceof SoloMatch) {
                    SoloMatch soloMatch = (SoloMatch) match;
                    if(ArenaHandler.getInstance().getAvailableArenas().size() == 0){
                        Bukkit.broadcastMessage("&4Ran out of arena's! SoloMatch ID: &a" + soloMatch.getMatchID() + "&4. Players: &a" + soloMatch.getPlayer1().getName() + " and " + soloMatch.getPlayer2().getName() + " &4Skipping!");
                        continue;
                    }
                    arena = ArenaHandler.getInstance().getAvailableArenas().get(0);
                    soloMatch.setArena(arena);
                    arena.setInUse(true);
                    TPlayer player1 = soloMatch.getPlayer1();
                    TPlayer player2 = soloMatch.getPlayer2();
                    if (player1.isOnline()) {
                        
                        if(player1.isSpectator()){
                            player1.setSpectator(false);
                            for (Player pl : Bukkit.getOnlinePlayers()) {
                                pl.showPlayer(player1.getPlayer());
                            }

                            player1.getPlayer().getInventory().clear();
                            player1.getPlayer().getInventory().setArmorContents(null);
                            player1.getPlayer().setGameMode(GameMode.SURVIVAL);
                            player1.getPlayer().setFlying(false);
                            player1.getPlayer().setCanPickupItems(true);
                            MatchHandler.getInstance().getActiveMatches().stream().filter(iMatch -> iMatch.getSpectators().contains(player1)).forEach(iMatch -> iMatch.getSpectators().remove(player1));
                        }
                        
                        player1.getPlayer().teleport(arena.getSpawnLocation1());
                        player1.getPlayer().setHealth(player1.getPlayer().getMaxHealth());
                        player1.getPlayer().setFoodLevel(20);
                    } else {
                        soloMatch.startLogOutTimer(player1);
                        GameHandler.getInstance().getTeleportQueue().put(player1.getUuid(), soloMatch.getArena().getSpawnLocation1());
                    }

                    if (player2.isOnline()) {
                        player2.getPlayer().teleport(arena.getSpawnLocation2());
                        player2.getPlayer().setHealth(player2.getPlayer().getMaxHealth());
                        player2.getPlayer().setFoodLevel(20);
                    } else {
                        soloMatch.startLogOutTimer(player2);
                        GameHandler.getInstance().getTeleportQueue().put(player2.getUuid(), soloMatch.getArena().getSpawnLocation2());
                    }
                }
            }
        }else if(counter == 25){
            ChatUtils.broadcast("&bRound &f" + round + " &bwill start in &f30 &bseconds...");
        }

        counter--;
    }
}
