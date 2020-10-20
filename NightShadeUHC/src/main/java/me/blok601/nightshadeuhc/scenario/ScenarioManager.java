package me.blok601.nightshadeuhc.scenario;

import com.google.common.base.Joiner;
import com.nightshadepvp.core.Core;
import com.nightshadepvp.core.Rank;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.component.ComponentHandler;
import me.blok601.nightshadeuhc.event.ScenarioDisableEvent;
import me.blok601.nightshadeuhc.event.ScenarioEnableEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.ItemBuilder;
import me.blok601.nightshadeuhc.util.PagedInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Blok on 3/28/2017.
 */
public class ScenarioManager implements UHCCommand {

    private UHC uhc;
    private GameManager gameManager;
    private ComponentHandler componentHandler;
    private static ArrayList<Scenario> scenarios;

    public ScenarioManager(UHC uhc, GameManager gameManager, ComponentHandler componentHandler) {
        this.uhc = uhc;
        this.gameManager = gameManager;
        this.componentHandler = componentHandler;
    }

    public void setup() {
        scenarios = new ArrayList<>();
        addScen(new AssaultAndBatteryScenario(), "AAB");
        addScen(new AurophobiaScenario(), "AP");
        addScen(new BackpackScenario(), "BP");
        addScen(new BarebonesScenario(), "BB");
        addScen(new BatsScenario(), "Bats");
        addScen(new BedBombScenario());
        addScen(new BenchBlitzScenario());
        addScen(new BestPvEScenario(), "BPVE");
        addScen(new BetaZombiesScenario(), "BZ");
        addScen(new BleedingSweetsScenario(), "BS");
        addScen(new BlockedScenario());
        addScen(new BloodDiamondsScenario(), "BD");
        addScen(new BloodStoneScenario());
        addScen(new BomberScenario());
        addScen(new SevenDeadlySinsScenario(gameManager));
        addScen(new HealthDonorScenario());
        addScen(new BowlessScenario());
        addScen(new ChickenScenario());
        addScen(new ColdWeaponsScenario(), "CW");
        addScen(new CreeperPongScenario(), "CP");
        addScen(new CrippleScenario());
        addScen(new CutCleanScenario(), "CC");
        addScen(new DepthsScenario());
        addScen(new DiamondLessScenario());
        addScen(new DragonRushScenario());
        addScen(new EggsScenario());
        addScen(new EnchantedDeathScenario());
        addScen(new EntropyScenario());
        addScen(new EveryRoseScenario());
        addScen(new FastGetawayScenario(), "FG");
        addScen(new FeistyBoysScenario());
        addScen(new FirelessScenario());
        addScen(new FourHorsemenScenario());
        addScen(new FlowerPowerScenario());
        addScen(new FurnaceDeathScenario());
        addScen(new GapZapScenario(), "GZ");
        addScen(new GigadrillScenario(), "GD");
        addScen(new GoldLessScenario());
        addScen(new GoneFishinScenario(), "GF");
        addScen(new HasteyBoysScenario(), "HB");
        addScen(new HobbitScenario());
        addScen(new InfiniteEnchanterScenario(), "IE");
        addScen(new KingsScenario());
        addScen(new LandMineScenario());
        addScen(new LootCrateScenario(), "LC");
        addScen(new MysteryScenarios(), "MS");
        addScen(new NoCleanScenario(), "NC");
        addScen(new NoFurnaceScenario());
        addScen(new NoFallScenario(), "NF");
        addScen(new NoTalkingScenario(), "NT");
        addScen(new OneHealScenario(), "OH");
        addScen(new OneHundredHeartsScenario(), "100H");
        addScen(new PermaKillScenario(), "PK");
        addScen(new PuppyPowerScenario(), "PP");
        addScen(new PuppyPlusScenario(), "PP+");
        addScen(new PuppyPowerPlusPlusScenario(), "PP++");
        addScen(new RewardingLongShotsScenario(), "RL");
        addScen(new RiskyRetrievalScenario(), "RR");
        addScen(new Scenario("Rush", "The game progresses quicker", new ItemStack(Material.COMPASS, 1)));
        addScen(new SkycleanScenario());
        addScen(new SkyhighScenario());
        addScen(new SlaveMarketScenario(Core.get()));
        addScen(new SlutCleanScenario(), "SC");
        addScen(new SoupScenario());
        addScen(new SoupPlusScenario());
        addScen(new SurpriseBombScenario());
        addScen(new StockUpScenario());
        addScen(new SuperheroesScenario(gameManager));
        addScen(new SuperHeroesTeamScenario(gameManager));
        addScen(new SwitcherooScenario());
        addScen(new TeamInventoryScenario(), "TI");
        addScen(new TimberScenario());
        addScen(new TimebombScenario(), "TB");
        addScen(new TrashOrTreasureScenario(), "TOT");
        addScen(new UltraParanoidScenario());
        addScen(new UnbreakableBoysScenario());
        addScen(new VeinminerScenario());
        addScen(new VillagerMadnessScenario());
        addScen(new WeakestLinkScenario(), "WL");
        addScen(new WebCageScenario(), "WC");
        addScen(new VanillaPlusScenario());
        //addScen(new VengefulSpiritsScenario(), "VS");
        addScen(new MolesScenario());
        addScen(new GoldenRetrieverScenario());
        addScen(new AnonymousScenario(), "Anon"); //Put this at bottom to test alphabetical order
        addScen(new DoubleDatesScenario());
        addScen(new SecretTeamsScenario());
        addScen(new StickyAndAfraidScenario());
        addScen(new TripleOresScenario());
        addScen(new RottenApplesScenario());
        addScen(new StrengtheningKillScenario());
        addScen(new DependencyScenario(gameManager));
        addScen(new BaldChickenScenario());
        addScen(new LimitationScenario());
        addScen(new RodlessScenario());
        addScen(new BloodCycleScenario(uhc), "BC");
        addScen(new AppleFamineScenario());
        addScen(new CatsEyesScenario());
        addScen(new CobbleHatersScenario());
        addScen(new RocketStartScenario());
        addScen(new SiphonScenario());
        addScen(new PlayerSwapScenario(uhc));
        addScen(new CupidScenario());
        addScen(new SugarRushScenario(uhc));
        addScen(new TrainingRabbitsScenario());
        addScen(new CaptainsScenario());
        addScen(new ChildrenLeftUnattendedScenario(gameManager));
        addScen(new EnchantParanoiaScenario());
        addScen(new BlastMiningScenario());
        addScen(new InfestationScenario());
        addScen(new KrenzinatorScenario(uhc, componentHandler));
        addScen(new SpeedShotScenario());
        addScen(new OPHasteyBoysScenario());
        addScen(new SafelootScenario());
        addScen(new AirBlocksScenario());
        addScen(new TrueLoveScenario());
        addScen(new FastSmeltingScenario());
        addScen(new DoubleOrNothingScenario());
        addScen(new SickToMyStomachScenario());
        addScen(new PermaNightScenario(gameManager));
        addScen(new OPsVsWorldScenario());

        sortScenarios();
    }

