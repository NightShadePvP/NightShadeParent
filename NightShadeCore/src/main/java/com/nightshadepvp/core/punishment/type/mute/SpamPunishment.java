package com.nightshadepvp.core.punishment.type.mute;

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
public class SpamPunishment extends AbstractPunishment {

    public SpamPunishment() {
        super("Spam", Material.BOOKSHELF, PunishmentType.MUTE);

        this.addChild(new Punishment("Spam (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spam &8(1st Offense&8)")
                .lore("&eClick to mute the player for 15m for spam").make(),
                this, Collections.singletonList("mute %player% 15m Spam (1st Offense)"), PunishmentType.MUTE
        ), 20);

        this.addChild(new Punishment("Spam (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spam &8(2nd Offense&8)")
                .amount(2)
                .lore("&eClick to mute the player for 30m for spam").make(),
                this, Collections.singletonList("mute %player% 45m Spam (2nd Offense)"), PunishmentType.MUTE
        ), 21);
    }
}
