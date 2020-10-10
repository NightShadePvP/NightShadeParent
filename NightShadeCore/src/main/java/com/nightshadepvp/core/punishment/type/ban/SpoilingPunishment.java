package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.Punishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.PunishmentType;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class SpoilingPunishment extends AbstractPunishment {

    public SpoilingPunishment() {
        super("Spoiling", Material.ARROW, PunishmentType.MUTE);

        this.addChild(new Punishment("Spoiling (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spoiling &8(&51st Offense&8)")
                .lore("&eClick to mute the player for 30m for Spoiling").make(),
                this, Collections.singletonList("mute %player% 15m Spoiling (1st Offense)"), PunishmentType.MUTE
        ), 20);

        this.addChild(new Punishment("Spoiling (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spoiling &8(&52nd Offense&8)")
                .amount(2)
                .lore("&eClick to mute the player for 1h for Spoiling").make(),
                this, Collections.singletonList("mute %player% 1h Spoiling (2nd Offense)"), PunishmentType.MUTE
        ), 21);

        this.addChild(new Punishment("Spoiling (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spoiling &8(&53rd Offense&8)")
                .amount(3)
                .lore("&eClick to mute the player for 1d for Spoiling").make(),
                this, Collections.singletonList("banip %player% 1d Spoiling (3rd Offense)"), PunishmentType.BAN
        ), 22);
    }
}
