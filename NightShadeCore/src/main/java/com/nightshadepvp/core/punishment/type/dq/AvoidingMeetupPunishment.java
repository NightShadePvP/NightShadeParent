package com.nightshadepvp.core.punishment.type.dq;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class AvoidingMeetupPunishment extends AbstractPunishment {
    public AvoidingMeetupPunishment() {
        super("Avoiding Meetup", Material.COMPASS, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Avoiding Meetup (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Avoiding Meetup &8(&51st Offense&8)")
                .lore("&eClick to warn the player for Avoiding Meetup 1st offense").make(),
                this, Collections.singletonList("warn %player% Avoiding Meetup 1/2"), PunishmentType.WARNING, this.getOffenseType()
        ), 20);

        this.addChild(new Punishment("Avoiding Meetup (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Avoiding Meetup &8(&52nd Offense&8)")
                .lore("&eClick to warn the player for Avoiding Meetup 2nd offense").make(),
                this, Collections.singletonList("warn %player% Avoiding Meetup 2/2"), PunishmentType.WARNING, this.getOffenseType()
        ), 21);

        this.addChild(new Punishment("Avoiding Meetup (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Avoiding Meetup &8(&53rd Offense&8)")
                .amount(2)
                .lore("&eClick to DQ the player for Avoiding Meetup 3rd offense").make(),
                this, Collections.singletonList(""), PunishmentType.DQ, this.getOffenseType()
        ), 22);
    }
}
