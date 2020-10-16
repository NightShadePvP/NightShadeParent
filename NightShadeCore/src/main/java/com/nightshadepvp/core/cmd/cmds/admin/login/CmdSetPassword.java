package com.nightshadepvp.core.cmd.cmds.admin.login;

import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.command.type.primitive.TypeString;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.cmd.NightShadeCoreCommand;
import com.nightshadepvp.core.cmd.req.ReqRankHasAtLeast;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.utils.fanciful.FancyMessage;
import com.nightshadepvp.core.utils.ChatUtils;
import com.nightshadepvp.core.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Blok on 6/20/2019.
 */
public class CmdSetPassword extends NightShadeCoreCommand implements CmdLogin.Encrypter {

    private static CmdSetPassword i = new CmdSetPassword();

    public static CmdSetPassword get() {
        return i;
    }

    public CmdSetPassword() {
        this.setAliases("setpassword", "setlogin");
        this.addRequirements(ReqRankHasAtLeast.get(Rank.ADMIN), RequirementIsPlayer.get());
        this.addParameter(TypeString.get(), "password");
    }

    @Override
    public void perform() throws MassiveException {
        String password = this.readArg();
        NSPlayer nsPlayer = NSPlayer.get(sender); //Is a player
        if (password.length() < 5 || password.length() > 16) {
            nsPlayer.msg(ChatUtils.message("&cYour password must be 5-16 characters!"));
            return;
        }

        if (nsPlayer.getAdminPassword() != null) {
            nsPlayer.msg(ChatUtils.message("&cYou already have a password! Use /newpassword to change your password."));
            return;
        }

        //Now they have the correct password criteria, and have no password -> set their new password
        nsPlayer.msg(ChatUtils.message("&eAre you sure you want to set your password as &a" + password + " &e?"));
        FancyMessage fancyMessage = new FancyMessage("Please confirm within 10 seconds by doing /confirm or click this message to confirm your new password.");
        fancyMessage.color(ChatColor.YELLOW).command("/confirm");
        fancyMessage.send(nsPlayer.getPlayer());
        PlayerUtils.getToConfirm().put(nsPlayer.getUuid(), () -> {

            byte[] encryptedPassword = encrypt(password.getBytes());
            nsPlayer.setAdminPassword(encryptedPassword);
            nsPlayer.msg(ChatUtils.message("&eYour password has been set!"));
            PlayerUtils.getToConfirm().remove(nsPlayer.getUuid());

        });
        new BukkitRunnable() {
            @Override
            public void run() {
                PlayerUtils.getToConfirm().remove(nsPlayer.getUuid());
            }
        }.runTaskLater(Core.get(), 20 * 10);
    }

    @Override
    public byte[] encrypt(byte[] data) {
        byte[] enc = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            enc[i] = (byte) ((i % 2 == 0) ? data[i] + 1 : data[i] - 1);
        }

        return enc;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        return new byte[0];
    }
}
