package com.minecreatr.sinkingship.arena;

import com.minecreatr.sinkingship.SinkingShip;
import com.minecreatr.sinkingship.util.ArenaSign;
import com.minecreatr.sinkingship.util.GameInstance;
import com.minecreatr.sinkingship.util.MiscUtils;
import com.minecreatr.sinkingship.util.NumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Object Representing an arena
 * THERE SHOULD ONLY BE ONE INSTANCE OF ANY ARENA
 *
 * @author minecreatr
 */
public class Arena{


    public static final List<Arena> arenas = new ArrayList<Arena>();

    //Name of Arena
    public final String name;

    public int x1;
    public int y1;
    public int z1;

    public int x2;
    public int y2;
    public int z2;

    public int spawnX1;
    public int spawnY1;
    public int spawnZ1;

    public int spawnX2;
    public int spawnY2;
    public int spawnZ2;

    public int waterStart;
    public int waterEnd;

    public int blocksPer;
    public int seconds;
    public int timeLimit;

    public int minimumPlayers;
    public int minNoVote;
    public int maxPlayers;

    public boolean enabled;

    public World world;

    public List<ArenaSign> signs;


    /**
     * The Game that is going on currently
     */
    public GameInstance game;

    public void edit(String f, Object value){

    }

    /**
     * Arena Constructor. DO NOT CALL THIS MANUALLY!!! MUST BE CALLED THROUGH THE createArena METHOD
     * @param name the arena name
     * @param x1 first x coord
     * @param y1 first y coord
     * @param z1 first z coord
     * @param x2 second x coord
     * @param y2 second y coord
     * @param z2 second z coord
     * @param start water starting level
     * @param end water ending level
     * @return The Arena that is created or gotten
     */
    private Arena(String name, int x1, int y1, int z1, int x2, int y2, int z2, int start, int end, int blocksPer, int seconds, int timeLimit, int minimumPlayers
    , int minNoVote, int maxPlayers, int spawnX1, int spawnY1, int spawnZ1, int spawnX2, int spawnY2, int spawnZ2, World w, List<ArenaSign> signs){
        if (!MiscUtils.getCaller().startsWith("com.minecreatr.sinkingship.arena.Arena.createArena")){
            throw new RuntimeException("Arena can only be instantiated via the createArena method!");
        }
        this.name=name;
        this.x1=x1;
        this.y1=y1;
        this.z1=z1;
        this.x2=x2;
        this.y2=y2;
        this.z2=z2;
        this.waterStart=start;
        this.waterEnd=end;
        this.blocksPer=blocksPer;
        this.seconds=seconds;
        this.timeLimit=timeLimit;
        this.minimumPlayers=minimumPlayers;
        this.minNoVote=minNoVote;
        this.maxPlayers=maxPlayers;
        this.spawnX1=spawnX1;
        this.spawnY1=spawnY1;
        this.spawnZ1=spawnZ1;
        this.spawnX2=spawnX2;
        this.spawnY2=spawnY2;
        this.spawnZ2=spawnZ2;
        this.world=w;
        this.signs=signs;
        this.game = new GameInstance(this);
        enabled=true;
        arenas.add(this);
    }

    /**
     * Creates a new Arena
     * @param name the arena name
     * @param x1 first x coord
     * @param y1 first y coord
     * @param z1 first z coord
     * @param x2 second x coord
     * @param y2 second y coord
     * @param z2 second z coord
     * @param start water starting level
     * @param end water ending level
     * @return The Arena that is created or gotten
     */
    public static Arena createArena(String name, int x1, int y1, int z1, int x2, int y2, int z2, int start, int end, int blocksPer, int seconds, int timeLimit,
                                    int minimumPlayers, int minNoVote, int maxPlayers, int spawnX1, int spawnY1, int spawnZ1, int spawnX2, int spawnY2, int spawnZ2, World w, List<ArenaSign> signs){
        if (existsIgnoreDisabled(name)){
            return getArena(name);
        }
        else {
            return new Arena(name, x1, y1, z1, x2, y2, z2, start, end, blocksPer, seconds, timeLimit, minimumPlayers, minNoVote, maxPlayers,
                    spawnX1, spawnY1, spawnZ1, spawnX2, spawnY2, spawnZ2, w, signs);
        }
    }

