//Do want you want with this trash code :)
package me.captain.adv.dialogue;

/**
 *
 * @author andre
 */
public class Line {
    
    private final int id;
    private final FlagType ft;
    private final String text;
    
    public int quest_id;
    public ResponseType rt;
    public int treespot;
    
    public Line(int id, FlagType ft, String text) {
        this.id = id;
        this.ft = ft;
        this.text = text;
        quest_id = 0;
        rt = ResponseType.NONE;
        treespot = 0;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the ft
     */
    public FlagType getFt() {
        return ft;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }
}
