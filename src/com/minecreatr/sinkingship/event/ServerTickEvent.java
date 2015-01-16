package com.minecreatr.sinkingship.event;

import com.minecreatr.sinkingship.SinkingShip;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Event that is fired every server tick
 *
 * @author minecreatr
 */
public class ServerTickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers(){
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    /**
     * Set up the ticking mechanism
     * @param p The Plugin
     */
    public static void setup(SinkingShip p){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(p, new Runnable() {
            @Override
            public void run() {
                ServerTickEvent event = new ServerTickEvent();
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }, 1, 1);
    }

    /**
     * Seconds to scheduler ticks
     * @param seconds The Amount of seconds
     * @return How many scheduler ticks
     */
    public static int toSchedulerTicks(int seconds){
        return seconds*20;
    }
}
