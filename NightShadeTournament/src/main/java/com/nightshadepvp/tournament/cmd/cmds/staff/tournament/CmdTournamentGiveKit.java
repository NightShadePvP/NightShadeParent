package com.nightshadepvp.tournament.cmd.cmds.staff.tournament;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.cmd.type.TypeNSPlayer;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.tournament.entity.TPlayer;
import com.nightshadepvp.tournament.entity.handler.KitHandler;
import com.nightshadepvp.tournament.entity.objects.data.Kit;
import com.nightshadepvp.tournament.entity.objects.player.PlayerInv;
import com.nightshadepvp.tournament.utils.ChatUtils;
import org.bukkit.entity.Player;

public class CmdTournamentGiveKit extends NightShadeCoreCommand {

    public CmdTournamentGiveKit() {
        this.addAliases("givekit");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
        this.addParameter(TypeString.get(), "kit");
        this.addParameter(TypeNSPlayer.get(), "player");
    }

    @Override
    public void perform() throws MassiveException {
        TPlayer tPlayer = TPlayer.get(sender);
        String kit = this.readArg();
        if(!KitHandler.getInstance().isKit(kit)){
            tPlayer.msg(ChatUtils.message("&cThat kit does not exist!"));
            return;
        }

        Kit k = KitHandler.getInstance().getKit(kit);
        NSPlayer nsPlayer = this.readArg();
        if(nsPlayer == null || !nsPlayer.isOnline()){
            tPlayer.msg(ChatUtils.message("&cThat player is not online or does not exist!"));
            return;
        }

        TPlayer targetTPlayer = TPlayer.get(nsPlayer.getUuid());
        Player target = targetTPlayer.getPlayer();
        PlayerInv inv = targetTPlayer.getInv(k);
        if (inv == null) {
            inv = new PlayerInv(k.getItems(), k.getArmor());
            tPlayer.getPlayerKits().put(k, inv);
        }
        target.getInventory().setContents(inv.getContents());
        target.getInventory().setArmorContents(inv.getArmorContents());
        tPlayer.msg(ChatUtils.message("&bGiven &f" + target.getName() + " &btheir &f" + k.getName() + " &bkit!"));
        target.sendMessage(ChatUtils.message("&bYou were given your items!"));
    }
}
