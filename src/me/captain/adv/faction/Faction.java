//Do want you want with this trash code :)
package me.captain.adv.faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import me.captain.adv.AdvPlayer;
import me.captain.adv.dialogue.DialoguePack;
import me.captain.adv.dialogue.FlagType;
import me.captain.adv.dialogue.Line;
import me.captain.adv.npc.CapSkin;
import me.captain.adv.npc.NPCCareerType;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author andre
 */
public class Faction {
    private final int id;
    private final String name;
    
    public FactionType type;
    public CapSkin skin;
    public ArrayList<FactionType> allies;
    public ArrayList<FactionType> enemies;
    public ArrayList<UUID> playerEnemies;
    public NPCCareerType career;
    public Boolean hidden;
    public Boolean combat;
    
    public HashMap<UUID, DialoguePack> shouldStop;
    
    public HashMap<FlagType, ArrayList<Line>> lines;
    
    public ArrayList<ItemStack> gear;
    
    public Faction(int id, String name) {
        this.id = id;
        this.name = name;
        allies = new ArrayList<>();
        enemies = new ArrayList<>();
        playerEnemies = new ArrayList<>();
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
}
