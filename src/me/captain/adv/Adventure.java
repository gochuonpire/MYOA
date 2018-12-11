//Do want you want with this trash code :)
package me.captain.adv;

import me.captain.adv.quest.AdvQuest;
import me.captain.adv.quest.AdvState;
import me.captain.adv.npc.NPCManager;
import org.bukkit.Location;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import me.captain.adv.npc.CapNPC;
import me.captain.adv.properties.PropertyManager;

/**
 *
 * @author andre
 */
public class Adventure {

    private final String name;
    private final String owner;
    private final Integer id;
    private final ArrayList<AdvTeleport> teleports;
    private final ArrayList<AdvZone> zones;
    private final ArrayList<AdvLocation> locations;
    private ArrayList<AdvPerk> perks;
    private final String path;
    private String prefix;
    public ArrayList<AdvPlayer> players;
    private final ArrayList<AdvQuest> quests;
    public MYOA plugin;
    public NPCManager npcm;
    public PropertyManager pm;

    public Adventure(MYOA instance, String name, String owner, Integer id, String path, String prefix) {
        plugin = instance;
        this.name = name;
        this.owner = owner;
        this.id = id;
        teleports = new ArrayList<>();
        zones = new ArrayList<>();
        locations = new ArrayList<>();
        this.path = path;
        players = new ArrayList<>();
        this.quests = new ArrayList<>();
        npcm = new NPCManager(instance, this);
        this.prefix = prefix;
        pm = new PropertyManager(this);
    }
    
    public void enabler() {
        System.out.println("[MYOA] Enabling " + name);
        npcm.loadNPCS();
        npcm.loadCareers();
        npcm.fm.loadFactionData();
        npcm.spawnNPCS();
        npcm.dm.loadDialogue();
        setAdvs();
        npcm.updateShouldStop();
        pm.loadProperties();
        System.out.println("[MYOA] Enabled " + name);
    }
    
    public void setAdvs() {
        for(AdvQuest q : this.getQuests()) {
            q.setAdv(this);
        }
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Integer getId() {
        return id;
    }

    public ArrayList<AdvTeleport> getTeleports() {
        return teleports;
    }

    public ArrayList<AdvPerk> getPerks() {
        return perks;
    }

    public ArrayList<AdvZone> getZones() {
        return zones;
    }

    public ArrayList<AdvLocation> getLocations() {
        return locations;
    }

    public void addTeleport(AdvTeleport tp) {
        this.teleports.add(tp);
    }

    public void addZone(AdvZone az) {
        this.zones.add(az);
    }

    public void addLocation(AdvLocation al) {
        this.locations.add(al);
    }

    public void removeTeleport(AdvTeleport tp) {
        this.teleports.remove(tp);
    }

    public void removeZone(AdvZone az) {
        this.zones.remove(az);
    }

    public void removeLocation(AdvLocation al) {
        this.locations.remove(al);
    }

    public AdvTeleport getTeleport(Integer id) {
        for (AdvTeleport at : teleports) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }

    public AdvZone getZone(Integer id) {
        for (AdvZone az : zones) {
            if (az.getId().equals(id)) {
                return az;
            }
        }
        return null;
    }

    public AdvLocation getLoc(Integer id) {
        for (AdvLocation al : locations) {
            if (al.getId().equals(id)) {
                return al;
            }
        }
        return null;
    }

    public String getPath() {
        return path;
    }

    public AdvPlayer getPlayer(UUID player) {
        for (AdvPlayer ap : players) {
            if (ap.getUserid().compareTo(player) == 0) {
                return ap;
            }
        }
        return null;
    }

    public void addQuest(AdvQuest aq) {
        this.quests.add(aq);
    }

    public ArrayList<AdvQuest> getQuests() {
        return quests;
    }

    public AdvLocation getExit(AdvZone az) {
        for (AdvTeleport at : teleports) {
            if (at.getZone().equals(az.getId())) {
                return getLoc(at.getLoc());
            }
        }
        return null;
    }

    public AdvQuest getQuest(int id) {
        for (AdvQuest q : quests) {
            if (q.getId() == id) {
                return q;
            }
        }
        return null;
    }

    public AdvTeleport getTeleport(AdvZone z) {
        for (AdvTeleport t : teleports) {
            if (Objects.equals(t.getZone(), z.getId())) {
                return t;
            }
        }
        return null;
    }

    public List<String> getPlayers() {
        ArrayList<String> ids = new ArrayList<>();
        for (AdvPlayer ap : players) {
            ids.add(ap.getUserid().toString());
        }
        return ids;
    }

    public AdvPlayer getPlayer(String uuid) {
        for (AdvPlayer ap : players) {
            if(ap.getUserid().toString().equals(uuid)) {
                return ap;
            }
        }
        return null;
    }

    public void finish(AdvQuest aq, AdvPlayer ap) {
        plugin.insertFinished(this, aq, ap);
        plugin.removeProgress(this, aq, ap);
    }

    public AdvState getState(int id) {
        for (AdvQuest aq : quests) {
            AdvState as = aq.getState(id);
            if (as != null) {
                return as;
            }
        }
        return null;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public AdvQuest getQuestByGiver(Location l) {
        for(AdvQuest aq : quests) {
            AdvLocation al = getLoc(aq.getGiver());
            if(al!=null) {
                if(al.getLocation().distance(l) < 0.5) {
                    return aq;
                }
            }
        }
        return null;
    }
    
    public AdvQuest getQuestByGiver(CapNPC cnpc) {
        for(AdvQuest aq : quests) {
            if(aq.getGiver() == cnpc.getId()) {
                return aq;
            }
        }
        return null;
    }
}
