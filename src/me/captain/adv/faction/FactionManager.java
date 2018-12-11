//Do want you want with this trash code :)
package me.captain.adv.faction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.captain.adv.AdvPlayer;
import me.captain.adv.Adventure;
import me.captain.adv.npc.CapNPC;
import me.captain.adv.npc.CapSkin;
import me.captain.adv.npc.NPCCareerType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mcmonkey.sentinel.SentinelTrait;

/**
 *
 * @author andre
 */
public class FactionManager {

    public Adventure a;

    public HashMap<FactionType, Faction> factions;

    public FactionManager(Adventure adv) {
        a = adv;
        factions = new HashMap<>();
    }

    public void loadFactionData() {
        try {
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-factions.yml");
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                for (String k : advf.getKeys(false)) {
                    int id = advf.getInt(k + ".id");
                    String type = advf.getString(k + ".type");
                    String name = advf.getString(k + ".name");
                    Boolean hidden = advf.getBoolean(k + ".hidden");
                    Boolean combat = advf.getBoolean(k + ".combat");
                    String ctype = advf.getString(k + ".career");
                    Boolean gear = advf.getBoolean(k + ".gear.special");
                    List<String> allies = advf.getStringList(k + ".allies");
                    List<String> enemies = advf.getStringList(k + ".enemies");
                    List<String> penemies = advf.getStringList(k + ".player_enemies");
                    Boolean skin = advf.getBoolean(k + ".skin.enabled");
                    Faction fac = new Faction(id, name);
                    fac.allies = arrayFy(allies);
                    fac.enemies = arrayFy(enemies);
                    fac.type = FactionType.valueOf(type);
                    fac.hidden = hidden;
                    fac.combat = combat;
                    fac.playerEnemies = arrayPy(penemies);
                    if(!ctype.equals("")) {
                        fac.career = NPCCareerType.valueOf(ctype.toUpperCase());
                    }
                    if (skin) {
                        String tname = advf.getString(k + ".skin.name");
                        String tuuid = advf.getString(k + ".skin.uuid");
                        String tval = advf.getString(k + ".skin.texvalue");
                        String tsig = advf.getString(k + ".skin.texsig");
                        CapSkin cs = new CapSkin(tname, tuuid, tval, tsig, skin);
                        fac.skin = cs;
                    } else {
                        CapSkin cs = new CapSkin(null, null, null, null, false);
                        fac.skin = cs;
                    }
                    if (gear) {
                        List<String> items = advf.getStringList(k + ".gear.items");
                        for (String s : items) {
                            ItemStack item = advf.getItemStack(k + ".gear." + s);
                            fac.gear.add(item);
                        }
                    }
                    factions.put(fac.type, fac);
                    System.out.println("[MYOA] Added faction " + name + " to " + a.getName());
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("[MYOA] Error loading factions for " + a.getName());
        }
    }

    public void saveFactionData() {
        String path = a.getPath();
        File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-factions.yml");
        YamlConfiguration advf = new YamlConfiguration();
        if (factions.isEmpty()) {
            System.out.println("[MYOA] No Factions in " + a.getName());
            return;
        }
        for (Faction fac : factions.values()) {
            try {
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".id", fac.getId());
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".type", fac.type.toString());
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".name", fac.getName());
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".career", fac.career.toString());
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".hidden", fac.hidden);
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".combat", fac.combat);
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".allies", listFy(fac.allies));
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".enemies", listFy(fac.enemies));
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".player_enemies", listPy(fac.playerEnemies));
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".skin.enabled", fac.skin.enabled);
                if (fac.skin.enabled) {
                    advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".skin.name", fac.skin.texName);
                    advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".skin.uuid", fac.skin.texUUID);
                    advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".skin.texvalue", fac.skin.texValue);
                    advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".skin.texsig", fac.skin.texSig);
                }
                advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".gear.special", !fac.gear.isEmpty());
                if (!fac.gear.isEmpty()) {
                    List<String> items = new ArrayList<>();
                    for (ItemStack s : fac.gear) {
                        items.add(s.getType().toString() + fac.gear.indexOf(s));
                        advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".gear." + s.getType().toString() + fac.gear.indexOf(s), s);
                    }
                    advf.set(fac.type.toString().toLowerCase() + Integer.toString(fac.getId()) + ".gear.items", items);
                }
                advf.save(f);
                System.out.println("[MYOA] Saved Faction " + fac.getName());
            } catch (IOException ex) {
                Logger.getLogger(FactionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ArrayList<FactionType> arrayFy(List<String> l) {
        ArrayList<FactionType> facs = new ArrayList<>();
        for (String s : l) {
            facs.add(FactionType.valueOf(s.toUpperCase()));
        }
        return facs;
    }

    public ArrayList<UUID> arrayPy(List<String> l) {
        ArrayList<UUID> ps = new ArrayList<>();
        for (String s : l) {
            ps.add(UUID.fromString(s));
        }
        return ps;
    }

    public List<String> listFy(ArrayList<FactionType> ary) {
        List<String> stList = new ArrayList<>();
        for (FactionType ft : ary) {
            stList.add(ft.toString());
        }
        return stList;
    }

    public List<String> listPy(ArrayList<UUID> ary) {
        List<String> sList = new ArrayList<>();
        for (UUID ap : ary) {
            sList.add(ap.toString());
        }
        return sList;
    }

    public void addShitlist(AdvPlayer p, CapNPC npc) {
        for (FactionType ft : npc.factions) {
            Faction f = factions.get(ft);
            if (!f.playerEnemies.contains(p.getUserid())) {
                System.out.println("[MYOA] Making " + f.getName() + " enemies with " + p.name);
                f.playerEnemies.add(p.getUserid());
                updateTargets(f, p);
            } else {
                System.out.println("[MYOA] " + f.getName() + " already enemies with " + p);
            }
        }
    }

    public void updateTargets(Faction f, AdvPlayer p) {
        for (CapNPC c : a.npcm.npcs.values()) {
            if (c.factions.contains(f.type)) {
                SentinelTrait s = c.npc.getTrait(SentinelTrait.class);
                s.addTarget(p.getUserid());
                System.out.println("[MYOA] " + c.getName() + " now enemies with " + p.name);
            }
        }
    }
    public String getFactionName(CapNPC npc) {
        ArrayList<FactionType> fts = npc.factions;
        String name = npc.getName();
        if (!fts.isEmpty()) {
            for (FactionType ft : fts) {
                Faction f = factions.get(ft);
                if (!f.hidden) {
                    name = f.getName();
                }
            }
        }
        return name;
    }
}
