package com.minecreatr.sinkingship.util;

import com.minecreatr.sinkingship.Messages;
import com.minecreatr.sinkingship.SinkingShip;
import com.minecreatr.sinkingship.arena.Arena;
import com.minecreatr.sinkingship.event.ServerTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import sun.plugin.util.UIUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Represents an instance of a game of sinking ship
 *
 * @author minecreatr
 */
public class GameInstance implements Listener{

    /**
     * The Players in the game
     */
    private ArrayList<Player> players = new ArrayList<Player>();

    /**
     * The spectating players
     */
    private ArrayList<Player> spectators = new ArrayList<Player>();

    /**
     * UUID's of all the players who have voted
     */
    public ArrayList<UUID> voters = new ArrayList<UUID>();
    /**
     * Time Until Game starts, if 0 than game is started
     */
    private int timeUntilStart;


    /**
     * The Arena the game is in
     */
    private Arena arena;

    /**
     * The Current water level
     */
    private int waterLevel;

    /**
     * The Amount of votes
     */
    public int votes;
    /**
     * The Amount of time the game is taking
     */
    private int totalTime;

    /**
     * Creates a new GameInstance in the specified arena
     */

    private int interval;
    /**
     * The Scoreboard for the game
     */
    private Scoreboard board;

    public GameInstance(Arena arena){
        this.arena=arena;
        this.timeUntilStart=1200;
        Bukkit.getServer().getPluginManager().registerEvents(this, SinkingShip.getInstance());
        board = ScoreboardHelper.manager.getNewScoreboard();
        waterLevel = arena.getWaterStart();
        interval = 0;
        totalTime=0;
        votes = 0;
    }

    public void endGame(){
        for (Player p : players){
            leaveGame(p);
        }
        for (Player p : spectators){
            leaveGame(p);
        }
        waterLevel = arena.getWaterStart();
        clearWater();
        interval = 0;
        totalTime = 0;
        timeUntilStart = 1200;
        voters.clear();
        votes = 0;
    }


