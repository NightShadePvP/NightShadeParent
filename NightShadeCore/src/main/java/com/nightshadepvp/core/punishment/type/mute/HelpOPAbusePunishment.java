package com.nightshadepvp.core.punishment.type.mute;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class HelpOPAbusePunishment extends AbstractPunishment {


    public HelpOPAbusePunishment() {
        super("HelpOP/Report Abuse", Material.PAPER, OffenseType.CHAT);

        this.addChild(new Punishment("HelpOP/Report Abuse (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5HelpOP/Report Abuse &8(&51st Offense&8)")
                .loreWithNuke("Click to warn the player for HelpOP/Report Abuse (1st Offense)")
                .make(),
                this, Collections.singletonList("warn %player% HelpOP/Report Abuse (1st Offense)"), PunishmentType.WARNING, OffenseType.CHAT), 20
        );

        this.addChild(new Punishment("HelpOP/Report Abuse (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5HelpOP/Report Abuse &8(&52nd Offense&8)")
                .loreWithNuke("Click to helpop mute the player for 1h for HelpOP/Report Abuse (2nd Offense)")
                .amount(2)
                .make(),
                this, Collections.singletonList("helpopmute %player% 60m HelpOP/Report Abuse (2nd Offense)"), PunishmentType.MUTE, OffenseType.CHAT), 20
        );
    }
}
