package com.nightshadepvp.core.cmd.cmds.staff;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CmdRename extends NightShadeCoreCommand {

    private static CmdRename i = new CmdRename();
    public static CmdRename get(){return i;}

    public CmdRename() {
        this.addAliases("rename");
        this.addParameter(TypeString.get(), "item name");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.TRIAL), RequirementIsPlayer.get());
    }

    @Override
    public void perform() throws MassiveException {
        NSPlayer nsPlayer = NSPlayer.get(sender);
        Player player = nsPlayer.getPlayer();
        if(player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR){
            player.sendMessage(ChatUtils.message("&cYou don't have an item in your hand!"));
            return;
        }

        String newName = ChatUtils.format(this.readArg());
        ItemStack itemStack = player.getItemInHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(newName);
        itemStack.setItemMeta(itemMeta);
        player.sendMessage(ChatUtils.message("&bYour item has been renamed to " + newName));
    }
}
