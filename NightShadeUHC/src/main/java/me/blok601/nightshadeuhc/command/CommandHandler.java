package me.blok601.nightshadeuhc.command;

import com.google.common.collect.Sets;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Logger;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.game.run.*;
import me.blok601.nightshadeuhc.command.game.setup.*;
import me.blok601.nightshadeuhc.command.player.*;
import me.blok601.nightshadeuhc.command.player.disguise.RandomDisguiseCommand;
import me.blok601.nightshadeuhc.command.player.teams.*;
import me.blok601.nightshadeuhc.command.server.eWhitelistCommand;
import me.blok601.nightshadeuhc.command.staff.*;
import me.blok601.nightshadeuhc.command.staff.admin.ResetStatsCommand;
import me.blok601.nightshadeuhc.command.staff.admin.SetArenaSpawnCommand;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.component.SplitEnchantsComponent;
import me.blok601.nightshadeuhc.gui.leaderboards.OreLeaderboard;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scenario.SlaveMarketScenario;
import me.blok601.nightshadeuhc.scenario.cmd.*;
import me.blok601.nightshadeuhc.scenario.cmd.btc.BTCAddCommand;
import me.blok601.nightshadeuhc.scenario.cmd.btc.BTCListCommand;
import me.blok601.nightshadeuhc.scenario.cmd.slavemarket.BidCommand;
import me.blok601.nightshadeuhc.scenario.cmd.slavemarket.SlaveOwnerCommand;
import me.blok601.nightshadeuhc.scenario.cmd.slavemarket.StartBiddingCommand;
import me.blok601.nightshadeuhc.scenario.cmd.mole.*;
import me.blok601.nightshadeuhc.scenario.cmd.superhero.*;
import me.blok601.nightshadeuhc.stat.command.LeaderboardsCommand;
import me.blok601.nightshadeuhc.stat.command.StatsCommand;

import java.util.HashSet;

/**
 * Created by Blok on 6/26/2017.
 */
public class CommandHandler  {


    private Core core;
    private UHC uhc;
    private GameManager gameManager;
    private ScenarioManager scenarioManager;
    private ComponentHandler componentHandler;

    private HashSet<UHCCommand> commands;

