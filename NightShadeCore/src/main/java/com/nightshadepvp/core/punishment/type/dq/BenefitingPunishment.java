package com.nightshadepvp.core.punishment.type.dq;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/26/2018.
 */
public class BenefitingPunishment extends AbstractPunishment {

    public BenefitingPunishment() {
        super("Benefiting", Material.GOLD_INGOT, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Benefiting", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Benefiting &8(&51st Offense&8)")
                .loreWithNuke("&eClick to DQ the player for benefiting").make(),
                this, Collections.singletonList(""), PunishmentType.DQ, this.getOffenseType()
        ), 22);
    }
}
