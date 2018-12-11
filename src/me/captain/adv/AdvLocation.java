// Do want you want with this trash code :)
package me.captain.adv;

import java.util.ArrayList;
import org.bukkit.Location;

/**
 *
 * @author andre
 */


public class AdvLocation {
    private String name;
    private Integer id;
    private Location location;
    private ArrayList<String> flavor;

    public AdvLocation(String n, Integer id, Location loc, ArrayList<String> meta) {
        name = n;
        location = loc;
        if(meta == null) {
            flavor = new ArrayList<>();
        } else {
            flavor = meta;
        }
        this.id = id;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the flavor
     */
    public ArrayList<String> getFlavor() {
        return flavor;
    }
    
    public void addFlavor(String s) {
        this.flavor.add(s);
    }
    
    public void removeFlavor(String s) {
        this.flavor.remove(s);
    }
    
    public void removeFlavor(Integer index) {
        this.flavor.remove(index);
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }
}
