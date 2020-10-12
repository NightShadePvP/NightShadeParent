package com.nightshadepvp.core.punishment.type.mute;

import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

/**
 * Created by Blok on 8/27/2018.
 */
public class AdvertisingPunishment extends AbstractPunishment {

    public AdvertisingPunishment() {
        super("Advertising Server IPs", Material.EMPTY_MAP, OffenseType.CHAT);

        this.addChild(new Punishment("Advertising Server IPs(1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Advertising Server IPs&8(&51st Offense&8)")
                .lore("&eClick to mute the player for 7d for advertising server ips").make(),
                this, Collections.singletonList("mute %player% 7d Advertising Server IPs (1st Offense)"), PunishmentType.MUTE, this.getOffenseType()
        ), 20);

    }

}
