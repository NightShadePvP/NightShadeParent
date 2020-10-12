package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class PunishmentEvadingPunishment extends AbstractPunishment {

    public PunishmentEvadingPunishment() {
        super("Evading Punishments", Material.SLIME_BALL, OffenseType.GAMEPLAY);


        this.addChild(new Punishment("Evading Punishments", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Evading Punishments")
                .loreWithNuke("&eClick to ban the player for Evading Punishments").make(), this,
                Collections.singletonList("banip %player% Evading Punishments"), PunishmentType.BAN, this.getOffenseType(), Rank.SENIOR), 20);
    }
}
