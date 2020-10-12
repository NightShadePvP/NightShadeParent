package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class SpoilingPunishment extends AbstractPunishment {

    public SpoilingPunishment() {
        super("Spoiling", Material.DIAMOND_CHESTPLATE, OffenseType.CHAT);

        this.addChild(new Punishment("Spoiling (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spoiling &8(&51st Offense&8)")
                .loreWithNuke("&eClick to mute the player for 1h for Spoiling").make(),
                this, Collections.singletonList("mute %player% 1h Spoiling (1st Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 20);

        this.addChild(new Punishment("Spoiling (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spoiling &8(&52nd Offense&8)")
                .amount(2)
                .loreWithNuke("&eClick to mute the player for 3d for Spoiling").make(),
                this, Collections.singletonList("mute %player% 3d Spoiling (2nd Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 21);

        this.addChild(new Punishment("Spoiling (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Spoiling &8(&53rd Offense&8)")
                .amount(3)
                .loreWithNuke("&eClick to mute the player for 7d for Spoiling").make(),
                this, Collections.singletonList("banip %player% 7d Spoiling (3rd Offense)"), PunishmentType.BAN, this.getOffenseType()
        ), 22);
    }
}
