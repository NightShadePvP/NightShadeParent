package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/25/2018.
 */
public class XrayPunishment extends AbstractPunishment {

    public XrayPunishment() {
        super("Xray", Material.DIAMOND_ORE, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Xray (1st Offense)",
                new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Xray &8(&51st Offense&8)")
                        .lore("&eBan the player for 2 months for Xray").make(), this,
                Collections.singletonList("banip %player% 2mo Xray (1st Offense)"), PunishmentType.BAN, this.getOffenseType()), 20
        );

        this.addChild(new Punishment("Xray (2nd Offense)",
                new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                        .name("&5Xray &8(&52nd Offense&8)")
                        .amount(2)
                        .lore("&eBan the player permanently for Xray").make(), this,
                Collections.singletonList("banip %player% Xray (2nd Offense)"), PunishmentType.BAN, this.getOffenseType(), Rank.SENIOR), 21
        );

    }
}
