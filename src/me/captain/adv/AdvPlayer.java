//Do want you want with this trash code :)
package me.captain.adv;

import me.captain.adv.quest.AdvPlayerProgress;
import me.captain.adv.quest.AdvQuest;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * @author andrew b
 */
public class AdvPlayer {
    
    private final UUID userid;
    public long xp;
    private int level;
    public String name;
    public String displayName;
    private ArrayList<AdvPerk> perks;
    public HashMap<AdvQuest, AdvPlayerProgress> quests;
    public AdvCharType chartype;
    
    public Player player;
        
    public AdvPlayer(UUID id, int qxp) {
        userid = id;
        xp = qxp;
        quests = new HashMap<>();
    }
    
    public UUID getUserid() {
        return userid;
    }

    public ArrayList<AdvPerk> getPerks() {
        return perks;
    }

    public Set<AdvQuest> getQuests() {
        return quests.keySet();
    }
    public void addPerk(AdvPerk ap) {
        this.perks.add(ap);
    }
    public void addQuest(Adventure a, AdvQuest aq) {
        this.quests.put(aq, new AdvPlayerProgress(a, aq, aq.getStart()));
    }

    public void endQuest(int id) {
        for(AdvQuest q : quests.keySet()) {
            if(q.getId()==id){
                quests.remove(q);
            }
        }
    }
    public void addXp(int xp) {
        this.setXp(this.xp+ xp);
    }

    /**
     * @param xp the xp to set
     */
    public void setXp(long xp) {
        this.xp = xp;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }
    public void incXp(long xp) {
        this.xp += xp;
    }
    public void incLevel() {
        this.level++;
    }
    public long nextlevel() {
        int nla = level*50;
        int b = level*10;
        return nla+b;
    }
    public boolean addCheck(int xp) {
        long n = nextlevel();
        if(this.xp < n) {
            this.xp += xp;
        }
        if(this.xp < n) {
            return false;
        } else {
            incLevel();
            return true;
        }
    }
    
    public void removeFromInventory(ItemStack is) {
        player.getInventory().remove(is);
    }
    
    public void setPlayer(Player player) {
        this.player = player;
        this.displayName = player.getDisplayName();
    }
    public void sendMessage(String msg) {
        this.player.sendMessage(msg);
    }
    public Location getLocation() {
        return player.getLocation();
    }
}

