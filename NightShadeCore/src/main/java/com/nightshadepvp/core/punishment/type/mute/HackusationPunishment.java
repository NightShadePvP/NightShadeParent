package com.nightshadepvp.core.punishment.type.mute;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class HackusationPunishment extends AbstractPunishment {

    public HackusationPunishment() {
        super("Hackusation", Material.ARROW, OffenseType.CHAT);

        this.addChild(new Punishment("Hackusation (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Hackusation &8(&51st Offense&8)")
                .loreWithNuke("&eClick to warn the player for hackusations in public chat").make(),
                this, Collections.singletonList("warn %player% Hackusation (1st Offense)"), PunishmentType.WARNING, this.getOffenseType()
        ), 20);

        this.addChild(new Punishment("Hackusation (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Hackusation &8(&52nd Offense&8)")
                .amount(2)
                .loreWithNuke("&eClick to mute the player for 15 minutes for hackusations in public chat").make(),
                this, Collections.singletonList("mute %player% 15m Hackusations (2nd Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 21);
    }
}
