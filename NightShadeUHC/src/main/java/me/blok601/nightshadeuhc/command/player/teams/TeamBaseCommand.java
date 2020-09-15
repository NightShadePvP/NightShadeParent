package me.blok601.nightshadeuhc.command.player.teams;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.nightshadepvp.core.Rank;
import com.nightshadepvp.core.entity.NSPlayer;
import com.nightshadepvp.core.fanciful.FancyMessage;
import me.blok601.nightshadeuhc.UHC;
import me.blok601.nightshadeuhc.command.UHCCommand;
import me.blok601.nightshadeuhc.entity.UHCPlayer;
import me.blok601.nightshadeuhc.entity.UHCPlayerColl;
import me.blok601.nightshadeuhc.entity.object.CachedColor;
import me.blok601.nightshadeuhc.entity.object.Team;
import me.blok601.nightshadeuhc.event.TeamColorEvent;
import me.blok601.nightshadeuhc.manager.GameManager;
import me.blok601.nightshadeuhc.manager.TeamManager;
import me.blok601.nightshadeuhc.scenario.Scenario;
import me.blok601.nightshadeuhc.scenario.ScenarioManager;
import me.blok601.nightshadeuhc.scoreboard.ScoreboardHandler;
import me.blok601.nightshadeuhc.util.ChatUtils;
import me.blok601.nightshadeuhc.util.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.*;

public class TeamBaseCommand implements UHCCommand{

    private ScenarioManager scenarioManager;

    public TeamBaseCommand(ScenarioManager scenarioManager) {
        this.scenarioManager = scenarioManager;
    }

    private HashMap<String, String> invites = new HashMap<>();

      //public static HashMap<UUID, String> colors = new HashMap<>();

	public void sendHelp(Player p){
        p.sendMessage(ChatUtils.message("&aTeam Commands"));
        p.sendMessage(ChatUtils.message("&a/team create"));
        p.sendMessage(ChatUtils.message("&a/team invite <player>"));
        p.sendMessage(ChatUtils.message("&a/team kick <player>"));
        p.sendMessage(ChatUtils.message("&a/team list [player]"));
        p.sendMessage(ChatUtils.message("&a/team join <player>"));


	}
	
	public void sendAdminHelp(Player p){
	    sendHelp(p);
        p.sendMessage(ChatUtils.message("&eStaff Commands"));
        p.sendMessage(ChatUtils.message("&e/team limit <size>"));
        p.sendMessage(ChatUtils.message("&e/team reset"));
        p.sendMessage(ChatUtils.message("&e/team man <on/off>"));
        p.sendMessage(ChatUtils.message("&e/team ff <on/off>"));
        p.sendMessage(ChatUtils.message("&e/team remove <player>"));
        p.sendMessage(ChatUtils.message("&e/team random"));
	}

    @Override
    public String[] getNames() {
        return new String[]{
                "team"
        };
    }

