package me.blok601.nightshadeuhc.entity;

import com.google.common.collect.Lists;
import com.massivecraft.massivecore.store.SenderColl;
import com.massivecraft.massivecore.store.SenderEntity;
import com.nightshadepvp.core.entity.MConf;
import com.nightshadepvp.core.store.NSStore;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.object.PlayerStatus;
import org.bukkit.GameMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UHCPlayerColl extends SenderColl<UHCPlayer> {
    // -------------------------------------------- //
    // INSTANCE & CONSTRUCT
    // -------------------------------------------- //

    private static UHCPlayerColl i = new UHCPlayerColl();

    public static UHCPlayerColl get() {
        return i;
    }

    public UHCPlayerColl() {
        super("uhc_uhcplayers", UHCPlayer.class, NSStore.getDb(), UHC.get());
    }

    // -------------------------------------------- //
    // STACK TRACEABILITY
    // -------------------------------------------- //

    @Override
    public void onTick() {
        super.onTick();
    }

    // -------------------------------------------- //
    // EXTRAS
    // -------------------------------------------- //

    @Override
    public long getCleanInactivityToleranceMillis() {
        return MConf.get().cleanInactivityToleranceMillis;
    }

    public List<UHCPlayer> getAllPlaying() {
        List<UHCPlayer> p = Lists.newArrayList();
        for (UHCPlayer uhcPlayer : getAllOnlinePlayers()) {
            if (uhcPlayer.isSpectator()) continue;
            if (uhcPlayer.getPlayer().getGameMode() != GameMode.SURVIVAL) continue;
            if (uhcPlayer.getPlayerStatus() != PlayerStatus.PLAYING) continue;

            p.add(uhcPlayer);
        }

        return p;
    }

    public Collection<UHCPlayer> getSpectators() {
        return getAllOnlinePlayers().stream().filter(UHCPlayer::isSpectator).collect(Collectors.toList());
    }

    public Collection<UHCPlayer> getAllOnlinePlayers() {
        return getAllOnline().stream().filter(SenderEntity::isPlayer).collect(Collectors.toList());
    }

}