    /**
     * Plays sound to all players in game and spectators
     * @param sound The Sound
     * @param volume The Volume
     * @param pitch The Pitch
     */
    public void playSound(Sound sound, float volume, float pitch){
        for (Player p : players){
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
        for (Player p : spectators){
            p.playSound(p.getLocation(), sound, volume, pitch);
        }
    }

    /**
     * Sends all the players and spectators a title, if time is 0 then it wont automatically expire
     * @param title The Title
     * @param subtitle The Subtitle
     * @param time The Time in seconds
     */
    public void sendTitle(String title, String subtitle, int time){
        for (final Player p : players){
            Messages.sendTitle(p, title, subtitle, time);
        }
        for (final Player p : spectators){
            Messages.sendTitle(p, title, subtitle, time);
        }
    }

    /**
     * Join the game
     * @param p The Player
     */
    public boolean joinGame(Player p){
        if (!isInProgress()){
            players.add(p);
            Location loc = arena.getRandomStartLocation();
            p.sendMessage(loc.toString());
            p.teleport(loc);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Whether the game is in progress
     * @return Whether the game is in progress
     */
    public boolean isInProgress(){
        return this.getTimeUntilStart()==0;
    }


    /**
     * Gets the time until the game starts, if 0 then game is in progress
     * @return the time until start
     */
    public int getTimeUntilStart(){
        return timeUntilStart;
    }

    /**
     * Sends a message to all the players in game and spectating
     * @param message The Message
     */
    public void sendMessage(String message){
        for (Player p : players){
            Messages.sendMessage(p, message);
        }
        for (Player p : spectators){
            Messages.sendMessage(p, message);
        }
    }

    /**
     * Get the players in the game
     * @return The Players in the game
     */
    public ArrayList<Player> getPlayers(){
        return this.players;
    }

    /**
     * Get all the spectators
     * @return
     */
    public ArrayList<Player> getSpectators(){
        return this.spectators;
    }


    /**
     * Makes the specified player a spectator
     * @param p The Player
     */
    public void spectate(Player p){
        spectators.add(p);
        if (SinkingShip.is18){
            p.setGameMode(GameMode.SPECTATOR);
        }
        else {
            p.setGameMode(GameMode.CREATIVE);
            hide(p);
        }
        p.teleport(arena.getRandomStartLocation());
    }

    /**
     * Makes a specified player stop spectating
     * @param p The Player
     */
    public void stopSpectating(Player p){
        spectators.remove(p);
        p.setGameMode(SinkingShip.lobbyGamemode);
        p.teleport(SinkingShip.lobbyLoc);
    }

    /**
     * Whether any of these events apply to the specified player
     * @param p Player
     * @return Whether it applies
     */
    public boolean applies(Player p){
        return players.contains(p)||spectators.contains(p);
    }

    /**
     * Hides the specified player from all the other players in game or spectating
     * @param p The Player
     */
    public void hide(Player p){
        for (Player cur : players){
            cur.hidePlayer(p);
        }
        for (Player cur : spectators){
            cur.hidePlayer(p);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryChange(InventoryInteractEvent event){
        if (event.getWhoClicked() instanceof Player){
            if (spectators.contains(event.getWhoClicked())){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (applies(event.getPlayer())) {
            if (players.contains(event.getPlayer())) {
                players.remove(event.getPlayer());
            }
            if (spectators.contains(event.getPlayer())) {
                players.remove(event.getPlayer());
            }
            event.getPlayer().setGameMode(SinkingShip.lobbyGamemode);
            event.getPlayer().teleport(SinkingShip.lobbyLoc);
            sendMessage(event.getPlayer()+" has left");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event){
        if (players.contains(event.getPlayer())){
            if (timeUntilStart>0){
                event.setCancelled(true);
            }
            else {
                if (event.getPlayer().getLocation().getY()<waterLevel){
                    playerDie(event.getPlayer());
                }
                if (event.getPlayer().getLocation().getY()>=arena.getWaterEnd()){
                    playerWin(event.getPlayer());
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onServerTick(ServerTickEvent event){
        if (players.size()<=0&&timeUntilStart==0){
            endGame();
        }

        if (timeUntilStart<=0) {
            totalTime++;
            interval++;
            if (interval >= ServerTickEvent.toSchedulerTicks(arena.getSeconds())) {
                interval = 0;
                waterLevel = waterLevel + arena.getBlocksPer();
                updateWaterLevel();
            }
            if (arena.timeLimit!=0){
                if (totalTime>=(arena.timeLimit*20)){
                    playSound(Sound.ENDERDRAGON_DEATH, 1, 3);
                    sendTitle(ChatColor.RED+"You ran out of time and died!", ChatColor.YELLOW+"Better luck next time!", 1);
                    endGame();
                }
            }
        }
        else {

            if (!shouldStart()){
                return;
            }
            timeUntilStart--;
            if (timeUntilStart<=200){
                playSound(Sound.CLICK, 2, 4);
                if ((timeUntilStart%20)==0) {
                    sendMessage(ChatColor.GREEN + "" + Math.round(timeUntilStart / 20));
                }
            }
            if (timeUntilStart==1){
                start();
            }
        }
        arena.updateSigns();
    }

    /**
     * Start the game
     */
    public void start(){
        timeUntilStart=0;
        playSound(Sound.ENDERDRAGON_GROWL, 1, 3);
        sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GO!!!", ChatColor.RED + "Dont Drown!", 20);
    }

    /**
     * Whether the game should start
     * @return Whether the game should start
     */
    public boolean shouldStart(){
        return (players.size()>=arena.minNoVote||(players.size()>arena.minimumPlayers&&votes>SinkingShip.votes));
    }

    private void updateWaterLevel(){
        int bigX = NumUtils.getBigger(arena.x1, arena.x2);
        int smallX = NumUtils.getSmaller(arena.x1, arena.x2);

        int bigZ = NumUtils.getBigger(arena.z1, arena.z2);
        int smallZ = NumUtils.getSmaller(arena.z1, arena.z2);
        for (int i=0;i<arena.getBlocksPer();i++){
            int curY = waterLevel-i;
            for (int x=smallX;x<bigX;x++){
                for (int z = smallZ;z<bigZ;z++){
                    Block block = arena.world.getBlockAt(x, curY, z);
                    if (block.getType()== Material.AIR){
                        block.setType(Material.WATER);
                    }
                }
            }
        }
    }

    public void clearWater(){
        int bigX = NumUtils.getBigger(arena.x1, arena.x2);
        int smallX = NumUtils.getSmaller(arena.x1, arena.x2);

        int bigY = NumUtils.getBigger(arena.y1, arena.y2);
        int smallY = NumUtils.getSmaller(arena.y1, arena.y2);

        int bigZ = NumUtils.getBigger(arena.z1, arena.z2);
        int smallZ = NumUtils.getSmaller(arena.z1, arena.z2);

        for (int x=smallX;x<bigX;x++){
            for (int y=smallY;y<bigY;y++){
                for (int z=smallZ;z<bigZ;z++){
                    Block block = arena.world.getBlockAt(x, y, z);
                    if (block.getType()==Material.WATER){
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    /**
     * When the player wins
     * @param p The Player
     */
    public void playerWin(Player p){
        players.remove(p);
        spectate(p);
        sendMessage(p.getName()+" has reached the end!");
        p.playSound(p.getLocation(), Sound.ENDERDRAGON_HIT, 1f, 3f);
        Messages.sendTitle(p, ChatColor.GREEN+"You Made it to safety!", ChatColor.LIGHT_PURPLE+"Congratulations!");
    }

    /**
     * Called when a player "dies"
     * @param p The Player
     */
    public void playerDie(Player p){
        players.remove(p);
        spectate(p);
        sendMessage(p.getName()+" drowned!");
        p.playSound(p.getLocation(), Sound.ENDERMAN_DEATH, 1f, 3f);
        Messages.sendTitle(p, ChatColor.RED+"You Drowned!", ChatColor.BLUE+"Better luck next time");
    }

    /**
     * Called to make a player leave the game
     * @param p The Player
     */
    public void leaveGame(Player p){
        if (players.contains(p)) {
            players.remove(p);
        } else if (spectators.contains(p)){
            spectators.remove(p);
        }
        sendMessage(p.getName()+" has left");
        p.teleport(SinkingShip.lobbyLoc);
        p.setGameMode(SinkingShip.lobbyGamemode);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event){
        if (applies(event.getPlayer())){
            if (SinkingShip.preventInteract){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event){
        if (applies(event.getPlayer())){
            if (SinkingShip.preventBlockBreak){
                event.setCancelled(true);
            }
        }
    }

    /**
     * Gets all the gameInstances
     * @return All the Game Instances
     */
    public static List<GameInstance> allGames(){
        ArrayList<GameInstance> list = new ArrayList<GameInstance>();
        for (Arena a : Arena.arenas){
            list.add(a.game);
        }
        return list;
    }

    /**
     * Whether the player is in a game
     * @param p The Player
     * @return Whether he is in a game
     */
    public static boolean isInGame(Player p){
        for (Arena a : Arena.arenas){
            if (a.game.players.contains(p)){
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the game a player is in
     * @param p The Player
     * @return The Game
     */
    public static GameInstance getGame(Player p){
        for (Arena a : Arena.arenas){
            if (a.game.players.contains(p)){
                return a.game;
            }
            if (a.game.spectators.contains(p)){
                return a.game;
            }
        }
        return null;
    }

    /**
     * Whether the player is spectating a game
     * @param p The Player
     * @return Whether the player is spectating a gane
     */
    public static boolean isSpectating(Player p){
        for (Arena a : Arena.arenas){
            if (a.game.spectators.contains(p)){
                return true;
            }
        }
        return false;
    }

}
