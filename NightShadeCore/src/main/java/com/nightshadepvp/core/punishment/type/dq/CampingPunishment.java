package com.nightshadepvp.core.punishment.type.dq;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/26/2018.
 */
public class CampingPunishment extends AbstractPunishment {

    public CampingPunishment() {
        super("Camping During Meetup", Material.LADDER, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Camping During Meetup (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Camping During Meetup&8(&51st Offense&8)")
                .loreWithNuke("&eClick to warn the player for camping during meetup (1st offense)").make(),
                this, Collections.singletonList("warn %player% Camping During Meetup 1/1"), PunishmentType.WARNING, this.getOffenseType()
        ), 22);

        this.addChild(new Punishment("Camping During Meetup (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Camping During Meetup &8(&52nd Offense&8)")
                .amount(2)
                .loreWithNuke("&eClick to DQ the player for camping during meetup (2nd offense)").make(),
                this, Collections.singletonList(""), PunishmentType.DQ, this.getOffenseType()
        ), 23);
    }
}
