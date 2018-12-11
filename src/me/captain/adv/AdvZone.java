//Do want you want with this trash code :)
package me.captain.adv;

import java.util.ArrayList;
import me.captain.adv.dialogue.FlagType;
import org.bukkit.Location;

/**
 *
 * @author andrew b
 */
public class AdvZone {

    private Integer id;
    private Location loc1;
    private Location loc2;
    private Integer offsetY;
    private String node;
    private ArrayList<String> flavor;
    private String name;
    public int advid;
    
    public FlagType areaFlag;
    
    public AdvZone(int advid, String name, int idnumber, String nNode, Location nLoc1, Location nLoc2, int nOffsetY, ArrayList<String> meta) {
        this.advid = advid;
        this.name = name;
        id = idnumber;
        node = nNode;
        loc1 = nLoc1;
        loc2 = nLoc2;
        offsetY = nOffsetY;
        flavor = meta;
        areaFlag = FlagType.NONE;
    }

    public Location getLoc1() {
        return loc1;
    }

    public void setLoc1(Location loc1) {
        this.loc1 = loc1;
    }

    public Location getLoc2() {
        return loc2;
    }

    public void setLoc2(Location loc2) {
        this.loc2 = loc2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(Integer offsetY) {
        this.offsetY = offsetY;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public ArrayList<String> getFlavor() {
        return flavor;
    }
    
    public void addFlavor(String s) {
        this.flavor.add(s);
    }
    
    public void removeFlavor(String s) {
        this.flavor.remove(s);
    }
    
    public void removeFlavor(int index) {
        this.flavor.remove(index);
    }

    public String getName() {
        return name;
    }
}
