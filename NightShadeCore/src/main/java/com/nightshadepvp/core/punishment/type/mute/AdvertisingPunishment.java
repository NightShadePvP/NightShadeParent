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
public class AdvertisingPunishment extends AbstractPunishment {

    public AdvertisingPunishment() {
        super("Advertising", Material.ITEM_FRAME, PunishmentType.MUTE);

        this.addChild(new Punishment("Advertising (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Advertising &8(&51st Offense&8)")
                .lore("&eClick to mute the player for 15m for advertising").make(),
                this, Collections.singletonList("mute %player% 15m Advertising (1st Offense)"), PunishmentType.MUTE
        ), 20);

        this.addChild(new Punishment("Advertising (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Advertising &8(&52nd Offense&8)")
                .lore("&eClick to mute the player for 45 for advertising").make(),
                this, Collections.singletonList("mute %player% 45m Advertising (2nd Offense)"), PunishmentType.MUTE
        ), 21);

        this.addChild(new Punishment("Advertising (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Advertising &8(&53rd Offense&8)")
                .lore("&eClick to mute the player for 2h for advertising").make(),
                this, Collections.singletonList("mute %player% 2h Advertising (3rd Offense)"), PunishmentType.MUTE
        ), 22);
    }

}
