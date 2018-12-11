//Do want you want with this trash code :)
package me.captain.adv.dialogue;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author andre
 */
public class NPCTree {
        
    public int counter;
    public HashMap<Integer, ArrayList<Line>> allOptions;
    public ArrayList<Line> currentNPCLines;
    
    public int max;
    
    public Line lastLine;
    
    public NPCTree() {
        this.counter = 0;
        allOptions = new HashMap<>();
        currentNPCLines = new ArrayList<>();
    }
    
    public void moveAlong() {
        counter++;
        currentNPCLines = allOptions.get(counter);
    }
    public void initiate() {
        currentNPCLines = allOptions.get(counter);
    }
} 
