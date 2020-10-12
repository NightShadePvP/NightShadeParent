package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class iPvPPunishment extends AbstractPunishment {

    public iPvPPunishment() {
        super("iPvP", Material.FLINT_AND_STEEL, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("iPvP (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5iPvP &8(&51st Offense&8)")
                .loreWithNuke("&eClick to ban the player for 3d for iPvP").make(),
                this, Collections.singletonList("ban %player% 3d iPvP (1st Offense)"), PunishmentType.BAN,
                this.getOffenseType()
        ), 20);
    }
}
