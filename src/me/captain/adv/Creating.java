//Do want you want with this trash code :)
package me.captain.adv;
import org.bukkit.Location;

/**
 *
 * @author andre
 */
public class Creating {
    
    public String name;
    public String node;
    public int id;
    public Location loc1;
    public Location loc2;
    public int offset;
    public int type;
    public String msg;
    public int advid;
    
    public Creating(String name, String node, int id, int offset, int aid) {
        this.name = name;
        this.node = node;
        this.id = id;
        this.offset = offset;
        type = 0;
        this.advid = aid;
    }
    public Creating(String name, int id, String msg) {
        this.name = name;
        this.id = id;
        this.msg = msg;
        type = 1;
    }
    public Creating(String name, int id, int aid) {
        this.name = name;
        this.id = id;
        this.advid = aid;
        type = 2;
    }
}
