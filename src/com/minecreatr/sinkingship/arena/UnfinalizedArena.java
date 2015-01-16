package com.minecreatr.sinkingship.arena;

import com.google.common.collect.Lists;
import com.minecreatr.sinkingship.SinkingShip;
import com.minecreatr.sinkingship.arena.Arena;
import com.minecreatr.sinkingship.arena.CreationState;
import com.minecreatr.sinkingship.util.ArenaSign;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * An Unfinalized version of {@link com.minecreatr.sinkingship.arena.Arena}
 *
 * @author minecreatr
 */
public class UnfinalizedArena {

    public String name;
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

    public int blocksPer = SinkingShip.blocksPer;
    public int seconds = SinkingShip.seconds;
    public int timeLimit = SinkingShip.timeLimit;

    public int minimumPlayers = SinkingShip.minPlayers;
    public int minNoVote = SinkingShip.minNoVote;
    public int maxPlayers = SinkingShip.maxPlayers;

    public World world;

    public CreationState state;

    public UnfinalizedArena(CreationState state){
        this.state=state;
    }

    public Arena create(){
        return Arena.createArena(name, x1, y2, z1, x2, y2, z2, waterStart, waterEnd, blocksPer, seconds, timeLimit, minimumPlayers, minNoVote, maxPlayers,
                spawnX1, spawnY1, spawnZ1, spawnX2, spawnY2, spawnZ2, world, new ArrayList<ArenaSign>());
    }

    public void nextStage(){
        this.state = state.getNext();
    }

    public String toString(){
        StringBuffer b = new StringBuffer("[");
        for (Field f : this.getClass().getDeclaredFields()){
            f.setAccessible(true);
            try {
                b.append("{"+f.getName() + ":" + f.get(this)+"}");
            } catch (IllegalAccessException exc){

            }
        }
        return b.append("]").toString();
    }

}
