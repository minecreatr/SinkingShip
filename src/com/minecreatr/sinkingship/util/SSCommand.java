package com.minecreatr.sinkingship.util;

import com.minecreatr.sinkingship.Messages;
import com.minecreatr.sinkingship.SinkingShip;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Object Representation of a sinking ship command. This is just to make it easier to load the commands from the config
 *
 * @author minecreatr
 */
public class SSCommand {

    /**
     * The commands that are considered "admin commands" and have the perm sinkingship.admin instead of just sinkingship
     */
    public static final List<String> adminCommands = new ArrayList<String>(Arrays.asList("start", "stop", "create", "setlobbyspawn", "enable", "disable",
            "delete", "setwaterspeed", "settimelimit", "reload", "addwall"));

    /**
     * Command Name
     */
    private String name;

    /**
     * Permission for this command
     */
    private String permission;

    /**
     * The Help message
     */
    private String help;

    /**
     * Whether the command is enabled
     */
    private boolean enabled;

    public SSCommand(String name, String perm, String help, boolean enabled){
        this.name=name;
        this.permission=perm;
        this.help=help;
        this.enabled=enabled;
    }

    public String getName(){
        return this.name;
    }

    public String getPerm(){
        return this.permission;
    }

    public String getHelp(){
        return this.help;
    }

    public boolean isEnabled(){
        return enabled;
    }

    /**
     * If the player has permission to call this command
     * @param player The Player
     * @return If player has permission to call
     */
    public boolean canCall(Player player){
        return player.hasPermission(permission)||player.isOp();
    }

    /**
     * Creates an SSCommand object from the command in the config with the specified name
     * @param name The Command Name
     * @return The SSCommand object
     */
    public static SSCommand fromConfig(String name){
        FileConfiguration c = SinkingShip.getInstance().getConfig();
        String base = "commands."+name+".";
        String perm;
        if (adminCommands.contains(name)) {
            perm = c.getString("permission", "sinkingship.admin."+name);
        }
        else {
            perm = c.getString("permission", "sinkingship."+name);
        }
        return new SSCommand(name, perm, c.getString(base+"help", "/"+name), c.getBoolean("enabled", true));
    }

    /**
     * Whether the permissible has permission to use this command
     * @param sender The Permissible
     * @return Whether it has permission
     */
    public boolean hasPermission(Permissible sender){
        return sender.hasPermission(this.getPerm());
    }

    /**
     * Sends the command help to the command sender
     * @param sender The Command Sender
     */
    public boolean sendHelp(CommandSender sender){
        Messages.sendMessage(sender, this.getHelp());
        return true;
    }

    /**
     * Whether that argument is calling the command
     * @param arg The Argument
     * @return Whether it is calling the command
     */
    public boolean isCalling(String arg){
        return (arg.equalsIgnoreCase(this.getName())&&isEnabled());
    }


}
