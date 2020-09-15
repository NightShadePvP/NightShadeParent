package me.blok601.nightshadeuhc.scoreboard;

import com.google.common.collect.Maps;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.event.TeamColorEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scoreboard.provider.ArenaProvider;
import me.blok601.nightshadeuhc.scoreboard.provider.LobbyProvider;
import me.blok601.nightshadeuhc.scoreboard.provider.UHCProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler implements Listener {

    private static final long UPDATE_TICK_INTERVAL = 4L;

    private final Map<UUID, PlayerBoard> playerBoards = Maps.newHashMap();
    private final LobbyProvider lobbyProvider;
    private final ArenaProvider arenaProvider;
    private final UHCProvider uhcProvider;
    private final UHC plugin;

    public ScoreboardHandler(UHC plugin, GameManager gameManager, ScenarioManager scenarioManager) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        lobbyProvider = new LobbyProvider(plugin, gameManager, scenarioManager);
        arenaProvider = new ArenaProvider();
        uhcProvider = new UHCProvider();

        // Give all online players a scoreboard.
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            applyBoard(player).addUpdates(players);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Update this player for every other online player.
        for (PlayerBoard board : playerBoards.values()) {
            board.addUpdate(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerBoards.remove(event.getPlayer().getUniqueId()).remove();
    }

    @EventHandler
    public void onColor(TeamColorEvent event){
        for (PlayerBoard board : playerBoards.values()) {
            board.addUpdates(Bukkit.getOnlinePlayers());
        }
    }

    public PlayerBoard getPlayerBoard(UUID uuid) {
        return playerBoards.get(uuid);
    }

    public PlayerBoard applyBoard(Player player) {
        PlayerBoard board = new PlayerBoard(plugin, player);
        PlayerBoard previous = playerBoards.put(player.getUniqueId(), board);
        if (previous != null) {
            previous.remove();
        }

        board.setSidebarVisible(true);

        switch (UHCPlayer.get(player).getPlayerStatus()){
            case ARENA:
                board.setDefaultSidebar(arenaProvider, 40L);
                break;
            case LOBBY:
                board.setDefaultSidebar(lobbyProvider, 30L);
                break;
            case SPECTATING:
                if(GameState.gameHasStarted()){
                    board.setDefaultSidebar(uhcProvider, 20L);
                }else{
                    board.setDefaultSidebar(lobbyProvider, 30L);
                }
                break;
            case DEAD:
            case PLAYING:
            default:
                board.setDefaultSidebar(uhcProvider, 20L);
        }
        return board;
    }

    public void clearBoards() {
        Iterator<PlayerBoard> iterator = playerBoards.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().remove();
            iterator.remove();
        }
    }

    public Map<UUID, PlayerBoard> getPlayerBoards() {
        return playerBoards;
    }
}
