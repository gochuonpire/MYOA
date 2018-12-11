//Do want you want with this trash code :)
package me.captain.adv.quest;

import java.util.ArrayList;
import java.util.List;
import me.captain.adv.Adventure;
import org.bukkit.inventory.ItemStack;

/**
 * @author andrew b
 */
public class AdvState {

    public Adventure adventure;
    private final int id;
    private String name;
    public int nextstate;
    public AdvStateType type;
    public ArrayList<String> flavor;
    public ItemStack items;
    private int zone;
    private int loc;
    private int npc;
    
    public AdvState(Adventure a, int id, String name, int nextstate, AdvStateType type, ArrayList<String> meta) {
        this.adventure = a;
        this.id = id;
        this.name = name;
        this.nextstate = nextstate;
        this.type = type;
        this.flavor = meta;
        this.zone = 0;
        this.loc = 0;
        this.npc = 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getNextstate() {
        return nextstate;
    }

    public void setMeta(ArrayList<String> meta) {
        this.flavor = meta;
    }

    public void addMeta(String s) {
        this.flavor.add(s);
    }

    public AdvStateType getType() {
        return type;
    }

    public List<String> getMeta() {
        return (List<String>) this.flavor;
    }
    public void setItems(ItemStack is) {
        this.items = is;
    }
    public ItemStack getItems() {
        return items;
    }
    public int getZone() {
        return zone;
    }
    public void setZone(int z) {
        zone = z;
    }
    public void setLoc(int l) {
        loc = l;
    }
    public int getLoc() {
        return loc;
    }
    public void setName(String s) {
        this.name = s;
    }
    public int getNPC() {
        return this.npc;
    }
    public void setNPC(int npc) {
        this.npc = npc;
    }
}
