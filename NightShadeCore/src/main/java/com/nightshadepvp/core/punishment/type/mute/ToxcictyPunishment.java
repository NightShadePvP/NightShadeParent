package com.nightshadepvp.core.punishment.type.mute;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class ToxcictyPunishment extends AbstractPunishment {
    public ToxcictyPunishment() {
        super("Toxicity", Material.BOOK, OffenseType.CHAT);
        this.addChild(new Punishment("Toxicity (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Toxicity &8(&51st Offense&8)")
                .loreWithNuke("&eClick to mute the player for 30m for Toxicity").make(),
                this, Collections.singletonList("mute %player% 30m Toxicity (1st Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 20);

        this.addChild(new Punishment("Toxicity (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Toxicity &8(&52nd Offense&8)")
                .amount(2)
                .loreWithNuke("&eClick to mute the player for 1h for Toxicity").make(),
                this, Collections.singletonList("mute %player% 1h Toxicity (2nd Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 21);

        this.addChild(new Punishment("Toxicity (3rd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Toxicity &8(&53rd Offense&8)")
                .amount(3)
                .loreWithNuke("&eClick to mute the player for 3d for Toxicity").make(),
                this, Collections.singletonList("mute %player% 3d Toxicity (3rd Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 22);
    }
}