    public CommandHandler(UHC uhc, GameManager gameManager, ScenarioManager scenarioManager, ComponentHandler componentHandler) {
        this.gameManager = gameManager;
        this.scenarioManager = scenarioManager;
        this.componentHandler = componentHandler;
        this.commands = Sets.newHashSet();
        this.core = Core.get();
        registerCommand(new FeedAll());
        registerCommand(new FreezeAll());
        registerCommand(new HealthCommand());
        registerCommand(new HealCommand());
        registerCommand(new HelpopCommand());
        registerCommand(new PvPCommand());
        registerCommand(new WhitelistCommand());
        registerCommand(new LootCrateTimeCommand(scenarioManager));
        registerCommand(new BackpackCommand(scenarioManager));
        registerCommand(new CancelGameCommand(gameManager));
        registerCommand(new RandomDisguiseCommand());
        registerCommand(new EndGameCommand(scenarioManager, core.getQuestHandler()));
        registerCommand(new SpectatorChatCommand());
        registerCommand(new AlertsCommand());
        registerCommand(new CommandSpyCmd());
        registerCommand(new SetTickCommand());
        registerCommand(new TeleportAllCommand());
        registerCommand(new ChatStopCommand());
        registerCommand(new ManualGameCommand(gameManager));
        registerCommand(new ClaimHostCommand());
        registerCommand(new SitCommand());
        registerCommand(new ScenarioManager(uhc, gameManager, componentHandler));
        registerCommand(new TeamsCommand(scenarioManager, gameManager));
        registerCommand(new TeamBaseCommand(scenarioManager, componentHandler));
        registerCommand(new MaxplayersCommand());
        registerCommand(new TimerCommand());
        registerCommand(new ConfigCommand(gameManager, componentHandler));
        registerCommand(new LoadPresetCommand());
        registerCommand(new eWhitelistCommand());
        registerCommand(new RespawnCommand());
        registerCommand(new LateStartCommand());
        registerCommand(new KillTopCommand());
        registerCommand(new DeathBanCommand());
        registerCommand(new ExplainCommand(scenarioManager));
        registerCommand(new RatesCommand(gameManager, scenarioManager));
        registerCommand(new FeedCommand());
        registerCommand(new ToggleHelpopCommand());
        registerCommand(new ScenariosCommand(scenarioManager));
        registerCommand(new ResetHealthCommand());
        registerCommand(new ClearArmorCommand());
        registerCommand(new CreateWorldCommand());
        registerCommand(new TpWorldCommand());
        registerCommand(new WorldsCommand());
        registerCommand(new DeleteWorldCommand());
        registerCommand(new FullBrightCommand());
        registerCommand(new RvBCommand());
        registerCommand(new LeaderboardsCommand());
        registerCommand(new StatsCommand());
        registerCommand(new MoleChatCommand(scenarioManager));
        registerCommand(new MoleKitCommand(scenarioManager));
        registerCommand(new MoleLocationCommand(scenarioManager));
        registerCommand(new MolesCommand());
        registerCommand(new StaffCommand());
        registerCommand(new SpectatorCommand());
        registerCommand(new SetSpawnCommand());
        registerCommand(new SetArenaSpawnCommand());
        registerCommand(new ArenaCommand(gameManager));
        registerCommand(new DonateHealthCommand(scenarioManager));
        registerCommand(new CancelPregenCommand());
        registerCommand(new PowerCommand(scenarioManager));
        registerCommand(new AssignPowerCommand(scenarioManager));
        registerCommand(new AnonymousNameCommand(scenarioManager));
        registerCommand(new AssaultAndBatteryRoleCommand(scenarioManager));
        registerCommand(new GiveItemsCommand(scenarioManager));
        registerCommand(new KickSolosCommand());
        registerCommand(new KickUnderCommand());
        registerCommand(new SpectatorsCommand());
        registerCommand(new PermaDayCommand());
        registerCommand(new AliveCommand());
        registerCommand(new ClearXPCommand());
        registerCommand(new TeamInventoryCommand(scenarioManager));
        registerCommand(new SpectatingTimeCommand());
        registerCommand(new TopSpecCommand());
        registerCommand(new HallOfFameCommand());
        registerCommand(new HelpopMuteCommand());
        registerCommand(new MoleCommand());
        registerCommand(new SetMoleCommand());
        registerCommand(new ToggleMobsCommand());
        registerCommand(new ClearTreesCommand());
        registerCommand(new PMOresCommand());
        registerCommand(new ResetPowersCommand());
        registerCommand(new BorderCommand(gameManager));
        registerCommand(new PowersCommand(scenarioManager));

        SlaveMarketScenario scenario = (SlaveMarketScenario) scenarioManager.getScen("Slave Market");
        if(scenario == null){
            core.getLogManager().log(Logger.LogType.SEVERE, "There was an error trying to load Slave Market! Slave Market Commands won't work!");
        }else{
            registerCommand(new StartBiddingCommand(scenario, uhc));
            registerCommand(new SlaveOwnerCommand(scenario));
            registerCommand(new BidCommand(scenario));
        }

        registerCommand(new HostCommand(gameManager, scenarioManager));
        registerCommand(new ConfirmCommand());
        registerCommand(new SetBorderCommand(gameManager));
        registerCommand(new DoubleDatesCommand(gameManager, scenarioManager));
        registerCommand(new CheckTeamCommand(scenarioManager));
        registerCommand(new TeamChatCommand());
        registerCommand(new SendCoordsCommand());
        registerCommand(new NearCommand(uhc));
        registerCommand(new SetWorldCommand(gameManager)); //idk this wasnt there before
        registerCommand(new ResetStatsCommand(uhc));
        registerCommand(new ClearPowerCommand(scenarioManager));
        registerCommand(new ShowCommand(uhc));
        registerCommand(new BTCListCommand(scenarioManager));
        registerCommand(new BTCAddCommand(scenarioManager));
        registerCommand(new XrayCommand());
        registerCommand(new SplitEnchantsComponent());
        registerCommand(new OresCommand());
    }


    public HashSet<UHCCommand> getCommands() {
        return commands;
    }

    private void registerCommand(UHCCommand uhcCommand) {
        if (this.commands.contains(uhcCommand)) return;
        this.commands.add(uhcCommand);
    }

//    public void checkCommands() {
//        if (this.commands == null || this.commands.isEmpty()) {
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    checkCommands();
//                    return;
//                }
//            }.runTaskLater(uhc, 100);
//        }
//
//        HashSet<String> unregistered = Sets.newHashSet();
//        PluginDescriptionFile pdf = uhc.getDescription();
//        for (String cmdName : pdf.getCommands().keySet()) {
//            if (getCommandByName(cmdName) == null) {
//                //unregistered.add(getCommandByName(cmdName));
//            }
//        }
//
//        if (!unregistered.isEmpty()) {
//            core.getLogManager().log(Logger.LogType.WARNING, "&cThere are unregistered commands: " + Joiner.on("&7, &c").join(unregistered));
//        }
//
//    }

//    private UHCCommand getCommandByName(String test) {
//        return this.commands.stream().filter(uhcCommand -> uhc.getName().equalsIgnoreCase(test)).findFirst().orElse(null);
//    }

}
