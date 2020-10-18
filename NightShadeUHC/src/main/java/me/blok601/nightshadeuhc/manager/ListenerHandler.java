package me.blok601.nightshadeuhc.manager;

import com.google.common.collect.Sets;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import com.nightshadepvp.core.quest.QuestHandler;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.player.disguise.Undisguise;
import me.blok601.nightshadeuhc.command.staff.RatesCommand;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.component.GoldenHeadConsume;
import me.blok601.nightshadeuhc.listener.game.*;
import me.blok601.nightshadeuhc.listener.gui.GameSetupInventoryClick;
import me.blok601.nightshadeuhc.listener.gui.InvClick;
import me.blok601.nightshadeuhc.listener.gui.ScenarioClick;
import me.blok601.nightshadeuhc.listener.misc.SpectatorListener;
import me.blok601.nightshadeuhc.listener.misc.StaffListener;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.cmd.DonateHealthCommand;
import me.blok601.nightshadeuhc.stat.listener.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.HashSet;

/**
 * Created by Blok on 8/5/2017.
 */
public class ListenerHandler {

    private UHC uhc;
    private HashSet<Listener> listeners;
    private Core core;

    public ListenerHandler(UHC uhc, Core core, ScenarioManager scenarioManager, GameManager gameManager, ComponentHandler componentHandler) {
        this.uhc = uhc;
        this.core = core;
        listeners = Sets.newHashSet();

        addListener(new GameDeathListener());
        addListener(new GoldenHeadConsume());
        addListener(new PlayerListener(gameManager, scenarioManager, core.getQuestHandler()));
        addListener(new ArenaListener());
        addListener(new RatesCommand(gameManager, scenarioManager));
        addListener(new Undisguise());
        addListener(new JoinListener(gameManager, scenarioManager, uhc));

        addListener(new InvClick(scenarioManager, uhc));
        addListener(new GameSetupInventoryClick(gameManager, uhc, scenarioManager, componentHandler));
        addListener(new ScenarioClick(scenarioManager));

        addListener(new SpectatorListener());
        //addListener(new SpectatorInfoListener(scenarioManager));
        addListener(new StaffListener());

        addListener(new GameListener(uhc, gameManager, scenarioManager, componentHandler));
        addListener(new WorldBorderListener(gameManager));
        addListener(new DonateHealthCommand(scenarioManager));
    }


    private void addListener(Listener listener){
        this.listeners.add(listener);
    }

    public void complete(){
        this.listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, uhc));
        core.getLogManager().log(Logger.LogType.INFO, "All listeners have been registered in: UHC");

    }

    public HashSet<Listener> getListeners() {
        return listeners;
    }
}
