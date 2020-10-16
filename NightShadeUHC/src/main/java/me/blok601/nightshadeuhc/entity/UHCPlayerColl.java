package me.blok601.nightshadeuhc.entity;

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
        return getAllOnlinePlayers().stream().filter(SenderEntity::isPlayer)
                .filter(uhcPlayer -> !uhcPlayer.isSpectator())
                .filter(uhcPlayer -> uhcPlayer.getPlayer().getGameMode() == GameMode.SURVIVAL)
                .filter(uhcPlayer -> uhcPlayer.getPlayerStatus() == PlayerStatus.PLAYING).collect(Collectors.toList());
    }

    public Collection<UHCPlayer> getSpectators() {
        ArrayList<UHCPlayer> list = new ArrayList<>();
        getAllOnline().stream().filter(UHCPlayer::isSpectator).forEach(list::add);
        return list;
    }

    public Collection<UHCPlayer> getAllOnlinePlayers() {
        return getAllOnline().stream().filter(SenderEntity::isPlayer).collect(Collectors.toList());
    }

}
