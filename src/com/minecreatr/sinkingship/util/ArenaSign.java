package com.minecreatr.sinkingship.util;

import com.minecreatr.sinkingship.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

/**
 * Represents an arena sign, mostly used to organize data
 *
 * @author minecreatr
 */
public class ArenaSign {

    /**
     * The location of the sign
     */
    private Location location;

    /**
     * The Id of the sign
     */
    private UUID id;

    /**
     * Creates an arena sign with the specified location and id
     * @param loc The Location
     * @param id The ID
     */
    public ArenaSign(Location loc, UUID id){
        this.location=loc;
        this.id=id;
    }

    /**
     * Creates an arena sign with the specified location and a random id
     * @param loc The Location
     */
    public ArenaSign(Location loc){
        this(loc, UUID.randomUUID());
    }

    /**
     * Gets the id
     * @return The Id
     */
    public UUID getId(){
        return this.id;
    }

    /**
     * Gets the location
     * @return The Location
     */
    public Location getLocation(){
        return this.location;
    }

    /**
     * Sets the location
     * @param loc The Location
     */
    public void setLocation(Location loc){
        this.location=loc;
    }

    /**
     * Saves to the file configuration
     * @param c The File Configuration
     */
    public void saveTo(FileConfiguration c){
        String prefix = "signs."+getId()+".";
        c.set(prefix+"x", location.getBlockX());
        c.set(prefix+"y", location.getBlockY());
        c.set(prefix+"z", location.getBlockZ());
        c.set(prefix+"world", location.getWorld().getName());
    }

    /**
     * Reads from the file configuration
     * @param c The File Configuration
     * @param id The Id
     * @return The ArenaSign
     */
    public static ArenaSign fromConfig(FileConfiguration c, UUID id){
        String prefix = "signs."+id+".";
        return new ArenaSign(new Location(Bukkit.getWorld(c.getString(prefix + "world", "world")),c.getInt(prefix + "x", 0),
                c.getInt(prefix + "y", 0), c.getInt(prefix + "z", 0)), id);
    }

    /**
     * Updates the signs status
     * @param arena The Arena
     * @return Whether the block is a sign
     */
    public boolean update(Arena arena){
        Block block = location.getBlock();
        if (block.getState() instanceof Sign){
            Sign sign = (Sign)block.getState();
            String status;
            if(!arena.isEnabled()){
                status=ChatColor.RED+"DISABLED";
            }
            else if (arena.game.isInProgress()){
                status=ChatColor.GREEN+"IN PROGRESS";
            }
            else {
                status = ChatColor.LIGHT_PURPLE+"WAITING";
            }
            sign.setLine(2, status);
            sign.setLine(3, arena.game.getPlayers().size()+"/"+arena.maxPlayers);
            return true;
        }
        else {
            return false;
        }
    }
}
