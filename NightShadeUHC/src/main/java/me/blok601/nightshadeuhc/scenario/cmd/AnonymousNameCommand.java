package me.blok601.nightshadeuhc.scenario.cmd;

import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.entity.object.GameState;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.scenario.AnonymousScenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Blok on 8/3/2018.
 */
public class AnonymousNameCommand implements UHCCommand {

    private ScenarioManager scenarioManager;

    public AnonymousNameCommand(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "setanonymousname"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        Player p = (Player) s;
        if(args.length != 1){
            p.sendMessage(ChatUtils.message("&cUsage: /setanonymousname <name>"));
            p.sendMessage(ChatUtils.message("&eThis command will set the disguise name to be assigned when the game starts. If the game has already started, it will give everyone this disguise"));
            return;
        }

        if(!scenarioManager.getScen("Anonymous").isEnabled()){
            p.sendMessage(ChatUtils.message("&cAnonymous isn't enabled!"));
            return;
        }

        String name = args[0];
        if(name.length() > 16){
            p.sendMessage(ChatUtils.message("&cThat name is too long! It must be a valid username!"));
            return;
        }

        AnonymousScenario.setDisuigse(name);
        if(GameState.gameHasStarted()){
            AnonymousScenario anonymousScenario = (AnonymousScenario) scenarioManager.getScen("Anonymous");
            anonymousScenario.assignAll();
        }

        p.sendMessage(ChatUtils.message("&eYou have set the Anonymous name to " + name));



    }

    @Override
    public boolean playerOnly() {
        return false;
    }

    @Override
    public Rank getRequiredRank() {
        return null;
    }

    @Override
    public boolean hasRequiredRank() {
        return false;
    }
}
