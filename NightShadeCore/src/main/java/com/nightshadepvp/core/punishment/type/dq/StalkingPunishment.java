package com.nightshadepvp.core.punishment.type.dq;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class StalkingPunishment extends AbstractPunishment {

    public StalkingPunishment() {
        super("Excessive Stalking", Material.WEB, OffenseType.GAMEPLAY); //Stalking media/ ranks is a 1d ban

        this.addChild(new Punishment("Excessive Stalking (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Excessive Stalking &8(&51st Offense&8)")
                .lore("&eClick to warn the player for excessive stalking (1st offense)").make(),
                this, Collections.singletonList("warn %player% Excessive Stalking (1st Offense)"), PunishmentType.WARNING, this.getOffenseType()
        ), 20);

        this.addChild(new Punishment("Stalking (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Stalking &8(&52nd Offense&8)")
                .lore("&eClick to DQ the player for stalking").make(),
                this, Collections.singletonList(""), PunishmentType.DQ, this.getOffenseType()
        ), 21);

        this.addChild(new Punishment("Excessive Stalking (2nd Offense - Media)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Excessive Stalking &8(&52nd Offense&8 - Media)")
                .lore("&eExcessive Click to ban the player for stalking a Media rank player").make(),
                this, Collections.singletonList("ban %player% 1d Excessive Stalking of Media Rank"), PunishmentType.BAN, this.getOffenseType()
        ), 22);
    }
}
