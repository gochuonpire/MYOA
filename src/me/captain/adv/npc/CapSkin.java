//Do want you want with this trash code :)
package me.captain.adv.npc;

/**
 *
 * @author andre
 */
public class CapSkin {
    public String texValue;
    public String texSig;
    public String texName;
    public String texUUID;
    public boolean enabled;
    
    public CapSkin(String skinName, String skinUUID, String skinValue, String skinSignature, boolean enabled) {
        texName = skinName;
        texUUID = skinUUID;
        texValue = skinValue;
        texSig = skinSignature;
        this.enabled = enabled;
    }
    public CapSkin() {
        texName = "";
        texUUID = "";
        texValue = "";
        texSig = "";
        this.enabled = false;
    }
}
