//Do want you want with this trash code :)
package me.captain.adv.dialogue;

import me.captain.adv.npc.CapNPC;

/**
 *
 * @author andre
 */
public class ShopDialogue {
    
    public CapNPC npc;
    
    public Line accept;
    public Line deny;
    
    public ShopDialogue(CapNPC npc, Line accept, Line deny) {
        this.npc = npc;
        this.accept = accept;
        this.deny = deny;
    }
}
