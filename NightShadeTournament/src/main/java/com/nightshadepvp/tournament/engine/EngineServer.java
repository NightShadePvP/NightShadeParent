package com.nightshadepvp.tournament.engine;

import com.massivecraft.massivecore.Engine;
import com.nightshadepvp.tournament.Tournament;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.TPlayerColl;
import com.nightshadepvp.tournament.entity.handler.GameHandler;
import com.nightshadepvp.tournament.entity.handler.RoundHandler;
import com.nightshadepvp.tournament.entity.objects.data.CachedGame;
import com.nightshadepvp.tournament.entity.objects.game.iMatch;
import com.nightshadepvp.tournament.event.MatchStartEvent;
import com.nightshadepvp.tournament.event.TournamentEndEvent;
import com.nightshadepvp.tournament.event.TournamentStartEvent;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class EngineServer extends Engine {

    private static EngineServer i = new EngineServer();
    public static EngineServer get() {return i;}

    @EventHandler
    public void onTournamentStart(TournamentStartEvent e){
        CachedGame cachedGame = Tournament.get().getCachedGame();
        cachedGame.setStart(System.currentTimeMillis());
        cachedGame.setHost(GameHandler.getInstance().getHost().getName());
        cachedGame.setBracketLink(GameHandler.getInstance().getBracketLink());
        cachedGame.setFill(Bukkit.getOnlinePlayers().size() - TPlayerColl.get().getSpectators().size());
        cachedGame.setServer("Tourney1");
        cachedGame.setTeamType("Solo"); //TODO: CHANGE
        cachedGame.setTourneyID(Tournament.get().getTourneyCollection().count() + 1);
        cachedGame.setKit(GameHandler.getInstance().getKit().getName());

    }

    @EventHandler
    public void onTournamentEnd(TournamentEndEvent e){
        Tournament tournament = Tournament.get();
        CachedGame cachedGame = Tournament.get().getCachedGame();
        cachedGame.setEnd(System.currentTimeMillis());
        cachedGame.setWinners(e.getChampionshipMatch().getWinners().stream().map(TPlayer::getName).collect(Collectors.toList()));
        cachedGame.setRounds(RoundHandler.getInstance().getRound());

        Bukkit.getServer().getScheduler().runTaskAsynchronously(tournament, () -> {
            Document document = new Document("matchID", cachedGame.getTourneyID());
            document.append("host", cachedGame.getHost());
            document.append("winners", cachedGame.getWinners());
            document.append("players", cachedGame.getFill());
            document.append("kit", cachedGame.getKit());
            document.append("teamType", cachedGame.getTeamType());
            document.append("startTime", cachedGame.getStart());
            document.append("endTime", cachedGame.getEnd());
            document.append("server", cachedGame.getServer());
            document.append("bracket", cachedGame.getBracketLink());
            document.append("rounds", cachedGame.getRounds());
            tournament.getTourneyCollection().insertOne(document);
        });
    }

    @EventHandler
    public void onStart(MatchStartEvent event){
        iMatch match = event.getMatch();
        Player player;
        for (TPlayer tPlayer : match.getPlayers()){
            if(!tPlayer.isOnline()) continue;

            player = tPlayer.getPlayer();
            for (ItemStack itemStack : player.getInventory().getContents()){
                if(itemStack != null && itemStack.getAmount() == 0){
                    itemStack.setAmount(1);
                }
            }
        }
    }
}
