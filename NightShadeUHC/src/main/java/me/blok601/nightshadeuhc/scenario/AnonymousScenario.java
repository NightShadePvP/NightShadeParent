package me.blok601.nightshadeuhc.scenario;

import de.robingrether.idisguise.disguise.PlayerDisguise;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.event.PlayerJoinGameLateEvent;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;

/**
 * Created by Blok on 8/3/2018.
 */
public class AnonymousScenario extends Scenario {

    private static  String disuigse;

    public AnonymousScenario() {
        super("Anonymous", "Everyone is disguised as the same person", "Anon", new ItemBuilder(Material.NAME_TAG).name("Anonymous").make());
        disuigse = "Notch";
    }

    @EventHandler
    public void onGameStart(GameStartEvent e){
        if(!isEnabled()) return;
        assignAll();
    }

    @EventHandler
    public void onJoinGameLate(PlayerJoinGameLateEvent event){
        if(!isEnabled())  return;
        assign(UHCPlayer.get(event.getPlayer()));
    }

    public static String getDisuigse() {
        return disuigse;
    }

    public static void setDisuigse(String disuigse) {
        AnonymousScenario.disuigse = disuigse;
    }

    public void assignAll(){
        UHCPlayerColl.get().getAllOnline().stream().filter(uhcPlayer -> !uhcPlayer.isSpectator()).forEach(this::assign);
    }

    public void assign(UHCPlayer uhcPlayer){
        UHC.getApi().disguise(uhcPlayer.getPlayer(), new PlayerDisguise(disuigse));
        uhcPlayer.msg(ChatUtils.format( "&4Anonymous&8» &eYou are now disguised as" + disuigse));
        uhcPlayer.setDisguised(true);
        uhcPlayer.setDisguisedName(disuigse);
    }


}
