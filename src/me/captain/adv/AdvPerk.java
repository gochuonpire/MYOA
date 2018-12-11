//Do want you want with this trash code :)
package me.captain.adv;

import java.util.ArrayList;

/**
 *
 * @author andre
 */
public class AdvPerk {

    private final Integer id;
    private final String name;
    private final ArrayList<String> meta;

    public AdvPerk(Integer id, String name, ArrayList<String> meta) {
        this.id = id;
        this.name = name;
        this.meta = meta;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the meta
     */
    public ArrayList<String> getMeta() {
        return meta;
    }
}
