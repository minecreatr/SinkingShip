package com.minecreatr.sinkingship.arena;

import com.minecreatr.sinkingship.Messages;

/**
 * Represents a stage/step in the arena creation process
 *
 * @author minecreatr
 */
public enum CreationState {
    NO_STAGE(0),
    SELECT_AREA(1),
    SET_WATER_START_HEIGHT(2),
    SET_WATER_END_HEIGHT(3),
    SET_SPAWN_REGION(4),
    DONE(5);

    private int id;
    private String message;

    CreationState(int id){
        this.id=id;
        this.message="Default Message, If you are seeing this that is an error, please report to either JAMESTEL or minecreatr";
    }

    public int getId(){
        return this.id;
    }

    public CreationState getNext(){
        return getStateById(id+1);
    }


    public void setMessage(String message){
        this.message=message;
    }

    public static CreationState getStateById(int id){
        for (CreationState state : values()){
            if (state.getId()==id){
                return state;
            }
        }
        return CreationState.NO_STAGE;
    }

    public String getMessage(){
        return this.message;
    }
}
