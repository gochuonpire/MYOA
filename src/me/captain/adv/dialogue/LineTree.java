//Do want you want with this trash code :)
package me.captain.adv.dialogue;

import java.util.ArrayList;
import java.util.HashMap;
import me.captain.adv.Adventure;

/**
 *
 * @author andre
 */
public class LineTree {
    
    public int counter;
    public HashMap<Integer, ArrayList<Line>> allOptions;
    public ArrayList<Line> currentPlayerOptions;
    
    public Line lastLine;
    
    public int max;
    
    public LineTree() {
        this.counter = 0;
        allOptions = new HashMap<>();
        currentPlayerOptions = new ArrayList<>();
    }
    
    public void moveAlong() {
        counter++;
        currentPlayerOptions = allOptions.get(counter);
    }
    
    public void initiate() {
        currentPlayerOptions = allOptions.get(counter);
    }
    
} 
