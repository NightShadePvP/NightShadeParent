package com.nightshadepvp.core.punishment.type.mute;

import com.nightshadepvp.core.punishment.AbstractPunishment;
import com.nightshadepvp.core.punishment.Punishment;
import com.nightshadepvp.core.punishment.PunishmentHandler;
import com.nightshadepvp.core.punishment.PunishmentType;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class ToxcictyPunishment extends AbstractPunishment {
    public ToxcictyPunishment() {
        super("Toxicity", Material.SPIDER_EYE, PunishmentType.MUTE );
        this.addChild(new Punishment("Toxicity (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Toxicity &8(&51st Offense&8)")
                .lore("&eClick to mute the player for 15m for Toxicity").make(),
                this, Collections.singletonList("mute %player% 30m Toxicity (1st Offense)"), PunishmentType.MUTE
        ), 20);

        this.addChild(new Punishment("Toxicity (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Toxicity &8(&52nd Offense&8)")
                .lore("&eClick to mute the player for 45 for Toxicity").make(),
                this, Collections.singletonList("mute %player% 1h Toxicity (2nd Offense)"), PunishmentType.MUTE
        ), 21);

        this.addChild(new Punishment("Toxicity (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Toxicity &8(&53rd Offense&8)")
                .lore("&eClick to mute the player for 2h for Toxicity").make(),
                this, Collections.singletonList("mute %player% 3d Toxicity (3rd Offense)"), PunishmentType.MUTE
        ), 22);
    }
}
