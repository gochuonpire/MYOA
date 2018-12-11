//Do want you want with this trash code :)
package me.captain.adv.quest;

import java.util.ArrayList;
import me.captain.adv.Adventure;
import org.bukkit.inventory.ItemStack;

/**
 * @author andrew b
 */
public class AdvQuest {

    private final int id;
    private String name;
    private int giver;
    private final int xp;
    private ItemStack requiredItems;
    private ItemStack rewardItems;
    public int advid;
    
    private ArrayList<AdvState> states;
    
    public AdvState failstate;
    
    private Adventure adventure;
    
    public AdvQuest(int advid, int id, String name, int giver, int xp, ItemStack rItems, ItemStack wItems) {
        this.advid = advid;
        this.id = id;
        this.name = name;
        this.giver = giver;
        this.xp = xp;
        requiredItems = rItems;
        rewardItems = wItems;
        this.states = new ArrayList<>();
        failstate = new AdvState(adventure, id, name + "-failstate", 0, AdvStateType.FAIL, new ArrayList<>());
    }

    public int getId() {
        return id;
    }
    
    public void setGiver(int g) {
        giver = g;
    }

    public String getName() {
        return name;
    }

    public int getGiver() {
        return giver;
    }

    public int getXp() {
        return xp;
    }

    public ItemStack getRequiredItems() {
        return requiredItems;
    }

    public ItemStack getRewardItems() {
        return rewardItems;
    }

    public ArrayList<AdvState> getStates() {
        return states;
    }

    public void setStates(ArrayList<AdvState> states) {
        this.states = states;
    }
    public AdvState getState(int stateId) {
        if(states.isEmpty()) {
            return null;
        }
        for(AdvState as : states) {
            if(as.getId() == stateId) {
                return as;
            }
        }
        return null;
    }

    public void setAdv(Adventure adv) {
        adventure = adv;
    }
    public AdvState getStart() {
        for(AdvState as : states) {
            if(as.getType() == AdvStateType.START) {
                return as;
            }
        }
        return null;
    }
    public AdvState getEnd() {
        for(AdvState as : states) {
            if(as.getType() == AdvStateType.END) {
                return as;
            }
        }
        return null;
    }
    public void addState(AdvState as) {
        if(this.getState(as.getId()) == null) {
            this.states.add(as);
        }
    }
    public void setName(String s) {
        this.name = s;
    }
    /*public void replaceState(AdvState as) {
        AdvState aso = this.getState(as.getId());
        this.states.remove(aso);
        this.states.add(as);
    }*/
    
    public void setRequiredItems(ItemStack requiredItems) {
        this.requiredItems = requiredItems;
    }
    public void setRewardItems(ItemStack rewardItems) {
        this.rewardItems = rewardItems;
    }

}
