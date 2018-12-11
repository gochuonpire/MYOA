//Do want you want with this trash code :)
package me.captain.adv;

/**
 *
 * @author andre
 */
public class AdvTeleport {

    private Integer loc;
    private Integer zone;
    private Integer id;
    private String name;
    private String msg;
    public int advid;

    public AdvTeleport(int advid, String name, Integer id, Integer exitLoc, Integer portalEntrance) {
        this.advid = advid;
        loc = exitLoc;
        zone = portalEntrance;
        this.name = name;
        this.id = id;
    }
    
    public void addMessage(String message) {
        this.msg = message;
    }

    public Integer getLoc() {
        return loc;
    }

    public void setLoc(Integer loc) {
        this.loc = loc;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }
}
