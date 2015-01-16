package com.minecreatr.sinkingship;

import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Object with different static messages
 *
 * @author minecreatr
 */
public class Messages {

    public static final String unused = "\u267E";

    /**
     * The Chat Prefix for most plugin messages
     */
    public static String prefix;

    /**
     * The Message that is sent when a player dosn't have permission to use a command
     */
    public static String noPerm;

    /**
     * Send a message to a command sender
     * @param sender The Command Sender
     * @param message The Message
     * @param regex The Regex which to replace
     * @param args The Arguments to replace the regex with
     */
    public static void sendMessage(CommandSender sender, String message, String regex, String... args){
        sender.sendMessage(prefix+substitute(regex, message, args));
    }

    /**
     * Send the no Permission message to the Command Sender
     * @param sender The Command Sender
     */
    public static boolean sendNoPerm(CommandSender sender){
        sendMessage(sender, noPerm);
        return true;
    }

    /**
     * Send a message to the command sender
     * @param sender The Command Sender
     * @param message The Message
     */
    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(prefix+message);
    }

    /**
     * Substitutes the regex in the message with the args respectivily
     * @param message The Message
     * @param regex The Regex
     * @param args The Arguments to replace the regex with
     * @return The finished string
     */
    public static String substitute(String message, String regex, String... args){
        message = message.replace(regex, unused);
        StringBuffer out = new StringBuffer();
        int cur = 0;
        for (String s : message.split("")){
            if (s.equals(unused)){
                out.append(args[cur]);
                cur++;
            }
            else {
                out.append(s);
            }
        }
        return out.toString();
    }

    /**
     * Sends the title and subtitle to the specified player
     * @param player The Player
     * @param title The Title
     * @param subtitle The Subtitle
     */
    public static void sendTitle(Player player, String title, String subtitle){
        if (SinkingShip.is18){
            PacketPlayOutTitle t = new PacketPlayOutTitle(EnumTitleAction.TITLE, new ChatComponentText(title));
            PacketPlayOutTitle s = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle));
            getConnection(player).sendPacket(t);
            getConnection(player).sendPacket(s);
        }
        else {
            sendMessage(player, title);
            sendMessage(player, subtitle);
        }
    }

    /**
     * Sends the title and subtitle for a certain amount of time
     * @param player The Player
     * @param title The Title
     * @param subtitle The Subtitle
     * @param time Amount of time, if 0 then title wont be cleared
     */
    public static void sendTitle(final Player player, String title, String subtitle, int time){
        sendTitle(player, title, subtitle);
        if (time!=0){
            BukkitScheduler s = Bukkit.getScheduler();
            s.scheduleSyncDelayedTask(SinkingShip.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Messages.clearTitle(player);
                }
            }, time*20);
        }
    }

    /**
     * Gets the PlayerConnection 1.8 ONLY
     * @param p
     * @return
     */
    public static PlayerConnection getConnection(Player p){
        return ((CraftPlayer)p).getHandle().playerConnection;
    }

    /**
     * Clears the players current title
     * @param player The Player
     */
    public static void clearTitle(Player player){
        if (SinkingShip.is18) {
            PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.CLEAR, new ChatComponentText(""));
            getConnection(player).sendPacket(title);
        }
    }

}
