package com.minecreatr.sinkingship;

import com.minecreatr.sinkingship.arena.Arena;
import com.minecreatr.sinkingship.arena.UnfinalizedArena;
import com.minecreatr.sinkingship.event.ServerTickEvent;
import com.minecreatr.sinkingship.stats.MySqlData;
import com.minecreatr.sinkingship.util.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Main plugin file
 *
 * @author minecreatr
 */
public class SinkingShip extends JavaPlugin implements Listener{


    public static final String sSignMarker = ChatColor.GREEN+"[SS]";
    /**
     * The Instance of the plugin
     */
    private static SinkingShip instance;
    /**
     * Whether the server is using 1.8
     */
    public static boolean is18;
    /**
     * The MySQL data
     */
    public static MySqlData mySqlData;
    /**
     * The Directory for the arena files
     */
    public static String arenaDirectory;
    /**
     * Whether to use the bossbar to tell the water level
     */
    public static boolean useBossBar;
    /**
     * The Default Blocks per...
     */
    public static int blocksPer;
    /**
     * ... seconds
     */
    public static int seconds;
    /**
     * Default time limit, if 0 then there is no time limit
     */
    public static int timeLimit;
    /**
     * Minimum players to be able to start the game
     */
    public static int minPlayers;
    /**
     * Minimum players at which the game will start without anyone voting
     */
    public static int minNoVote;
    /**
     * Maximum amount of players that can be a game
     */
    public static int maxPlayers;
    /**
     * All of the current GameInstances
     */
    public static ArrayList<GameInstance> games = new ArrayList<GameInstance>();

    /**
     * Location of the lobby
     */
    public static Location lobbyLoc;

    /**
     * The Gamemode for the lobby
     */
    public static GameMode lobbyGamemode;

    /**
     * Whether to prevent spectators and players from breaking blocks
     */
    public static boolean preventBlockBreak;

    /**
     * Whether to prevent spectators and players from interacting
     */
    public static boolean preventInteract;

    /**
     * The Amount of votes required to start early
     */
    public static int votes;


    /**
     * Stores Players progress in the Creation Wizard
     */
    public static HashMap<UUID, UnfinalizedArena> creations = new HashMap<UUID, UnfinalizedArena>();
    /**
     * All the commands as SSCommand objects
     */
    public static SSCommand list; //list commands
    public static SSCommand join; //Join Command
    public static SSCommand leave; //Leave Command
    public static SSCommand lobby; //Lobby Command
    public static SSCommand spectate; //Spectate command
    public static SSCommand vote; //Vote command
    public static SSCommand start; //Start Command
    public static SSCommand stop; //Stop command
    public static SSCommand create; //Create Command
    public static SSCommand setLobbySpawn; //Set Lobby Spawn Command
    public static SSCommand enable; //Enable command
    public static SSCommand disable; //Disable command
    public static SSCommand delete; //Delete Command
    public static SSCommand addwall; //Add Wall Command
    public static SSCommand stats; //Stats command
    public static SSCommand setWaterSpeed; //Set Water Speed command
    public static SSCommand setTimeLimit; //Set Time Limit command
    public static SSCommand reload; // Reload command
    public static SSCommand wand; //Wand command



    @Override
    public void onEnable(){
        instance = this;
        this.getServer().getPluginManager().registerEvents(this, this);
        ServerTickEvent.setup(this);
        arenaDirectory = getInstance().getDataFolder()+File.separator+"arenas"+File.separator;
        onStart();
    }

    public void onStart(){
        ConfigurationHelper.loadConfiguration();
        if (Bukkit.getVersion().contains("1.8")){
            is18=true;
        }
        Arena.loadArenas(new File(arenaDirectory));
    }

    /**
     * Gets an instance of the sinking ship plugin
     * @return the instance
     */
    public static SinkingShip getInstance(){
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (cmd.getName().equalsIgnoreCase("ss")){
            return CommandHandler.handle(sender, args);
        }
        else {
            return false;
        }
    }

    /**
     * Sends the plugin help to the specified CommandSender
     * @param sender The Command Sender
     */
    public static void sendHelp(CommandSender sender){
        Messages.sendMessage(sender, "Temporary Help Message");
    }

    /**
     * Whether the specified arena is in a game at the moment
     * @param arena The Arena
     * @return Whether its in a game
     */
    public boolean isArenaInUse(Arena arena){
        for (Arena a : Arena.arenas){
            if (a.game.getTimeUntilStart()==0){
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignChange(SignChangeEvent event){
        if (event.getLine(0).equalsIgnoreCase("[SSsign]")){
            String line2 = event.getLine(1);
            if (line2.isEmpty()){
                event.setLine(1, ChatColor.RED+"NO ARENA");
                return;
            }
            if (!Arena.exists(line2)){
                event.setLine(1, ChatColor.RED+"INVALID ARENA");
                return;
            }
            event.setLine(0, sSignMarker);
            ArenaSign sign = new ArenaSign(event.getBlock().getLocation());
            Arena.getArena(line2).signs.add(sign);
            Messages.sendMessage(event.getPlayer(), ChatColor.GREEN+"Created Sign for arena "+line2+" successfully");
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        if (event.getClickedBlock()!=null) {
            if (event.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) event.getClickedBlock().getState();
                if (sign.getLine(0).equals(sSignMarker)) {
                    if (Arena.exists(sign.getLine(1))) {
                        Arena arena = Arena.getArena(sign.getLine(1));
                        if (arena.isSign(event.getClickedBlock().getLocation())) {
                            if (!arena.game.isInProgress()) {
                                Bukkit.dispatchCommand(event.getPlayer(), "ss join " + sign.getLine(1));
                            } else {
                                Messages.sendMessage(event.getPlayer(), "Cannot join game in progress");
                            }
                        }
                    }
                }
            }
        }
        if (SelectionWand.isWand(event.getPlayer().getItemInHand())){
            Location loc = event.getClickedBlock().getLocation();
            if (event.getAction()== Action.RIGHT_CLICK_BLOCK) {
                SelectionWand.secondLocs.put(event.getPlayer().getUniqueId(), loc);
                Messages.sendMessage(event.getPlayer(), "Set Second loc to X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
                event.setCancelled(true);
            }
            else if (event.getAction()==Action.LEFT_CLICK_BLOCK){
                SelectionWand.firstLocs.put(event.getPlayer().getUniqueId(), loc);
                Messages.sendMessage(event.getPlayer(), "Set First loc to X:" + loc.getBlockX() + " Y:" + loc.getBlockY() + " Z:" + loc.getBlockZ());
                event.setCancelled(true);
            }
        }
    }








}
