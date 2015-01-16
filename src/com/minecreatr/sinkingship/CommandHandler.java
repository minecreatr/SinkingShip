package com.minecreatr.sinkingship;

import com.minecreatr.sinkingship.arena.Arena;
import com.minecreatr.sinkingship.arena.CreationState;
import com.minecreatr.sinkingship.arena.UnfinalizedArena;
import com.minecreatr.sinkingship.util.ConfigurationHelper;
import com.minecreatr.sinkingship.util.GameInstance;
import com.minecreatr.sinkingship.util.SelectionWand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Handles the Sinking Ship commands
 *
 * @author minecreatr
 */
public class CommandHandler {

    /**
     * Stores whether player is starting or confirming delete process, long is to expire after 15 seconds
     */
    public static HashMap<UUID, Long> useDelete = new HashMap<UUID, Long>();

    /**
     * UUID used for console
     */
    public static final UUID console = UUID.randomUUID();

    /**
     * Handle all Sinking Ship subcommands
     * @param sender The Command Sender
     * @param args The String arguments
     * @return Whether onCommand should return true or false
     */
    public static boolean handle(CommandSender sender, String[] args){
        if (args.length<1){
            SinkingShip.sendHelp(sender);
            return true;
        }
        if (SinkingShip.create.isCalling(args[0])){
            if (!SinkingShip.create.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.create.sendHelp(sender);
            }
            if (sender instanceof Player){
                Player player = (Player) sender;
                if (SinkingShip.creations.containsKey(player.getUniqueId())){
                    UnfinalizedArena unf = SinkingShip.creations.get(player.getUniqueId());
                    if (unf.state==CreationState.SELECT_AREA){
                        if (SelectionWand.firstLocs.containsKey(player.getUniqueId())&&SelectionWand.secondLocs.containsKey(player.getUniqueId())){
                            Location loc1 = SelectionWand.firstLocs.get(player.getUniqueId());
                            Location loc2 = SelectionWand.secondLocs.get(player.getUniqueId());
                            unf.x1=loc1.getBlockX();
                            unf.y1=loc1.getBlockY();
                            unf.z1=loc1.getBlockZ();
                            unf.x2=loc2.getBlockX();
                            unf.y2=loc2.getBlockY();
                            unf.z2=loc2.getBlockZ();
                            unf.state.getNext();
                            unf.nextStage();
                            Messages.sendMessage(sender, unf.state.getMessage());
                            return true;
                        }
                        else {
                            Messages.sendMessage(sender, unf.state.getMessage());
                            return true;
                        }
                    }
                    else if (unf.state==CreationState.SET_WATER_START_HEIGHT){
                        unf.waterStart=player.getLocation().getBlockY();
                        unf.nextStage();
                        Messages.sendMessage(sender, unf.state.getMessage());
                        return true;
                    }
                    else if (unf.state==CreationState.SET_WATER_END_HEIGHT){
                        unf.waterEnd=player.getLocation().getBlockY();
                        unf.nextStage();
                        Messages.sendMessage(sender, unf.state.getMessage());
                        return true;
                    }
                    else if (unf.state==CreationState.SET_SPAWN_REGION){
                        if (SelectionWand.firstLocs.containsKey(player.getUniqueId())&&SelectionWand.secondLocs.containsKey(player.getUniqueId())){
                            Location loc1 = SelectionWand.firstLocs.get(player.getUniqueId());
                            Location loc2 = SelectionWand.secondLocs.get(player.getUniqueId());
                            unf.spawnX1=loc1.getBlockX();
                            unf.spawnY1=loc1.getBlockY();
                            unf.spawnZ1=loc1.getBlockZ();
                            unf.spawnX2=loc2.getBlockX();
                            unf.spawnY2=loc2.getBlockY();
                            unf.spawnZ2=loc2.getBlockZ();
                            unf.state.getNext();
                            unf.nextStage();
                            Messages.sendMessage(sender, "Are you sure you would like the to create the arena "+unf.toString());
                            Messages.sendMessage(sender, "If you are do /ss create "+args[1]+" one more time");
                            return true;
                        }
                        else{
                            Messages.sendMessage(sender, unf.state.getMessage());
                            return true;
                        }
                    }
                    else if (unf.state==CreationState.DONE){
                        unf.world = player.getWorld();
                        Arena a = unf.create();
                        a.saveTo();
                        Messages.sendMessage(sender, "Saved arena "+a.toString());
                        SinkingShip.creations.remove(player.getUniqueId());
                        return true;
                    }
                }
                else {
                    UnfinalizedArena unf = new UnfinalizedArena(CreationState.SELECT_AREA);
                    unf.name=args[1];
                    SinkingShip.creations.put(player.getUniqueId(), unf);
                    Messages.sendMessage(sender, unf.state.getMessage());
                    return true;
                }
            }
            else {
                sender.sendMessage(Messages.prefix+"Only players may execute this command");
                return true;
            }
        }
        else if (SinkingShip.list.isCalling(args[0])){
            if (!SinkingShip.list.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            Messages.sendMessage(sender, "Arenas:");
            for (Arena arena : Arena.arenas){
                if (arena.isEnabled()){
                    Messages.sendMessage(sender, ChatColor.GREEN+arena.getName());
                }
                else {
                    Messages.sendMessage(sender, ChatColor.RED+arena.getName());
                }
            }
            return true;

        }
        else if (SinkingShip.join.isCalling(args[0])){
            if (!SinkingShip.join.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                SinkingShip.join.sendHelp(sender);
            }
            if (!Arena.exists(args[1])){
                Messages.sendMessage(sender, "No Arena with the name "+args[1]);
                return true;
            }
            if (!(sender instanceof Player)){
                Messages.sendMessage(sender, "Only Players can join an arena!");
                return true;
            }
            Player player = (Player) sender;
            Arena arena = Arena.getArena(args[1]);
            if (!arena.game.isInProgress()){
                if (arena.game.joinGame(player)){
                    Messages.sendMessage(sender, ChatColor.GREEN+"You Joined the game!");
                    return true;
                }
                else {
                    Messages.sendMessage(sender, ChatColor.RED+"Unable to join game!");
                    return true;
                }
            }
            else {
                Messages.sendMessage(sender, ChatColor.RED+"The Game is already in progress");
            }
        }
        else if (SinkingShip.leave.isCalling(args[0])){
            if (!SinkingShip.leave.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (!(sender instanceof Player)){
                Messages.sendMessage(sender, "Only Players can leave a game");
                return true;
            }
            Player player = (Player)sender;
            if (!(GameInstance.isInGame(player)||GameInstance.isSpectating(player))){
                Messages.sendMessage(sender, "You aren't in a game!");
                return true;
            }
            GameInstance game = GameInstance.getGame(player);
            if (game.getSpectators().contains(player)){
                game.stopSpectating(player);
            }
            else {
                game.leaveGame(player);
            }
            Messages.sendMessage(sender, "You Left the game!");
            return true;
        }
        else if (SinkingShip.lobby.isCalling(args[0])){
            if (!SinkingShip.lobby.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (!(sender instanceof Player)) {
                Messages.sendMessage(sender, "Only Players Can teleport to the lobby");
                return true;
            }
            Player player = (Player)sender;
            if (GameInstance.isInGame(player)){
                Messages.sendMessage(sender, "Please use /ss leave when you are in game");
                return true;
            }
            player.setGameMode(SinkingShip.lobbyGamemode);
            player.teleport(SinkingShip.lobbyLoc);
            Messages.sendMessage(sender, "Joined the lobby");
            return true;
        }
        else if (SinkingShip.spectate.isCalling(args[0])){
            if (!SinkingShip.spectate.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.spectate.sendHelp(sender);
            }
            if (!(sender instanceof Player)){
                Messages.sendMessage(sender, "Only Players can spectate a game");
                return true;
            }
            Player player = (Player)sender;
            if (!Arena.exists(args[1])){
                Messages.sendMessage(sender, "No Arena with the name "+args[1]);
                return true;
            }
            if (GameInstance.isInGame(player)){
                Messages.sendMessage(sender, "Please leave your current game before spectating");
                return true;
            }
            GameInstance game = Arena.getArena(args[1]).game;
            game.spectate(player);
            Messages.sendMessage(sender, "You are now spectating the game!");
            return true;
        }
        else if (SinkingShip.vote.isCalling(args[0])){
            if (!SinkingShip.vote.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (!(sender instanceof Player)){
                Messages.sendMessage(sender, "Only players can vote to start the arena");
                return true;
            }
            Player player = (Player)sender;
            if (!GameInstance.isInGame(player)){
                Messages.sendMessage(sender, "You cant vote if you aren't in a game!");
                return true;
            }
            GameInstance game = GameInstance.getGame(player);
            if (game.voters.contains(player.getUniqueId())){
                Messages.sendMessage(sender, "You have already voted!");
                return true;
            }
            game.votes++;
            game.voters.add(player.getUniqueId());
            Messages.sendMessage(sender, "You voted to start the game");
            Messages.sendMessage(sender, "There are "+game.votes+" total votes");
            return true;
        }
        else if (SinkingShip.start.isCalling(args[0])){
            if (!SinkingShip.start.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.start.sendHelp(sender);
            }
            if (!Arena.exists(args[1])){
                Messages.sendMessage(sender, "No Arena with the name "+args[1]);
                return true;
            }
            GameInstance game = Arena.getArena(args[1]).game;
            if (game.getTimeUntilStart()==0){
                Messages.sendMessage(sender, "Game is already in progress");
                return true;
            }
            else {
                game.start();
                Messages.sendMessage(sender, "Starting game in arena "+args[1]);
            }
            return true;
        }
        else if (SinkingShip.stop.isCalling(args[0])){
            if (!SinkingShip.stop.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.stop.sendHelp(sender);
            }
            if (!Arena.exists(args[1])){
                Messages.sendMessage(sender, "No Arena with the name "+args[1]);
                return true;
            }
            GameInstance game = Arena.getArena(args[1]).game;
            if (game.getTimeUntilStart()!=0){
                Messages.sendMessage(sender, "Game is not in progress");
                return true;
            }
            else {
                game.endGame();
                Messages.sendMessage(sender, "Stopped Game in arena "+args[1]);
            }
            return true;
        }
        else if (SinkingShip.setLobbySpawn.isCalling(args[0])){
            if (!SinkingShip.setLobbySpawn.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (!(sender instanceof Player)) {
                Messages.sendMessage(sender, "You must be a player to set the lobby spawn");
                return true;
            }
            Player player = (Player)sender;
            SinkingShip.lobbyLoc = player.getLocation();
            ConfigurationHelper.saveLobbyLoc();
            Messages.sendMessage(sender, "Saved the lobby location as your current location");
            return true;
        }
        else if (SinkingShip.enable.isCalling(args[0])){
            if (!SinkingShip.enable.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.enable.sendHelp(sender);
            }
            if (!Arena.existsIgnoreDisabled(args[1])){
                Messages.sendMessage(sender, "There is no arena with the name "+args[1]);
                return true;
            }
            Arena arena = Arena.getArena(args[1]);
            if (arena.isEnabled()){
                Messages.sendMessage(sender, "Arena is already enabled");
                return true;
            }
            else {
                arena.setEnabled(true);
                Messages.sendMessage(sender, "Enabling arena "+args[0]);
                return true;
            }
        }
        else if (SinkingShip.disable.isCalling(args[0])){
            if (!SinkingShip.disable.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.disable.sendHelp(sender);
            }
            if (!Arena.existsIgnoreDisabled(args[1])){
                Messages.sendMessage(sender, "There is no arena with the name "+args[1]);
                return true;
            }
            Arena arena = Arena.getArena(args[1]);
            if (!arena.isEnabled()){
                Messages.sendMessage(sender, "Arena is already disabled");
                return true;
            }
            else {
                arena.setEnabled(false);
                Messages.sendMessage(sender, "Disabling arena "+args[0]);
                return true;
            }
        }
        else if (SinkingShip.delete.isCalling(args[0])){
            if (!SinkingShip.delete.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.delete.sendHelp(sender);
            }
            if (!Arena.existsIgnoreDisabled(args[1])){
                Messages.sendMessage(sender, "There is no arena with the name "+args[0]);
                return true;
            }
            Arena arena = Arena.getArena(args[1]);
            UUID id;
            if (sender instanceof Player){
                id = ((Player)sender).getUniqueId();
            }
            else {
                id = console;
            }
            if (useDelete.containsKey(id)){
                if ((System.currentTimeMillis()-useDelete.get(id)<(15*1000))) {
                    if (arena.delete()){
                        Messages.sendMessage(sender, "Deleted arena "+args[1]);
                        return true;
                    }
                    else {
                        Messages.sendMessage(sender, "Error deleting arena "+args[1]);
                        return true;
                    }
                }
            }
            Messages.sendMessage(sender, "Are you sure you would like to delete the arena "+args[1]+"?");
            Messages.sendMessage(sender, "If so then do /ss delete "+args[1]+" , this will expire in 15 seconds");
            useDelete.put(id, System.currentTimeMillis());
        }
        else if (SinkingShip.setWaterSpeed.isCalling(args[0])){
            if (!SinkingShip.setWaterSpeed.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<4){
                return SinkingShip.setWaterSpeed.sendHelp(sender);
            }
            if (!Arena.exists(args[1])){
                Messages.sendMessage(sender, "No arena with the name "+args[1]);
                return true;
            }
            int blocksPer;
            int seconds;
            try {
                blocksPer = Integer.parseInt(args[2]);
            } catch (NumberFormatException exception){
                Messages.sendMessage(sender, "blocksPer must be an integer");
                return true;
            }
            try {
                seconds = Integer.parseInt(args[3]);
            } catch (NumberFormatException exception){
                Messages.sendMessage(sender, "seconds must be an integer");
                return true;
            }
            Arena arena = Arena.getArena(args[1]);
            arena.blocksPer=blocksPer;
            arena.seconds=seconds;
            Messages.sendMessage(sender, "Setting water speed for arena "+args[1]+" to "+blocksPer+" blocks per "+seconds+" seconds");
            arena.saveTo();
            return true;
        }
        else if (SinkingShip.setTimeLimit.isCalling(args[0])){
            if (!SinkingShip.setTimeLimit.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (args.length<2){
                return SinkingShip.setTimeLimit.sendHelp(sender);
            }
            if (!Arena.exists(args[1])){
                Messages.sendMessage(sender, "No arena with the name "+args[1]);
                return true;
            }
            int timeLimit;
            if (args.length==2){
                timeLimit=0;
            }
            else {
                try {
                    timeLimit = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    Messages.sendMessage(sender, "timeLimit must be an integer");
                    return true;
                }
            }
            Arena arena = Arena.getArena(args[1]);
            Messages.sendMessage(sender, "Setting timeLimit in arena "+args[1]+" to "+timeLimit);
            arena.timeLimit=timeLimit;
            arena.saveTo();
            return true;
        }
        else if (SinkingShip.wand.isCalling(args[0])){
            if (!SinkingShip.wand.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            if (sender instanceof Player){
                Player player = (Player)sender;
                player.getInventory().addItem(SelectionWand.getWandItem());
                Messages.sendMessage(sender, "Revieved Wand item");
                return true;
            }
            else {
                Messages.sendMessage(sender, "Only players may recieve a wand");
                return true;
            }
        }
        else if (SinkingShip.reload.isCalling(args[0])){
            if (!SinkingShip.reload.hasPermission(sender)){
                return Messages.sendNoPerm(sender);
            }
            SinkingShip.getInstance().onStart();
            Messages.sendMessage(sender, "Reloaded Sinking Ships Config");
            return true;
        }
        SinkingShip.sendHelp(sender);
        return true;
    }
}
