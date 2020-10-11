package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.Punishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.PunishmentType;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/25/2018.
 */
public class IllegalMiningPunishment extends AbstractPunishment {

    public IllegalMiningPunishment() {
        super("Illegal Mining", Material.DIAMOND_PICKAXE, PunishmentType.WARNING);

        this.addChild(new Punishment("Illegal Mining (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Illegal Mining &8(&51st Offense&8)")
                .lore("&eClick to warn the player for Illegal Mining").make(),
                this, Collections.singletonList("warn %player% Illegal Mining (1/1)"), PunishmentType.WARNING
        ), 20);

        this.addChild(new Punishment("Illegal Mining (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .amount(2)
                .name("&5Illegal Mining &8(&52nd Offense&8)")
                .lore("&eClick to warn the player for Illegal Mining").make(),
                this, Collections.singletonList("banip %player% 7d Illegal Mining (2nd Offense)"), PunishmentType.WARNING
        ), 21);
    }
}
