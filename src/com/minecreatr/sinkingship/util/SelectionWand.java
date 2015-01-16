package com.minecreatr.sinkingship.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Class to help with the selection wand
 *
 * @author minecreatr
 */
public class SelectionWand {

    public static final String SSWandName = ChatColor.LIGHT_PURPLE+"SS Selection Wand";
    public static final String SSWandLore = ChatColor.GREEN+"Use to select the dimensions of an arena";

    /**
     * All the players selected pos1 locations
     */
    public static HashMap<UUID, Location> firstLocs = new HashMap<UUID, Location>();

    /**
     * All the players selected pos2 locations
     */
    public static HashMap<UUID, Location> secondLocs = new HashMap<UUID, Location>();

    /**
     * Gets the selection wand item
     * @return The Selection wand item
     */
    public static ItemStack getWandItem(){
        ItemStack stack = new ItemStack(Material.ARROW);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE+"SS Selection Wand");
        meta.setLore(Arrays.asList(SSWandLore));
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * Return whetehr the passed in item stack is a selection wand
     * @param stack The Itemstack
     * @return Whether the Itemstack is a selection wand
     */
    public static boolean isWand(ItemStack stack){
        return (stack.getType()==Material.ARROW&&stack.getItemMeta().getLore().get(0).equals(SSWandLore));
    }
}