    private void sortScenarios() {
        scenarios.sort(Comparator.comparing(Scenario::getName));
    }


    private void addScen(Scenario s) {
        scenarios.add(s);
        s.setScenarioManager(this);
        Bukkit.getPluginManager().registerEvents(s, uhc);
    }

    private void addScen(Scenario s, String abbreviation) {
        scenarios.add(s);
        if (s.getAbbreviation() != null)
            s.setAbbreviation(abbreviation);
        s.setScenarioManager(this);
        Bukkit.getPluginManager().registerEvents(s, uhc);
    }

    public Scenario getScen(String name) {
        return scenarios.stream().filter(scem -> scem.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isCutClean(){
        return getScen("CutClean").isEnabled();
    }

    private void openScenarioGUI(Player player) {
        System.out.println("Current Scens: " + scenarios.size());
        ArrayList<ItemStack> items = new ArrayList<>();

        ItemStack item;
        for (Scenario scenario : ScenarioManager.scenarios) {
            item = new ItemBuilder(scenario.getItem()).name(scenario.isEnabled() ? ChatUtils.format("&a" + scenario.getName()) : ChatUtils.format("&c" + scenario.getName())).lore(scenario.getDesc(), true).make();
            items.add(item);
        }

        new PagedInventory(items, ChatColor.translateAlternateColorCodes('&', "&6Scenarios"), player);

    }

    public Scenario getScen(ItemStack itemStack) {
        for (Scenario scenario : getScenarios()) {
            if (scenario.getItem().equals(itemStack)) {
                return scenario;
            }
        }
        return null;
    }


    public ArrayList<Scenario> getScenarios() {
        return scenarios;
    }

    public List<Scenario> getEnabledScenarios() {
        return scenarios.stream().filter(Scenario::isEnabled).collect(Collectors.toList());
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "scenario"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        if (s instanceof Player) {
            Player p = (Player) s;
            if (args.length == 0) {
                openScenarioGUI(p);
                p.sendMessage(ChatUtils.message("&eOpening Scenario GUI..."));
                return;
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("clear")) {
                    getEnabledScenarios().forEach(scenario -> scenario.setEnabled(false));
                    p.sendMessage(ChatUtils.message("&eAll scenarios have been disabled!"));
                } else if (args[0].equalsIgnoreCase("list")) {
                    HashSet<String> names = new HashSet<>();
                    scenarios.stream().sorted(Comparator.comparing(Scenario::getName)).forEach(scenario -> {
                        if (scenario.isEnabled()) {
                            names.add("&a" + scenario.getName());
                        }
                    });
                    p.sendMessage(ChatUtils.message("&eScenarios: &b" + Joiner.on("&7, &b").join(names)));
                } else {
                    p.sendMessage(ChatUtils.message("&cUsage: /scen"));
                    p.sendMessage(ChatUtils.message("&cUsage: /scen <enable/disable> <scenario>"));
                }
            } else {
                if (args[0].equalsIgnoreCase("enable")) {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }

                    String f = builder.toString().trim();


                    Scenario scenario = this.getScen(f);
                    if (scenario == null) {
                        p.sendMessage(ChatUtils.message("That scenario couldn't be found!"));
                        return;
                    }


                    ScenarioEnableEvent ev = new ScenarioEnableEvent(scenario, p);
                    Bukkit.getServer().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        scenario.setEnabled(true);
                        scenario.onToggle(true, p);
                        p.sendMessage(ChatUtils.message("&aEnabled &e" + scenario.getName() + "!"));
                    }else{
                        p.sendMessage(ChatUtils.message("&e" + scenario.getName() + " &ccould not be enabled because " + ev.getMessage()));
                    }
                } else if (args[0].equalsIgnoreCase("disable")) {
                    StringBuilder builder = new StringBuilder();

                    for (int i = 1; i < args.length; i++) {
                        builder.append(args[i]).append(" ");
                    }

                    String f = builder.toString().trim();


                    Scenario scenario = this.getScen(f);
                    if (scenario == null) {
                        p.sendMessage(ChatUtils.message("That scenario couldn't be found!"));
                        return;
                    }

                    if (!scenario.isEnabled()) {
                        p.sendMessage(ChatUtils.message("&b" + scenario.getName() + "&c is not enabled!"));
                        return;
                    }
                    ScenarioDisableEvent scenarioDisableEvent = new ScenarioDisableEvent(scenario, p);
                    UHC.get().getServer().getPluginManager().callEvent(scenarioDisableEvent);
                    if(!scenarioDisableEvent.isCancelled()){
                        scenario.setEnabled(false);
                        scenario.onToggle(false, p);
                        p.sendMessage(ChatUtils.message("&cDisabled &e" + scenario.getName() + "!"));
                    }
                } else {
                    p.sendMessage(ChatUtils.message("&cUsage: /scen"));
                    p.sendMessage(ChatUtils.message("&cUsage: /scen <enable/disable> <scenario>"));
                    return;
                }
            }
        }
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return Rank.TRIAL;
    }

    @Override
    public boolean hasRequiredRank() {
        return true;
    }
}
