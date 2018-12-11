//Do want you want with this trash code :)
package me.captain.adv.properties;

import java.util.ArrayList;
import me.captain.adv.AdvZone;
import me.captain.adv.npc.CapNPC;
import org.bukkit.inventory.Inventory;

/**
 *
 * @author andre
 */
public class Property {
    private final int id;
    private final int owner;
    private final String name;
    
    public ArrayList<CapNPC> associates;
    
    public Inventory inv;
    
    public AdvZone zone;
    public AdvZone hold;
    
    public Property(int id, int owner, String name) {
        this.id = id;
        this.owner = owner;
        this.name = name;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the owner
     */
    public int getOwner() {
        return owner;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}
