package com.nightshadepvp.core.cmd.cmds.staff;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.TypeItemStack;
import com.massivecraft.massivecore.command.type.enumeration.TypeMaterial;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.massivecraft.massivecore.command.type.sender.TypePlayer;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.core.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class CmdGive extends NightShadeCoreCommand {

    private static CmdGive i = new CmdGive();
    public static CmdGive get() {return i;}

    public CmdGive() {
        this.addAliases("give");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL));
        this.addParameter(TypePlayer.get(), "player");
        this.addParameter(TypeMaterial.get(), "item");
        this.addParameter(TypeInteger.get(), "amount");
    }

    @Override
    public void perform() throws MassiveException {
        NSPlayer nsPlayer = NSPlayer.get(sender);
        Player target = this.readArg();
        if(target == null || !target.isOnline()){
            nsPlayer.msg(ChatUtils.message("&cThat player couldn't be found!"));
            return;
        }

        Material item = this.readArg();
        int amount = this.readArg();

        ItemStack itemStack = new ItemStack(item, amount);
        PlayerUtils.giveItem(itemStack, target);
        nsPlayer.msg(ChatUtils.message("&bGave &f" + amount + " &bof &f" + item.name() + " &bto &f" + target.getName()));
    }

}
