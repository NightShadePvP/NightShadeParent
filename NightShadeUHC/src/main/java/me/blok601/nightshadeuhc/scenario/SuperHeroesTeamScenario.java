package me.blok601.nightshadeuhc.scenario;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.event.GameStartEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class SuperHeroesTeamScenario extends Scenario{
    private GameManager gameManager;
    public static HashMap<UUID, SuperHeroType> powers;



    public SuperHeroesTeamScenario(GameManager gameManager) {
        super("Superheroes Teams", "Each player will gain a special ability. The powers are speed 2, strength 1, resistance 1, invisibility, 10 extra hearts, and jump boost 4.", new ItemBuilder(Material.BREWING_STAND_ITEM).name("Superheroes").make());
        this.gameManager = gameManager;
        powers = Maps.newHashMap();
    }


    @EventHandler
    public void onGameStart(GameStartEvent e){
        if(!isEnabled()) return;
        Player tempPlayer;
        for (Team team : TeamManager.getInstance().getTeams()) {
            ArrayList<SuperHeroType> jew = new ArrayList<>();
            for (SuperHeroType t : SuperHeroType.values()) {
                jew.add(t);
            }
            Collections.shuffle(jew);
            Collections.shuffle(team.getMembers());
            int times = team.getMembers().size() - 1;
            for (int i = 0; i <= times; i++){
                tempPlayer = Bukkit.getPlayer(team.getMembers().get(i));

                if(powers.containsKey(tempPlayer.getUniqueId())) {
                    continue;
                }
                SuperHeroType type = jew.get(0);


                powers.put(tempPlayer.getUniqueId(), type);
                jew.remove(0);

                type.giveEffects(tempPlayer);
                sendMessage(tempPlayer, "&bYour superpower is: " + type.getName());
            }

        }
        UHCPlayerColl.get().getAllPlaying().forEach(uhcPlayer -> {
            Player p = (Player) uhcPlayer;
            Random random = ThreadLocalRandom.current();

            if (TeamManager.getInstance().getTeam(p) == null) {
                SuperHeroType type = SuperHeroType.values()[random.nextInt(SuperHeroType.values().length)];
                if (powers.containsKey(uhcPlayer.getUuid())) {
                    return;
                }

                powers.put(uhcPlayer.getUuid(), type);
                type.giveEffects(p);

                uhcPlayer.msg(ChatUtils.format(getPrefix() + "&eYour super power is: " + type.getName()));
            }
        });
    }

    public enum SuperHeroType {
        SPEED("Speed 2, Haste 2", Lists.newArrayList(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1),
                new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1))),


        STRENGTH("Strength 1", Lists.newArrayList(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0))),


        RES("Resistance 1, Fire Resistance 1", Lists.newArrayList(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0), new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0))),


        HEALTH("10 Extra Hearts", Collections.emptyList()),


        JUMP("Jump Boost 4, Haste 2, Saturation", Lists.newArrayList(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 3), new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1), new PotionEffect(PotionEffectType.SATURATION, Integer.MAX_VALUE, 9)));

        private String name;
        private List<PotionEffect> effect;

        SuperHeroType(String name, List<PotionEffect> effect) {
            this.name = ChatUtils.format(name);
            this.effect = effect;
        }

        public String getName() {
            return name;
        }

        public List<PotionEffect> getEffecst() {
            return effect;
        }

        public void giveEffects(Player player) {
            if (this == HEALTH) {
                player.setMaxHealth(40.0);
                player.setHealth(40.0);
                return;
            }

            effect.forEach(player::addPotionEffect);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e){

        if(!isEnabled()){
            return;
        }

        if(e.getEntity() instanceof Player){
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

}

