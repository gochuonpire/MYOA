//Do want you want with this trash code :)
package me.captain.adv.npc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import me.captain.adv.dialogue.DialoguePack;
import me.captain.adv.dialogue.FlagType;
import me.captain.adv.dialogue.Line;
import me.captain.adv.faction.FactionType;
import me.captain.adv.quest.AdvQuest;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.minecraft.server.v1_12_R1.EntityPlayer;

/**
 *
 * @author andre
 */
public class CapNPC {
    
    private final int id;
    private String name;
    
    public Location anchor;
    public Location startGaze;
    public UUID uuid;
    public EntityPlayer npcEntity;
    public EntityType npcType;
    public NPCCareerType career;
    public boolean spawned;
    public boolean killed;
    public HashMap<UUID, ArrayList<AdvQuest>> shouldStop;
    
    public Player lastDamaged;
    
    public CapSkin skin;
    
    public ArrayList<FactionType> factions;
    
    public ArrayList<ItemStack> gear;
    
    public HashMap<FlagType, ArrayList<Line>> lines;
    
    
    public NPC npc;
    
    
    public CapNPC(int id, String name) {
        this.id = id;
        this.name = name;
        this.spawned = false;
        this.factions = new ArrayList<>();
        gear = new ArrayList<>();
        lines = new HashMap<>();
        shouldStop = new HashMap<>();
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
