//Do want you want with this trash code :)
package me.captain.adv.npc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.captain.adv.AdvPlayer;
import me.captain.adv.Adventure;
import me.captain.adv.MYOA;
import me.captain.adv.dialogue.DialogueManager;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import me.captain.adv.faction.Faction;
import me.captain.adv.faction.FactionManager;
import me.captain.adv.faction.FactionType;
import me.captain.adv.quest.AdvPlayerProgress;
import me.captain.adv.quest.AdvQuest;
import me.captain.adv.quest.AdvStateType;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.util.SpigotUtil;
import net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.npc.skin.Skin;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.mcmonkey.sentinel.SentinelTrait;

/**
 *
 * @author andre
 */
public class NPCManager {

    private final MYOA plugin;
    private final Adventure adv;

    public NPCRegistry reg;
    public FactionManager fm;
    public DialogueManager dm;

    public HashMap<Integer, CapNPC> npcs;
    public HashMap<NPCCareerType, Career> careers;

    public NPCManager(MYOA instance, Adventure a) {
        plugin = instance;
        adv = a;
        npcs = new HashMap<>();
        reg = plugin.citizens.getNPCRegistry();
        fm = new FactionManager(a);
        dm = new DialogueManager(a);
        careers = new HashMap<>();
    }

    public void loadNPCS() {
        try {
            String path = adv.getPath();
            File f = new File(plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-npcs.yml");
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                for (String k : advf.getKeys(false)) {
                    int id = advf.getInt(k + ".id");
                    String name = advf.getString(k + ".name");
                    boolean killed = advf.getBoolean(k + ".killed");
                    double x = advf.getDouble(k + ".anchor.x");
                    double y = advf.getDouble(k + ".anchor.y");
                    double z = advf.getDouble(k + ".anchor.z");
                    List<Float> fl = advf.getFloatList(k + ".anchor.dir");
                    String worldname = advf.getString(k + ".anchor.world");
                    String career = advf.getString(k + ".career");
                    String ntype = advf.getString(k + ".type");
                    String property = advf.getString(k + ".property");
                    String uuid = advf.getString(k + ".uuid");
                    List<String> factions = advf.getStringList(k + ".factions");
                    Boolean skin = advf.getBoolean(k + ".skin.enabled");
                    NPCCareerType c = NPCCareerType.valueOf(career);
                    EntityType et = EntityType.valueOf(ntype);
                    World w = plugin.getServer().getWorld(worldname);
                    Float yaw = fl.get(0);
                    Float pitch = fl.get(1);
                    if (yaw.isInfinite() || yaw.isNaN()) {
                        yaw = (float) 1.0;
                    }
                    if (pitch.isInfinite() || pitch.isNaN()) {
                        pitch = (float) 1.0;
                    }
                    Location anchor = new Location(w, x, y, z, yaw, pitch);
                    CapNPC npc = new CapNPC(id, name);
                    if (skin) {
                        String tname = advf.getString(k + ".skin.name");
                        String tuuid = advf.getString(k + ".skin.uuid");
                        String tval = advf.getString(k + ".skin.texvalue");
                        String tsig = advf.getString(k + ".skin.texsig");
                        CapSkin cs = new CapSkin(tname, tuuid, tval, tsig, skin);
                        npc.skin = cs;
                    } else {
                        CapSkin cs = new CapSkin(null, null, null, null, false);
                        npc.skin = cs;
                    }
                    for (String s : factions) {
                        npc.factions.add(FactionType.valueOf(s.toUpperCase()));
                    }
                    npc.killed = killed;
                    npc.anchor = anchor;
                    npc.startGaze = anchor;
                    npc.career = c;
                    npc.npcType = et;
                    npc.uuid = UUID.fromString(uuid);
                    npcs.put(id, npc);
                    if (plugin.debug) {
                        System.out.println("[MYOA] Added NPC " + name + " to list");
                    }
                }
                System.out.println("[MYOA] Loaded " + adv.getName() + " NPCs");
            } else {

            }
        } catch (IOException ex) {
            Logger.getLogger(NPCManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidConfigurationException ex) {
            Logger.getLogger(NPCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadCareers() {
        for(NPCCareerType ct : NPCCareerType.values()) {
            careers.put(ct, new Career(ct));
        }
    }

    public void saveNPCS() {
        try {
            String path = adv.getPath();
            File f = new File(plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-npcs.yml");
            YamlConfiguration advf = new YamlConfiguration();
            if (npcs.isEmpty()) {
                System.out.println("[MYOA] No NPCs in " + adv.getName());
                return;
            }
            for (CapNPC npc : npcs.values()) {
                System.out.println("[MYOA] Saving NPC " + npc.getName());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".id", npc.getId());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".name", npc.getName());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".killed", npc.killed);
                Location l = npc.anchor;
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".anchor.x", l.getX());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".anchor.y", l.getY());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".anchor.z", l.getZ());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".anchor.world", l.getWorld().getName());
                List<Float> dir = new ArrayList<>();
                dir.add(l.getYaw());
                dir.add(l.getPitch());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".anchor.dir", dir);
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".type", npc.npcType.toString());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".uuid", npc.uuid.toString());
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".career", npc.career.toString());
                if (npc.skin.enabled) {
                    advf.set(npc.getName() + Integer.toString(npc.getId()) + ".skin.enabled", true);
                    advf.set(npc.getName() + Integer.toString(npc.getId()) + ".skin.name", npc.skin.texName);
                    advf.set(npc.getName() + Integer.toString(npc.getId()) + ".skin.uuid", npc.skin.texUUID);
                    advf.set(npc.getName() + Integer.toString(npc.getId()) + ".skin.texvalue", npc.skin.texValue);
                    advf.set(npc.getName() + Integer.toString(npc.getId()) + ".skin.texsig", npc.skin.texSig);
                } else {
                    advf.set(npc.getName() + Integer.toString(npc.getId()) + ".skin.enabled", false);
                }
                advf.set(npc.getName() + Integer.toString(npc.getId()) + ".factions", fm.listFy(npc.factions));
            }
            advf.save(f);
            System.out.println("[MYOA] Saved " + adv.getName() + " NPCs");
        } catch (IOException ex) {
            Logger.getLogger(NPCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void spawnNPCS() {
        purgeNPCs();
        for (CapNPC npc : npcs.values()) {
            if (npc.anchor != null && npc.career != null && npc.uuid != null && npc.startGaze != null && npc.npcType != null) {
                if (npc.killed == true) {
                    System.out.println("[MYOA] NPC " + npc.getName() + " is dead, not spawning him.. ");
                } else {
                    NPC add = newNPC(npc);
                    skinCap(npc);
                    createNPC(npc, add);
                    System.out.println("[MYOA] NPC " + npc.getName() + " spawned with id " + npc.getId());
                }
            } else {
                System.out.println("[MYOA] NPC " + npc.getName() + " missing info to spawn");
            }
        }
    }

    public NPC newNPC(CapNPC npc) {
        String name = npc.getName();
        ArrayList<FactionType> fts = npc.factions;
        boolean combat = false;
        if (!fts.isEmpty()) {
            for (FactionType ft : fts) {
                Faction f = fm.factions.get(ft);
                if (f.combat) {
                    combat = true;
                }
                if (!f.hidden) {
                    name = f.getName();
                }
            }
        }
        NPC nn = reg.createNPC(npc.npcType, npc.uuid, npc.getId(), name);
        npc.npc = nn;
        if (combat) {
            nn.addTrait(SentinelTrait.class);
            nn.addTrait(Equipment.class);
            equipNPC(npc);
        }
        return nn;
    }

    public void createNPC(CapNPC npc, NPC added) {
        added.spawn(npc.anchor);
        boolean on = added.getTrait(LookClose.class).toggle();
        if (!on) {
            added.getTrait(LookClose.class).toggle();
        }
        added.setProtected(false);
        added.data().setPersistent("protected", false);
        npc.spawned = true;
    }

    public void addNPC(CapNPC npc) {
        this.npcs.put(npc.getId(), npc);
    }

    public CapNPC getNPC(int id) {
        return npcs.get(id);
    }

    public void spawnUnspawned() {
        for (CapNPC npc : npcs.values()) {
            if (!npc.spawned) {
                if (npc.anchor != null && npc.career != null && npc.uuid != null && npc.startGaze != null && npc.npcType != null) {
                    NPC add = newNPC(npc);
                    skinCap(npc);
                    createNPC(npc, add);
                } else {
                    System.out.println("[MYOA] NPC " + npc.getName() + " missing info to spawn");
                }
            }
        }
    }

    public void purgeNPCs() {
        for (CapNPC npc : npcs.values()) {
            try {
                npc.npcEntity.die();
                npc.npc.destroy();
                reg.deregister(npc.npc);
                npc.npc = null;
                npc.spawned = false;
            } catch (Exception e) {

            }
            reg.deregisterAll();
            System.out.println("[MYOA] NPC " + npc.getName() + " purged");
        }
    }

    public void skinCap(CapNPC c) {
        //personal skins first
        if (c.skin != null) {
            System.out.println("[MYOA] Skinning " + c.getName() + " with custom skin");
            skinNPC(c, c.skin);
        } else {
            boolean skinned = false;
            for (FactionType f : c.factions) {
                if (!skinned) {
                    Faction fac = fm.factions.get(f);
                    if (fac.skin != null) {
                        System.out.println("[MYOA] Skinning " + c.getName() + " with faction skin");
                        skinNPC(c, fac.skin);
                        c.skin = fac.skin;
                        skinned = true;
                    }
                }
            }
        }
    }

    public void skinNPC(CapNPC cnpc, CapSkin skin) {
        NPC npc = cnpc.npc;
        if (skin.enabled) {
            npc.data().remove(NPC.PLAYER_SKIN_UUID_METADATA);
            npc.data().remove(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA);
            npc.data().remove(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA);
            npc.data().remove("cached-skin-uuid-name");
            npc.data().remove("cached-skin-uuid");
            npc.data().remove(NPC.PLAYER_SKIN_UUID_METADATA);
            npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);
            npc.data().set("cached-skin-uuid-name", skin.texName);
            npc.data().set("cached-skin-uuid", skin.texUUID);
            npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, skin.texName);
            npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, skin.texValue);
            npc.data().setPersistent(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, skin.texSig);
            if (npc.isSpawned()) {
                SkinnableEntity skinnable = npc.getEntity() instanceof SkinnableEntity ? (SkinnableEntity) npc.getEntity() : null;
                if (skinnable != null) {
                    System.out.println("[MYOA] NPC " + npc.getName() + " is spawned, respawning for skin");
                    skinnable.setSkinPersistent(cnpc.skin.texUUID, cnpc.skin.texSig, cnpc.skin.texValue);
                    Skin.get(skinnable).applyAndRespawn(skinnable);
                    npc.teleport(cnpc.anchor, TeleportCause.PLUGIN);
                }
            }
        }
    }

    public int getGiver(int i) {
        for (AdvQuest aq : adv.getQuests()) {
            if (aq.getGiver() == i) {
                return aq.getId();
            }
        }
        return 0;
    }

    public CapNPC getNPC(Entity e) {
        for (CapNPC npc : npcs.values()) {
            if (npc.npc != null) {
                if (npc.npc.isSpawned()) {
                    if (npc.npc.getEntity().equals(e)) {
                        return npc;
                    }
                }
            }
        }
        CapNPC c = new CapNPC(0, "none");
        return c;
    }

    public void equipNPC(CapNPC cc) {
        System.out.println("[MYOA] Equipping " + cc.getName());
        NPC npc = cc.npc;
        Equipment trait = npc.getTrait(Equipment.class);
        if (!cc.gear.isEmpty()) {
            ArrayList<ItemStack> personal = cc.gear;
            for (ItemStack is : personal) {
                EquipmentSlot slot;
                if (BOOTS.contains(is.getType())) {
                    slot = EquipmentSlot.BOOTS;
                } else if (CHESTPLATES.contains(is.getType())) {
                    slot = EquipmentSlot.CHESTPLATE;
                } else if (HELMETS.contains(is.getType())) {
                    slot = EquipmentSlot.HELMET;
                } else if (LEGGINGS.contains(is.getType())) {
                    slot = EquipmentSlot.LEGGINGS;
                } else {
                    slot = EquipmentSlot.HAND;
                }
                ItemStack clone = is.clone();
                clone.setAmount(1);
                trait.set(slot, clone);
            }
        } else {
            System.out.println("[MYOA] PERSONAL GEAR IS EMPTY ");
            for (FactionType ft : cc.factions) {
                Faction f = fm.factions.get(ft);
                if (!f.gear.isEmpty()) {
                    System.out.println("[MYOA] FACTION GEAR IS NOT EMPTY ");
                    for (ItemStack is : f.gear) {
                        EquipmentSlot slot;
                        if (BOOTS.contains(is.getType())) {
                            slot = EquipmentSlot.BOOTS;
                        } else if (CHESTPLATES.contains(is.getType())) {
                            slot = EquipmentSlot.CHESTPLATE;
                        } else if (HELMETS.contains(is.getType())) {
                            slot = EquipmentSlot.HELMET;
                        } else if (LEGGINGS.contains(is.getType())) {
                            slot = EquipmentSlot.LEGGINGS;
                        } else {
                            slot = EquipmentSlot.HAND;
                        }
                        ItemStack clone = is.clone();
                        clone.setAmount(1);
                        System.out.println("[MYOA] Setting " + npc.getName() + " item " + slot.toString());
                        trait.set(slot, clone);
                    }
                }
            }
        }
    }
    
    public void updateShouldStop() {
        for(CapNPC npc : npcs.values()) {
            for(AdvPlayer ap : adv.players) {
                ArrayList<AdvQuest> newq = new ArrayList<>();
                for(AdvQuest aq : ap.getQuests()) {
                    AdvPlayerProgress app = ap.quests.get(aq);
                    if(app.current.getNPC() == npc.getId()) {
                        if(app.current.type == AdvStateType.CONVERSE) {
                            newq.add(aq);
                        }
                    }
                }
                if(newq.size()>0) {
                    npc.shouldStop.put(ap.getUserid(), newq);
                }
            }
        }
    }

    private static final Set<Material> BOOTS = EnumSet.of(Material.CHAINMAIL_BOOTS, Material.DIAMOND_BOOTS,
            Material.IRON_BOOTS, Material.LEATHER_BOOTS,
            SpigotUtil.isUsing1_13API() ? Material.GOLD_BOOTS : Material.valueOf("GOLD_BOOTS"));
    private static final Set<Material> CHESTPLATES = EnumSet.of(Material.CHAINMAIL_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
            Material.IRON_CHESTPLATE, Material.LEATHER_CHESTPLATE,
            Material.GOLD_CHESTPLATE);
    private static final Set<Material> HELMETS = EnumSet.of(Material.PUMPKIN, Material.JACK_O_LANTERN, Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET,
            Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.valueOf("SKULL_ITEM"),
            Material.GOLD_HELMET);
    private static final Set<Material> LEGGINGS = EnumSet.of(Material.CHAINMAIL_LEGGINGS, Material.DIAMOND_LEGGINGS,
            Material.IRON_LEGGINGS, Material.LEATHER_LEGGINGS,
            Material.GOLD_LEGGINGS);
}
