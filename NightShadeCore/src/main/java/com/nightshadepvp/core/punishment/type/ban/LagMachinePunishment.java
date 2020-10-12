package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class LagMachinePunishment extends AbstractPunishment {

    public LagMachinePunishment(){
        super("Lag Machine", Material.REDSTONE, OffenseType.GAMEPLAY);

        this.addChild(new Punishment("Lag Machine (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Lag Machine &8(&51st Offense&8)")
                .lore("&eClick to warn the player for creating a lag machine").make(),
                this, Collections.singletonList("warn %player% Creating a Lag Machine"), PunishmentType.WARNING, this.getOffenseType()
        ), 20);

        this.addChild(new Punishment("Lag Machine (2nd Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Lag Machine &8(&52nd Offense&8)")
                .amount(2)
                .lore("&eClick to ban the player for 3d for creating a lag machine").make(),
                this, Collections.singletonList("banip %player% 3d Creating a Lag Machine (2nd Offense)"), PunishmentType.BAN, this.getOffenseType()
        ), 21);
    }
}
