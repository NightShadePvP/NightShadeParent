package com.nightshadepvp.core.punishment.type.ban;

import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.punishment.*;
import com.nightshadepvp.core.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Collections;

public class BreakingGameplayRulesPunishment extends AbstractPunishment {


    public BreakingGameplayRulesPunishment() {
        super("Breaking Gameplay Rules", Material.BEDROCK, OffenseType.GAMEPLAY);


        this.addChild(new Punishment("Breaking Gameplay Rules (1st Offense)", new ItemBuilder(PunishmentHandler.getInstance().getChildStack())
                .name("&5Breaking Gameplay Rules &8(&51st Offense&8)")
                .loreWithNuke("&eBan the player for 3 days for breaking gameplay rules").make(),
                this, Collections.singletonList("ban %player% 3d Breaking Gameplay Rules (1/1)"), PunishmentType.BAN,
                this.getOffenseType(),
                Rank.TRIAL
        ), 20);
    }
}