    @Override
    public void onCommand(CommandSender s, Command cmd, String l, String[] args) {
        if(s instanceof Player){
            final Player p = (Player) s;
            NSPlayer user = NSPlayer.get(p.getUniqueId());
            System.out.println(args.length);

            if(args.length == 0){
                if(user.hasRank(Rank.TRIAL)){
                    sendAdminHelp(p);
                    System.out.println("Sending help line 33");
                    return;
                }else{
                    sendHelp(p);
                    System.out.println("Sending help line 36");
                    return;
                }
            }else if(args.length == 1){
                if(args[0].equalsIgnoreCase("create")){
                    //Check if team man is on
                    if(TeamManager.getInstance().isTeamManagement()){
                        if(TeamManager.getInstance().getTeam(p) != null){
                            p.sendMessage(ChatUtils.message("&cYou are already on a team!"));
                            return;
                        }

                        int ts = TeamManager.getInstance().getTeams().size()+1;

                        TeamManager.getInstance().addTeam(new Team("UHC" + ts, p, ChatUtils.generateTeamColor()));
                        p.sendMessage(ChatUtils.message("&eSuccessfully created team!"));
                    }else{
                        p.sendMessage(ChatUtils.message("&cTeam management is currently disabled!"));
                        return;
                    }

                }else if(args[0].equalsIgnoreCase("list")){
                    if(TeamManager.getInstance().getTeam(p) == null){
                        p.sendMessage(ChatUtils.message("&cYou aren't on a team!"));
                        return;
                    }

                    Team t = TeamManager.getInstance().getTeam(p);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String str : t.getMembers()) {
                        stringBuilder.append("&e").append(str).append("&7, ");
                    }

                    String f = stringBuilder.toString().trim();
                    p.sendMessage(ChatUtils.format("&b&m---------------------------------"));
                    p.sendMessage(ChatUtils.format("&aYour Team&8: " + f.substring(0, f.length() - 1)));
                    p.sendMessage(ChatUtils.format("&b&m---------------------------------"));
                }else if(args[0].equalsIgnoreCase("leave")){
                    if(TeamManager.getInstance().isTeamManagement()){
                        if(TeamManager.getInstance().getTeam(p) == null){
                            p.sendMessage(ChatUtils.message("&cYou aren't on a team!"));
                            return;
                        }

                        Team t = TeamManager.getInstance().getTeam(p);
                        if(t.getMembers().size() == 1){
                            t.removeMember(p);
                            TeamManager.getInstance().removeTeam(t);
                            p.sendMessage(ChatUtils.message("&eYour team has disbanded!"));
                            return;
                        }else{
                            t.removeMember(p);
                            p.sendMessage(ChatUtils.message("&eYou have left your team!"));
                            for (String str : t.getMembers()){
                                Bukkit.getPlayer(str).sendMessage(ChatUtils.message("&6" + p.getName() + " &ehas left the team!"));
                            }
                            return;
                        }

                    }else{
                        p.sendMessage(ChatUtils.message("&cTeam management is currently disabled!"));
                        return;
                    }
                }else if(args[0].equalsIgnoreCase("reset")){
                    if(user.hasRank(Rank.TRIAL)){
                        TeamManager.getInstance().resetTeams();
                        p.sendMessage(ChatUtils.message("&eTeams have been reset!"));
                    }else{
                        p.sendMessage(ChatUtils.message("&cYou require the HOST rank to do this command!"));
                    }
                }else if(args[0].equalsIgnoreCase("color")){
                    if(user.hasRank(Rank.TRIAL)){

                        ChatUtils.sendAll("&bRecoloring all teams...");
                        //colors.clear();
                        ScoreboardHandler scoreboardManager = UHC.getScoreboardManager();
                        Scoreboard scoreboard;
                        TeamManager.getInstance().colorAllTeams();

                        //Color solos
                        Player player;
                        String name;
                        Predicate<UHCPlayer> IS_SPEC = UHCPlayer::isSpectator;

                        for (UHCPlayer uhcPlayer : Iterables.filter(UHCPlayerColl.get().getAllOnline(), Predicates.not(IS_SPEC))) {


                        }

                        TeamManager.getInstance().updateSpectatorTeam();
                        Bukkit.getServer().getPluginManager().callEvent(new TeamColorEvent());
                        p.sendMessage(ChatUtils.message("&eColored all teams and solos!"));

                    }else{
                        p.sendMessage(ChatUtils.message("&cYou require the HOST rank to do this command!"));
                    }
                }else if(args[0].equalsIgnoreCase("random")){

                    if(!user.hasRank(Rank.TRIAL)){
                        p.sendMessage(ChatUtils.message("&cYou require the TRIAL rank to do this command!"));
                        return;
                    }

                    TeamManager.getInstance().setRandomTeams(true);

                    p.sendMessage(ChatUtils.message("&cGenerating random teams of ..." + TeamManager.getInstance().getTeamSize()));

                    List<Player> valid = new ArrayList<>();
                    UHCPlayer gamePlayer;
                    for (Player player : Bukkit.getOnlinePlayers()){
                       gamePlayer = UHCPlayer.get(player.getUniqueId());
                       if(gamePlayer == null) continue;
                       if(!gamePlayer.isSpectator()) valid.add(player);
                    }
                    Collections.shuffle(valid);

                    List<List<Player>> teams = splitList(valid, TeamManager.getInstance().getTeamSize());


                    ArrayList<String> templist;
                    for (List<Player> list : teams){
                        templist = new ArrayList<>();
                        for (Player player : list){
                            templist.add(player.getName());
                        }


                        Team team = new Team("UHC" + (TeamManager.getInstance().getTeams().size()+1), templist, ChatUtils.generateTeamColor());
                        TeamManager.getInstance().addTeam(team);
                    }

                    p.sendMessage(ChatUtils.message("&eFinished creating &6" + TeamManager.getInstance().getTeams().size() + " &eteams of &6" + TeamManager.getInstance().getTeamSize()));
                }
            }else if(args.length == 2){
                if (args[0].equalsIgnoreCase("delete")) {
                    if(!user.hasRank(Rank.TRIAL)){
                        p.sendMessage(ChatUtils.message("&cYou require the TRIAL rank to do this command!"));
                        return;
                    }
                    final Player target = Bukkit.getPlayerExact(args[1]);
                    Team t = TeamManager.getInstance().getTeam(target);
                    TeamManager.getInstance().removeTeam(t);
                    p.sendMessage(ChatUtils.message("&cDeleted " + target.getName() + "&c's team!"));

                }
                 else if(args[0].equalsIgnoreCase("invite")){
                    //Check if team management is on or off
                    //Check if their team size is less than the limit
                    if(TeamManager.getInstance().isTeamManagement()){
                        if(TeamManager.getInstance().getTeam(p) == null){
                            p.performCommand("team create");
                            p.sendMessage(ChatUtils.message("&eNo team found, creating one for you."));

                        }

                        Team team = TeamManager.getInstance().getTeam(p);
                        if(!team.getMembers().get(0).equalsIgnoreCase(p.getName())){
                            p.sendMessage(ChatUtils.message("&cOnly team leaders can do this command!"));
                            return;
                        }

                        final Player target = Bukkit.getPlayerExact(args[1]);
                        if(target == null){
                            p.sendMessage(ChatUtils.message("&cCouldn't find that player!"));
                            return;
                        }else{
                            if(TeamManager.getInstance().getTeam(p).getMembers().size() == TeamManager.getInstance().getTeamSize()){
                                p.sendMessage(ChatUtils.message("&cYour team is already full!"));
                                return;
                            }else{
                                invites.put(target.getName(), TeamManager.getInstance().getTeam(p).getName());
                                p.sendMessage(ChatUtils.message("&eYou have invited &6" + target.getName() + " &eto your team!"));
                                FancyMessage fancyMessage = new FancyMessage(ChatUtils.message("&eYou have been invited to &6" + p.getName() + "'s &eteam. Do "))
                                        .then("/team join " + p.getName() + " to join!").color(ChatColor.YELLOW).command("/team join " + p.getName());
                                fancyMessage.send(target);
                                new BukkitRunnable(){

                                    @Override
                                    public void run() {
                                        if(invites.containsKey(target.getName())){
                                            invites.remove(target.getName());
                                            target.sendMessage(ChatUtils.message("&cYour team invite from &6" + p.getName() + " &chas expired!"));

                                        }
                                    }
                                }.runTaskLater(UHC.get(), 20*60);
                                return;
                            }
                        }
                    }else{
                        p.sendMessage(ChatUtils.message("&cTeam management is currently disabled!"));
                        return;
                    }
                }else if(args[0].equalsIgnoreCase("join")){
                    //Check if team man is on
                    if(TeamManager.getInstance().isTeamManagement()){
                        if(TeamManager.getInstance().getTeam(p) != null){
                            p.sendMessage(ChatUtils.message("&cYou are already on a team! Do /team leave to leave!"));
                            return;
                        }

                        Player target = Bukkit.getPlayerExact(args[1]);
                        if(target == null){
                            p.sendMessage(ChatUtils.message("&cCouldn't find that player!"));
                            return;
                        }

                        if(TeamManager.getInstance().getTeam(target) == null){
                            p.sendMessage(ChatUtils.message("&cThat player doesn't have a team!"));
                            return;
                        }

                        if(TeamManager.getInstance().getTeam(target).getMembers().size() == TeamManager.getInstance().getTeamSize()){
                            p.sendMessage(ChatUtils.message("&cThat team is already full!"));
                            return;
                        }

                        if(invites.containsKey(p.getName())){
                            if(invites.get(p.getName()).equalsIgnoreCase(TeamManager.getInstance().getTeam(target).getName())){
                                TeamManager.getInstance().getTeam(target).addMember(p);
                                Team team = TeamManager.getInstance().getTeam(target);
                                for (String str : team.getMembers()){
                                    Bukkit.getPlayer(str).sendMessage(ChatUtils.message("&6" + p.getName() + " &ehas joined the team!"));
                                }
                                invites.remove(p.getName());
                                return;
                            }else{
                                p.sendMessage(ChatUtils.message("&cThat team hasn't invited you!"));
                                return;
                            }
                        }else{
                            p.sendMessage(ChatUtils.message("&cYou haven't been invited to a team!"));
                            return;
                        }
                    }else{p.sendMessage(ChatUtils.message("&cTeam management is currently disabled!"));
                        return;
                    }

                }else if(args[0].equalsIgnoreCase("man")){
                    if(user.hasRank(Rank.TRIAL)){
                        if(args[1].equalsIgnoreCase("on")){
                            if(TeamManager.getInstance().isTeamManagement()){
                                p.sendMessage(ChatUtils.message("&cTeam Management is already enabled!"));
                                return;
                            }
                            TeamManager.getInstance().setTeamManagement(true);
                            p.sendMessage(ChatUtils.message("&eSuccessfully enabled team management!"));
                            return;
                        }else if(args[1].equalsIgnoreCase("off")){
                            if(!TeamManager.getInstance().isTeamManagement()){
                                p.sendMessage(ChatUtils.message("&cTeam management is already disabled!"));
                                return;
                            }
                            TeamManager.getInstance().setTeamManagement(false);
                            p.sendMessage(ChatUtils.message("&eSuccessfully disabled team management!"));
                            return;
                        }else{
                            sendAdminHelp(p);
                            return;
                        }
                    }else{
                        sendHelp(p);
                        return;
                    }
                }else if(args[0].equalsIgnoreCase("limit")){
                    if(user.hasRank(Rank.TRIAL)){
                        if(MathUtils.isInt(args[1])){
                            TeamManager.getInstance().setTeamSize(Integer.parseInt(args[1]));
                            p.sendMessage(ChatUtils.message("&eSet the team size to " + args[1]));
                            return;
                        }else{
                            p.sendMessage(ChatUtils.message("&cThat is not a number!"));
                            return;
                        }
                    }else{
                        sendHelp(p);
                        return;
                    }
                }else if(args[0].equalsIgnoreCase("ff")){

                    if(!user.hasRank(Rank.TRIAL)){
                        p.sendMessage(ChatUtils.message("&cYou require the TRIAL rank to do this command!"));
                        return;
                    }else{
                        if(args[1].equalsIgnoreCase("on")){
                            TeamManager.getInstance().setTeamFriendlyFire(true);
                            p.sendMessage(ChatUtils.message("&eTeam friendly fire has been enabled!"));
                            return;
                        }else if(args[1].equalsIgnoreCase("off")){
                            TeamManager.getInstance().setTeamFriendlyFire(false);
                            p.sendMessage(ChatUtils.message("&eTeam friendly fire has been disabled!"));
                            return;
                        }else{
                            sendAdminHelp(p);
                            System.out.println("Sending help line 248");
                            return;
                        }
                    }
                }else if(args[0].equalsIgnoreCase("kick")){
                    if(TeamManager.getInstance().isTeamManagement()){
                        if(TeamManager.getInstance().getTeam(p) == null){
                            p.sendMessage(ChatUtils.message("&cYou are not on a team!"));
                            return;
                        }

                        Player target = Bukkit.getPlayer(args[1]);
                        if(target == null){
                            p.sendMessage(ChatUtils.message("&cCouldn't find that player!"));
                            return;
                        }

                        if(target.getName().equalsIgnoreCase(p.getName())){
                            p.sendMessage(ChatUtils.message("&cYou can't kick yourself from the team!"));
                            return;
                        }

                        if(TeamManager.getInstance().getTeam(p).getLeader().equalsIgnoreCase(p.getName())){
                            if(TeamManager.getInstance().getTeam(target) == TeamManager.getInstance().getTeam(p)){
                                TeamManager.getInstance().getTeam(p).removeMember(target);
                                p.sendMessage(ChatUtils.message("&eYou have kicked &6" + target.getName() + " &efrom the team!"));
                                for (String string : TeamManager.getInstance().getTeam(p).getMembers()){
                                    Bukkit.getPlayer(string).sendMessage(ChatUtils.message("&6" + p.getName() + " &ehas kicked &6" + target.getName() + " &efrom the team!"));
                                }
                                target.sendMessage(ChatUtils.message("&eYou have been kicked from the team!"));
                                return;
                            }else{
                                p.sendMessage(ChatUtils.message("&cThat player is not on your team!"));
                                return;
                            }
                        }else{
                            p.sendMessage(ChatUtils.message("&cYou are not the leader of the team!9"));
                            return;
                        }
                    }else{
                        p.sendMessage(ChatUtils.message("&cTeam management is currently disabled!"));
                        return;
                    }
                } else if (args[0].equalsIgnoreCase("list")) {

                    Scenario scenario = scenarioManager.getScen("Secret Teams");
                    if (scenario != null && scenario.isEnabled()) {
                        p.sendMessage(ChatUtils.format(scenario.getPrefix() + "&cYou can't view other player's teams in Secret Teams!"));
                        return;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        Team targetTeam = TeamManager.getInstance().getTeambyPlayerOnTeam(args[1]);
                        if (targetTeam == null) {
                            p.sendMessage(ChatUtils.message("&cThat player is not on a team!"));
                            return;
                        }


                        StringBuilder stringBuilder = new StringBuilder();
                        for (String str : targetTeam.getMembers()) {
                            stringBuilder.append("&e").append(str).append("&7, ");
                        }

                        String f = stringBuilder.toString().trim();
                        p.sendMessage(ChatUtils.format("&b&m---------------------------------"));
                        p.sendMessage(ChatUtils.format("&a" + args[1] + "'s Team&8: " + f.substring(0, f.length() - 1)));
                        p.sendMessage(ChatUtils.format("&b&m---------------------------------"));

                    } else {
                        Team targetTeam = TeamManager.getInstance().getTeam(target);
                        if (targetTeam == null) {
                            p.sendMessage(ChatUtils.message("&cThat player is not on a team!"));
                            return;
                        }
                        StringBuilder stringBuilder = new StringBuilder();
                        for (String str : targetTeam.getMembers()) {
                            stringBuilder.append("&e").append(str).append("&7, ");
                        }

                        String f = stringBuilder.toString().trim();
                        p.sendMessage(ChatUtils.format("&b&m---------------------------------"));
                        p.sendMessage(ChatUtils.format("&a" + args[1] + "'s Team&8: " + f.substring(0, f.length() - 1)));
                        p.sendMessage(ChatUtils.format("&b&m---------------------------------"));

                    }
                } else if (args[0].equalsIgnoreCase("remove")) {

                    if (!user.hasRank(Rank.TRIAL)) {
                        p.sendMessage(ChatUtils.message("&cYou require the " + Rank.TRIAL.getPrefix() + " &crank to use this command!"));
                        return;
                    }

                    String target = args[1];
                    Team team = TeamManager.getInstance().getTeambyPlayerOnTeam(target);
                    if (team == null) {
                        p.sendMessage(ChatUtils.message("&cThat player doesn't have a team!"));
                        return;
                    }

                    for (Team t : TeamManager.getInstance().getTeams()){
                        t.scheduleRemoval(target);
                    }

                    team.scheduleRemoval(target);
                    p.sendMessage(ChatUtils.message("&b" + target + " &ewill be removed from all of their teams!"));

                }
            }else if(args.length == 3){
                if(args[0].equalsIgnoreCase("set")){
                    // ./team set <player to add> <player to add to>
                    if(!user.hasRank(Rank.TRIAL)){
                        p.sendMessage(ChatUtils.message("&cYou require the TRIAL rank to do this command!"));
                        return;
                    }

                    Player target = Bukkit.getPlayer(args[1]);
                    if(target == null){
                        p.sendMessage(ChatUtils.message("&cThat player couldn't be found!"));
                        return;
                    }

                    for (Team t : TeamManager.getInstance().getTeams()) {
                        t.scheduleRemoval(target.getName());
                    }


                    Team targetTeam = TeamManager.getInstance().getTeambyPlayerOnTeam(args[2]);
                    if(targetTeam == null){
                        p.sendMessage(ChatUtils.message("&cThe specified team couldn't be found! Make sure you spelled the target team right!"));
                        return;
                    }

                    targetTeam.addMember(target);
                    p.sendMessage(ChatUtils.message("&eYou have added &a" + target.getName() + " &eto &a" + args[2] + "'s &eteam!"));
                    target.sendMessage(ChatUtils.message("&eYou were added to &a" + args[2] + "'s &eteam!"));
                    targetTeam.message("&a" + target.getName() + " &ehas joined your team!");
                }
            }
        }else{
            return;
        }
    }

    @Override
    public boolean playerOnly() {
        return true;
    }

    @Override
    public Rank getRequiredRank() {
        return null;
    }

    @Override
    public boolean hasRequiredRank() {
        return false;
    }


    private static <T> List<List<T>> splitList(List<T> list, int size) {
        Iterator<T> iterator = list.iterator();
        List<List<T>> returnList = new ArrayList<>();
        while (iterator.hasNext()) {
            List<T> tempList = new ArrayList<>();
            for (int i = 0; i< size; i++) {
                if (!iterator.hasNext()) break;
                tempList.add(iterator.next());
            }
            returnList.add(tempList);
        }
        return returnList;
    }


}
