//Do want you want with this trash code :)
package me.captain.adv.properties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.captain.adv.AdvPlayer;
import me.captain.adv.AdvZone;
import me.captain.adv.Adventure;
import me.captain.adv.npc.CapNPC;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class PropertyManager {

    public Adventure a;

    public HashMap<CapNPC, Property> properties;

    public PropertyManager(Adventure a) {
        this.a = a;
        properties = new HashMap<>();
    }

    public void loadProperties() {
        try {
            System.out.println("[MYOA] Loading properties");
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-properties.yml");
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                for (String k : advf.getKeys(false)) {
                    Integer id = advf.getInt(k + ".id");
                    String name = advf.getString(k + ".name");
                    Integer owner = advf.getInt(k + ".owner");
                    Integer zone = advf.getInt(k + ".zone");
                    Integer hold = advf.getInt(k + ".hold");
                    int itemAmt = advf.getInt(k + ".itemstacks");
                    ArrayList<ItemStack> items = new ArrayList();
                    Inventory inv = a.plugin.getServer().createInventory(null, 45);
                    if (itemAmt > 0) {
                        for (int i = 0; i < itemAmt; i++) {
                            items.add(advf.getItemStack(k + ".inventory." + i));
                        }
                        for (ItemStack item : items) {
                            try {
                                inv.addItem(item);
                            } catch (IllegalArgumentException e) {

                            }
                        }
                    }
                    List<Integer> ass = advf.getIntegerList(k + ".associates");
                    Property prop = new Property(id, owner, name);
                    prop.inv = inv;
                    AdvZone z = a.getZone(zone);
                    if (z != null) {
                        prop.zone = z;
                    }
                    AdvZone h = a.getZone(hold);
                    if (hold != null) {
                        prop.hold = h;
                    }
                    CapNPC npc = a.npcm.getNPC(owner);
                    for (Integer asc : ass) {
                        CapNPC ascn = a.npcm.getNPC(asc);
                        if (ascn != null) {
                            prop.associates.add(ascn);
                        }
                    }
                    this.properties.put(npc, prop);
                }
            }
            System.out.println("[MYOA] Loaded properties");
        } catch (Exception e) {
            System.out.println("[MYOA] Error loading properties");
            e.printStackTrace();
        }
    }

    public void saveProperties() {
        try {
            System.out.println("[MYOA] Saving properties");
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-properties.yml");
            YamlConfiguration advf = new YamlConfiguration();
            for (CapNPC npc : properties.keySet()) {
                Property p = properties.get(npc);
                advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".name", p.getName());
                advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".owner", p.getOwner());
                if (p.zone != null) {
                    advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".zone", p.zone.getId());
                }
                if (p.hold != null) {
                    advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".hold", p.hold.getId());
                }
                Inventory inv = p.inv;
                int itemAmt = inv.getSize();
                advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".itemstacks", itemAmt);
                advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".associates", p.associates);
                for (int i = 0; i < itemAmt; i++) {
                    advf.set(npc.getId() + "-" + npc.getName() + "-" + p.getName() + ".inventory." + i, inv.getItem(i));
                }
            }
            advf.save(f);
            System.out.println("[MYOA] Saving properties");
        } catch (IOException ex) {
            System.out.println("[MYOA] Error saving properties");
            Logger.getLogger(PropertyManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }

    public void serveInventory(AdvPlayer player, CapNPC npc) {
        if (properties.containsKey(npc)) {
            player.player.openInventory(properties.get(npc).inv);
        } else {
            System.out.println("[MYOA] This shop has no inventory");
        }
    }
}
