//Do want you want with this trash code :)
package me.captain.adv.npc;

import java.util.ArrayList;
import java.util.HashMap;
import me.captain.adv.dialogue.FlagType;
import me.captain.adv.dialogue.Line;

/**
 *
 * @author andre
 */
public class Career {
    private final NPCCareerType type;
    public HashMap<FlagType,ArrayList<Line>> lines;
    public Career(NPCCareerType type) {
        this.type = type;
        lines = new HashMap<>();
    }

    /**
     * @return the type
     */
    public NPCCareerType getType() {
        return type;
    }
}
