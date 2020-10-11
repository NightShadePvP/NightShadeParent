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
        super("Advertising Server IPs", Material.ITEM_FRAME, PunishmentType.MUTE);

        this.addChild(new Punishment("Advertising Server IPs(1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Advertising Server IPs&8(&51st Offense&8)")
                .lore("&eClick to ban the player for 7d for advertising server ips").make(),
                this, Collections.singletonList("banip %player% 7d Advertising Server IPs(1st Offense)"), PunishmentType.MUTE
        ), 20);

    }

}
