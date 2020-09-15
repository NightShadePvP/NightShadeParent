package me.blok601.nightshadeuhc.task;

import com.massivecraft.massivecore.nms.NmsChat;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.FreezeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

/**
 * Created by Blok on 12/22/2018.
 */
public class GameCountdownTask extends BukkitRunnable {

    private int counter = 180;
    private GameManager gameManager;
    private ScenarioManager scenarioManager;

    public GameCountdownTask(GameManager gameManager, ScenarioManager scenarioManager) {
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
    }

    public int getCounter() {
        return counter;
    }

    @Override
    public void run() {

        if (counter <= -1) return;

        if (counter % 60 == 0) {
            Bukkit.broadcastMessage(ChatUtils.message("&eScatter will begin in &b" + (counter / 60) + " &eminute&8(&es&8)..."));
        }

        if (counter == 60) {
            UHCPlayerColl.get().getAllOnline().forEach(UHCPlayer::leaveArena);
        }

        if (counter <= 0) {

            ArrayList<Player> validPlayers = new ArrayList<>();

            for (UHCPlayer uhcPlayer : UHCPlayerColl.get().getAllOnline()) {

                if (uhcPlayer.isInArena()) {
                    uhcPlayer.leaveArena();
                }

                NmsChat.get().sendActionbarMessage(uhcPlayer, ChatColor.translateAlternateColorCodes('&', "&5The scatter is beginning...."));

                uhcPlayer.getPlayer().getInventory().clear();
                uhcPlayer.getPlayer().getInventory().setArmorContents(null);

                if (uhcPlayer.isSpectator()) continue;

                uhcPlayer.setPlayerStatus(PlayerStatus.PLAYING);
                validPlayers.add(uhcPlayer.getPlayer());

            }
//            UHC.getScoreboardManager().updateCache();
            Bukkit.getOnlinePlayers().forEach(player -> UHC.getScoreboardManager().applyBoard(player));

            gameManager.getWorld().setTime(20);

            Bukkit.broadcastMessage(ChatUtils.message("&eUse /helpop or message any online staff members if you need help!"));

            ChatUtils.setChatFrozen(true);
            FreezeUtil.start();
            GameState.setState(GameState.STARTING);

            new ScatterTask(validPlayers, gameManager.getWorld(), gameManager.getSetupRadius(), gameManager.getHost(), gameManager.getFinalHealTime(), gameManager.getPvpTime(), gameManager.getBorderTime(), gameManager.isIsTeam(), gameManager.getFirstShrink(), gameManager.getMeetupTime(), gameManager, scenarioManager).runTaskTimer(UHC.get(), 0, 4);
            counter = -1;
            return;
        }

        counter--;

        Bukkit.getOnlinePlayers().forEach(player -> NmsChat.get().sendActionbarMessage(player, "§5Scatter§8» " + formatTime(counter)));

    }

    private String formatTime(int i) {
        int m = i / 60;
        int s = i % 60;

        return "§3" + m + "§5m§3" + s + "§5s";
    }

}
