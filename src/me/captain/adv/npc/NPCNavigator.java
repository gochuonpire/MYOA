//Do want you want with this trash code :)
package me.captain.adv.npc;

import java.util.HashMap;
import me.captain.adv.Adventure;

/**
 *
 * @author andre
 */
public class NPCNavigator {
   
    public Adventure a;
    public CapNPC npc;
    public HashMap<Integer, NPCTarget> targets;
    
    public NPCNavigator(Adventure adv, CapNPC npc) {
        a = adv;
        this.npc = npc;
        targets = new HashMap<>();
    }

    public void addTarget(int id, NPCTarget target) {
        this.targets.put(id, target);
    }
    public void removeTarget(int id) {
        this.targets.remove(id);
    }
}
