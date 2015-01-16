package com.minecreatr.sinkingship.util;

import com.minecreatr.sinkingship.Messages;
import com.minecreatr.sinkingship.SinkingShip;
import com.minecreatr.sinkingship.arena.CreationState;
import com.minecreatr.sinkingship.stats.MySqlData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Helps with config stuff
 *
 * @author minecreatr
 */
public class ConfigurationHelper {

    /**
     * The Prefix to be used with the helper methods
     */
    private static String configPrefix = "";


    /**
     * Loads the configuration
     */
    public static void loadConfiguration(){
        SinkingShip plugin = SinkingShip.getInstance();

        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        FileConfiguration c = plugin.getConfig();

        SinkingShip.list = SSCommand.fromConfig("list");
        SinkingShip.join = SSCommand.fromConfig("join");
        SinkingShip.leave = SSCommand.fromConfig("leave");
        SinkingShip.lobby = SSCommand.fromConfig("lobby");
        SinkingShip.spectate = SSCommand.fromConfig("spectate");
        SinkingShip.vote = SSCommand.fromConfig("vote");
        SinkingShip.start = SSCommand.fromConfig("start");
        SinkingShip.stop = SSCommand.fromConfig("stop");
        SinkingShip.create = SSCommand.fromConfig("create");
        SinkingShip.setLobbySpawn = SSCommand.fromConfig("setlobbyspawn");
        SinkingShip.enable = SSCommand.fromConfig("enable");
        SinkingShip.disable = SSCommand.fromConfig("disable");
        SinkingShip.delete = SSCommand.fromConfig("delete");
        SinkingShip.stats = SSCommand.fromConfig("stats");
        SinkingShip.setWaterSpeed = SSCommand.fromConfig("setwaterspeed");
        SinkingShip.setTimeLimit = SSCommand.fromConfig("settimelimit");
        SinkingShip.reload = SSCommand.fromConfig("reload");
        SinkingShip.addwall = SSCommand.fromConfig("addwall");
        SinkingShip.wand = SSCommand.fromConfig("wand");

        Messages.prefix = ChatColor.translateAlternateColorCodes('&', c.getString("prefix", "&7[&bSS&7]&a"));
        Messages.noPerm = c.getString("noPerm", "§4You do not have permission to execute this command");

        CreationState.SELECT_AREA.setMessage(c.getString("selectArea", "Select the area of the arena with the ss wand, then try to create again"));
        CreationState.SET_WATER_START_HEIGHT.setMessage(c.getString("selectWaterStart", "Stand at the height you want the water to start and type /ss create [name] waterStart"));
        CreationState.SET_WATER_END_HEIGHT.setMessage(c.getString("selectWaterEnd", "Stand at the height you want the water to end at and type /ss create [name] waterEnd"));
        CreationState.SET_SPAWN_REGION.setMessage(c.getString("selectSpawnArea", "Select the area that you would like players to spawn in with a wooden axe, then type /ss create [name] spawn"));
        CreationState.DONE.setMessage(c.getString("arenaCreationComplete", "Done!"));


        SinkingShip.mySqlData = new MySqlData(c.getString("dbhost", "localhost"), c.getString("dbport", "3306"),
                c.getString("dbuser", "root"), c.getString("dbpassword", "password123"));
        MySqlData.isEnabled=c.getBoolean("mysql", true);

        SinkingShip.useBossBar = c.getBoolean("bossbar", true);
        SinkingShip.blocksPer = c.getInt("blocksPer", 1);
        SinkingShip.seconds = c.getInt("seconds", 4);
        SinkingShip.timeLimit = c.getInt("timelimit", 0);
        SinkingShip.minPlayers = c.getInt("minPlayers", 5);
        SinkingShip.minNoVote = c.getInt("startNoVote", 8);
        SinkingShip.maxPlayers = c.getInt("maxPlayers", 10);

        int lobbyX = c.getInt("lobby.x", 0);
        int lobbyY = c.getInt("lobby.y", 0);
        int lobbyZ = c.getInt("lobby.z", 0);
        World world = Bukkit.getWorld(c.getString("lobby.world", "world"));
        SinkingShip.lobbyLoc = new Location(world, lobbyX, lobbyY, lobbyZ);

        int g = c.getInt("lobby.gamemode", 2);
        switch (g){
            case 0: SinkingShip.lobbyGamemode = GameMode.SURVIVAL;
            case 1: SinkingShip.lobbyGamemode = GameMode.CREATIVE;
            case 2: SinkingShip.lobbyGamemode = GameMode.ADVENTURE;
            case 3:{
                if (SinkingShip.is18){
                    SinkingShip.lobbyGamemode = GameMode.SPECTATOR;
                }
                else {
                    SinkingShip.lobbyGamemode = GameMode.ADVENTURE;
                }
            }
            default: SinkingShip.lobbyGamemode = GameMode.ADVENTURE;
        }

        SinkingShip.preventBlockBreak = c.getBoolean("preventBlockBreak", true);
        SinkingShip.preventInteract = c.getBoolean("preventInteract", true);

        SinkingShip.votes = c.getInt("amountOfVotes", 5);
    }

    /**
     * Saves the lobby location
     */
    public static void saveLobbyLoc(){
        SinkingShip.getInstance().reloadConfig();
        FileConfiguration c = SinkingShip.getInstance().getConfig();
        c.set("lobby.x", SinkingShip.lobbyLoc.getBlockX());
        c.set("lobby.y", SinkingShip.lobbyLoc.getBlockY());
        c.set("lobby.z", SinkingShip.lobbyLoc.getBlockZ());
        c.set("lobby.world", SinkingShip.lobbyLoc.getWorld().getName());
        SinkingShip.getInstance().saveConfig();
    }

    /**
     * Sets the configuration prefix;
     * @param prefix The Configuration prefix
     */
    public static void setPrefix(String prefix){
        configPrefix=prefix;
    }

    /**
     * Gets the configuration prefix
     * @return The Configuration prefix
     */
    public static String getPrefix(){
        return configPrefix;
    }

    /**
     * Clears the configuration prefix
     */
    public static void clearPrefix(){
        setPrefix("");
    }

    /**
     * Get String from config
     * @param c The Config
     * @param key The Key
     * @param def The Default value
     * @return The Value
     */
    public static String getString(FileConfiguration c, String key, String def){
        return (c.getString(configPrefix+key, def));
    }

    /**
     * Get int from config
     * @param c The Config
     * @param key The Key
     * @param def The Default
     * @return The Value
     */
    public static int getInt(FileConfiguration c, String key, int def){
        return (c.getInt(configPrefix+key, def));
    }


}
