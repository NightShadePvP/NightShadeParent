package com.nightshadepvp.core.cmd.cmds.staff;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeInteger;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CmdAmount extends NightShadeCoreCommand {

    private static CmdAmount i = new CmdAmount();
    public static CmdAmount get() {return i;}

    public CmdAmount() {
        this.addAliases("amount");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL), RequirementIsPlayer.get());
        this.addParameter(TypeInteger.get(), "amount");
    }

    @Override
    public void perform() throws MassiveException {
        Player player = NSPlayer.get(sender).getPlayer();
        if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR){
            player.sendMessage(ChatUtils.message("&cYou don't have an item in your hand!"));
            return;
        }
        int newAmount = this.readArg();
        if(newAmount <= 0 || newAmount > 64){
            player.sendMessage(ChatUtils.message("&cValue must be between 0 and 64!"));
            return;
        }

        ItemStack itemStack = player.getItemInHand();
        itemStack.setAmount(newAmount);
        player.sendMessage(ChatUtils.message("&bYou now have &f" + newAmount +" &bof this item!"));
    }
}