    /**
     * If this Arena has a time limit
     * @return If the Arena has a time limit
     */
    public boolean hasTimeLimit(){
        return this.timeLimit!=0;
    }

    /**
     * Gets the Aren'as name
     * @return The Name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Loads the arena from a file
     * @param file the file
     * @return the arena
     */
    public static Arena fromFile(File file){
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!config.contains("isArena")){
            return null;
        }
        return createArena(config.getString("name", "Error"), config.getInt("x1", 0), config.getInt("y1", 0),
                config.getInt("z1", 0), config.getInt("x2", 0), config.getInt("y2", 0), config.getInt("z2", 0),
                config.getInt("waterStart", 0), config.getInt("waterEnd", 0), config.getInt("blocksPer", SinkingShip.blocksPer),
                config.getInt("seconds", SinkingShip.seconds), config.getInt("timeLimit", SinkingShip.timeLimit),
                config.getInt("minimumPlayers", SinkingShip.minPlayers), config.getInt("minNoVote", SinkingShip.minNoVote),
                        config.getInt("maxPlayers", SinkingShip.maxPlayers), config.getInt("spawnX1", 0),config.getInt("spawnY1", 0),
                config.getInt("spawnZ1", 0), config.getInt("spawnX2", 0), config.getInt("spawnY2", 0), config.getInt("spawnZ2", 0),
                Bukkit.getWorld(config.getString("world", "world")), getSigns(config));
    }

    /**
     * Gets all the signs from the configuration file
     * @param c The Configuration File
     * @return List of the sign locations
     */
    public static List<ArenaSign> getSigns(FileConfiguration c){
        List<ArenaSign> signs = new ArrayList<ArenaSign>();
        for (String key : c.getKeys(true)){
            if (key.startsWith("signs.")){
                UUID id = UUID.fromString(key.split(".")[1]);
                signs.add(ArenaSign.fromConfig(c, id));
            }
        }
        return signs;
    }

    /**
     * Saves the signs to the config
     * @param c The config
     */
    public void writeSigns(FileConfiguration c){
        for (ArenaSign sign : signs){
            sign.saveTo(c);
        }
    }

    /**
     * Updates all the signs
     */
    public void updateSigns(){
        for (ArenaSign sign : signs){
            if (!sign.update(this)){
                signs.remove(sign);
            }
        }
    }


    /**
     * Saves the arena to the specified file
     * @param file the file
     */
    public void saveTo(File file){
        FileConfiguration config = new YamlConfiguration();
        config.set("name", name);
        config.set("x1", x1);
        config.set("y1", y1);
        config.set("z1", z1);
        config.set("x2", x2);
        config.set("y2", y2);
        config.set("z2", z2);
        config.set("waterStart", waterStart);
        config.set("waterEnd", waterEnd);
        config.set("blocksPer", blocksPer);
        config.set("seconds", seconds);
        config.set("timeLimit", timeLimit);
        config.set("minimumPlayers", minimumPlayers);
        config.set("minNoVote", minNoVote);
        config.set("maxPlayers", maxPlayers);
        config.set("spawnX1", spawnX1);
        config.set("spawnY1", spawnY1);
        config.set("spawnZ1", spawnZ1);
        config.set("spawnX2", spawnX2);
        config.set("spawnY2", spawnY2);
        config.set("spawnZ2", spawnZ2);
        config.set("world", world.getName());
        config.set("isArena", true);
        writeSigns(config);
        try {
            config.save(file);
        } catch (IOException exception){
            SinkingShip.getInstance().getLogger().severe("Error saving Arena "+name+" to file "+file.getName());
        }
    }

    /**
     * Saves the arena to its defualt file
     */
    public void saveTo(){
        saveTo(new File(SinkingShip.arenaDirectory+this.getName()+".yml"));
    }

    /**
     * If arena with name exists and is enabled
     * @param name the name
     * @return if it exists
     */
    public static boolean exists(String name){
        for (Arena arena : arenas){
            if (arena.getName().equalsIgnoreCase(name)){
                return arena.isEnabled();
            }
        }
        return false;
    }

    /**
     * If the arena exists
     * @param name The Arena name
     * @return if it exists
     */
    public static boolean existsIgnoreDisabled(String name){
        for (Arena arena : arenas){
            if (arena.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the arena with the specified name
     * @param name the name
     * @return the arena
     */
    public static Arena getArena(String name){
        for (Arena arena : arenas){
            if (arena.getName().equalsIgnoreCase(name)){
                return arena;
            }
        }
        return null;
    }

    /**
     * Loads the arena's from the flat file directory
     * @param file The Directory file
     */
    public static void loadArenas(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File cur : files){
                if (cur.getName().endsWith(".yml")){
                    if (fromFile(cur)==null){
                        SinkingShip.getInstance().getLogger().severe("Error Reading Arena from file "+cur.getName());
                    }
                    else {
                        SinkingShip.getInstance().getLogger().info("Creating arena from file "+cur.getName());
                    }
                }
            }
        }
    }

    public boolean equals(Object obj){
        if (obj instanceof Arena){
            return ((Arena)obj).getName().equals(this.getName());
        }
        else {
            return false;
        }
    }

    public String toString(){
        StringBuffer b = new StringBuffer("[");
        for (Field f : this.getClass().getDeclaredFields()){
            if (f.getName().equalsIgnoreCase("arenas")){
                continue;
            }
            f.setAccessible(true);
            try {
                b.append("{"+f.getName() + ":" + f.get(this)+"}");
            } catch (IllegalAccessException exc){

            }
        }
        return b.append("]").toString();
    }

    /**
     * Whether or not the arena is enabled
     * @return Whether the arena is enabled
     */
    public boolean isEnabled(){
        return this.enabled;
    }

    /**
     * Sets whether the arena is enabled or disabled
     * @param en
     */
    public void setEnabled(boolean en){
        this.enabled=en;
    }

    /**
     * Gets a random start position
     * @return The Start position
     */
    public Location getRandomStartLocation(){
        int x = NumUtils.randomInRange(spawnX1, spawnX2);
        int y = NumUtils.randomInRange(spawnY1, spawnY2);
//        System.out.println(spawnY1+" "+spawnY2+" "+y);
        int z = NumUtils.randomInRange(spawnZ1, spawnZ2);
        return new Location(world, x, y, z);
    }

    /**
     * Gets the water starting point
     * @return The water starting point
     */
    public int getWaterStart(){
        return this.waterStart;
    }

    /**
     * Gets the water ending point
     * @return The Water Ending point
     */
    public int getWaterEnd(){
        return this.waterEnd;
    }

    /**
     * Get blocks per...
     * @return BlocksPer
     */
    public int getBlocksPer(){
        return this.blocksPer;
    }

    /**
     * ...seconds
     * @return Seconds
     */
    public int getSeconds(){
        return this.seconds;
    }

    /**
     * Deletes the arena
     */
    public boolean delete(){
        arenas.remove(this);
        File file = new File(SinkingShip.arenaDirectory+this.getName()+".yml");
        if (file.exists()){
            return file.delete();
        }
        return false;
    }

    /**
     * Return whether the location is a sign in this arena
     * @param loc The Location
     * @return Whether it is a sign to this arena
     */
    public boolean isSign(Location loc){
        for (ArenaSign sign : signs){
            if (sign.getLocation().equals(loc)){
                return true;
            }
        }
        return false;
    }

}
