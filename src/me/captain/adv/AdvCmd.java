//Do want you want with this trash code :)
package me.captain.adv;

import me.captain.adv.quest.AdvStateType;
import me.captain.adv.quest.AdvPlayerProgress;
import me.captain.adv.quest.AdvQuest;
import me.captain.adv.quest.AdvState;
import me.captain.adv.npc.CapNPC;
import me.captain.adv.npc.NPCCareerType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;
import me.captain.adv.faction.Faction;
import me.captain.adv.faction.FactionType;
import me.captain.adv.npc.CapSkin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;

/**
 * @author andrew b
 */
public class AdvCmd implements CommandExecutor {

    public MYOA plugin;

    public AdvCmd(MYOA instance) {
        plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmnd, String string, String[] args) {
        if ((sender instanceof Player)) {
            Player player = (Player) sender;
            if (args.length == 0) {
                player.sendMessage("[MYOA] Loaded adventures:");
                for (Adventure a : plugin.adventures) {
                    player.sendMessage(a.getId().toString() + " - " + a.getName());
                }
                return true;
            }
            if (args[0].equals("name")) {
                switch (args[1]) {
                    case "loc": {
                        int aid = Integer.parseInt(args[2]);
                        int lid = Integer.parseInt(args[3]);
                        Adventure a = plugin.getAdventure(aid);
                        AdvLocation al = a.getLoc(lid);
                        String fname = name(args, 4);
                        al.setName(fname);
                        player.sendMessage(ChatColor.GRAY + "Set name of location " + ChatColor.GREEN + al.getId() + ChatColor.GRAY + " to " + ChatColor.GREEN + al.getName());
                        return true;
                    }
                    case "quest": {
                        int aid = Integer.parseInt(args[2]);
                        int qid = Integer.parseInt(args[3]);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(qid);
                        String fname = name(args, 4);
                        aq.setName(fname);
                        player.sendMessage(ChatColor.GRAY + "Set name of quest " + ChatColor.GREEN + aq.getId() + ChatColor.GRAY + " to " + ChatColor.GREEN + aq.getName());
                        return true;
                    }
                    case "state": {
                        int aid = Integer.parseInt(args[2]);
                        int qid = Integer.parseInt(args[3]);
                        int sid = Integer.parseInt(args[4]);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(qid);
                        AdvState as = aq.getState(sid);
                        String fname = name(args, 5);
                        as.setName(fname);
                        player.sendMessage(ChatColor.GRAY + "Set name of state " + ChatColor.GREEN + as.getId() + ChatColor.GRAY + " to " + ChatColor.GREEN + as.getName());
                        return true;
                    }
                    case "npc": {
                        Adventure a = plugin.getAdventure(player);
                        CapNPC npc = a.npcm.getNPC(Integer.parseInt(args[2]));
                        String name = name(args, 3);
                        npc.setName(name);
                        a.npcm.spawnUnspawned();
                        player.sendMessage(ChatColor.GRAY + "NPC " + ChatColor.GREEN + npc.getId() + ChatColor.GRAY + " name updated: " + ChatColor.GREEN + npc.getName());
                        return true;
                    }
                    default:
                        break;
                }
            } else if (args[0].equals("meta")) {
                if (args[1].equals("loc")) {
                    int aid = Integer.parseInt(args[2]);
                    int lid = Integer.parseInt(args[3]);
                    Adventure a = plugin.getAdventure(aid);
                    AdvLocation al = a.getLoc(lid);
                    String fmeta = name(args, 4);
                    al.addFlavor(fmeta);
                    player.sendMessage(ChatColor.GRAY + "Added flavor to " + ChatColor.GREEN + al.getId() + ChatColor.GRAY + ": " + ChatColor.GREEN + fmeta);
                    return true;
                } else if (args[1].equals("state")) {
                    int aid = Integer.parseInt(args[2]);
                    int qid = Integer.parseInt(args[3]);
                    int sid = Integer.parseInt(args[4]);
                    Adventure a = plugin.getAdventure(aid);
                    AdvQuest aq = a.getQuest(qid);
                    AdvState as = aq.getState(sid);
                    String fmeta = name(args, 5);
                    as.addMeta(fmeta);
                    player.sendMessage(ChatColor.GRAY + "Added flavor to " + ChatColor.GREEN + as.getId() + ChatColor.GRAY + ": " + ChatColor.GREEN + fmeta);
                    return true;
                }
            } else if (args[0].equals("npc")) {
                if (args.length == 1) {
                    Adventure a = plugin.getAdventure(player);
                    Collection<CapNPC> npcs = a.npcm.npcs.values();
                    String msg = ChatColor.GRAY + "=== Current NPCs in " + ChatColor.GREEN + a.getName() + ChatColor.GRAY + " ===";
                    String msg2 = ChatColor.GRAY + "[";
                    for (CapNPC npc : npcs) {
                        msg2 += "" + ChatColor.GREEN + npc.getId() + ChatColor.GRAY + "-" + ChatColor.GREEN + npc.getName() + ChatColor.GRAY + ",";
                    }
                    msg2 = msg2.substring(0, msg2.length() - 1);
                    msg2 += "]";
                    player.sendMessage(msg);
                    player.sendMessage(msg2);
                    return true;
                } else {
                    if (args[1].equals("new")) {
                        try {
                            int npcid = plugin.getAdventure(player).npcm.npcs.size() + 1;
                            String cs = args[2].toUpperCase();
                            String ts = args[3].toUpperCase();
                            String name = name(args, 4);
                            NPCCareerType c = NPCCareerType.valueOf(cs);
                            EntityType et = EntityType.valueOf(ts);
                            CapNPC npc = new CapNPC(npcid, name);
                            npc.anchor = player.getLocation();
                            npc.startGaze = player.getLocation();
                            npc.career = c;
                            npc.skin = new CapSkin();
                            npc.npcType = et;
                            npc.uuid = UUID.randomUUID();
                            plugin.getAdventure(player).npcm.npcs.put(npcid, npc);
                            plugin.getAdventure(player).npcm.spawnUnspawned();
                            player.sendMessage(ChatColor.GRAY + "Creating NPC " + ChatColor.GREEN + npcid + ChatColor.GRAY + " - " + ChatColor.GREEN + npc.getName());
                            return true;
                        } catch (Exception e) {
                            player.sendMessage("Try /adv npc new career type");
                        }
                    }
                }
                switch (args[1]) {
                    case "respawn":
                        for (Adventure a : plugin.adventures) {
                            for (CapNPC npc : a.npcm.npcs.values()) {
                                npc.npc.despawn();
                                npc.npc.spawn(npc.anchor);
                            }
                        }
                        return true;
                    case "copy":
                        int cccc = Integer.parseInt(args[2]);
                        String faction = args[3];
                        CapNPC nnnnpc = plugin.getAdventure(player).npcm.getNPC(cccc);
                        Faction f = plugin.getAdventure(player).npcm.fm.factions.get(FactionType.valueOf(faction.toUpperCase()));
                        NPC n = nnnnpc.npc;
                        Equipment trait = n.getTrait(Equipment.class);
                        f.gear.clear();
                        for (int i = 0; i < 6; i++) {
                            if (trait.get(i) != null) {
                                f.gear.add(trait.get(i));
                            }
                        }
                        player.sendMessage(ChatColor.GRAY + "Copied " + n.getName() + "'s " + f.gear.size() + " items to " + f.getName());
                        return true;
                    case "add":
                        int aid = Integer.parseInt(args[2]);
                        int addid = Integer.parseInt(args[3]);
                        String addname = name(args, 4);
                        Adventure a = plugin.getAdventure(aid);
                        CapNPC npc = new CapNPC(addid, addname);
                        a.npcm.addNPC(npc);
                        player.sendMessage(ChatColor.GRAY + "Added NPC " + ChatColor.GREEN + addname + ChatColor.GRAY + " to " + a.getName());
                        return true;
                    case "career":
                        int cadvid = Integer.parseInt(args[2]);
                        int cid = Integer.parseInt(args[3]);
                        String cc = args[4].toUpperCase();
                        Adventure ca = plugin.getAdventure(cadvid);
                        CapNPC cnpc = ca.npcm.getNPC(cid);
                        NPCCareerType career = NPCCareerType.valueOf(cc);
                        cnpc.career = career;
                        player.sendMessage(ChatColor.GRAY + "Set career of " + ChatColor.GREEN + cnpc.getName() + ChatColor.GRAY + " to " + career.toString());
                        ca.npcm.spawnUnspawned();
                        return true;
                    case "type":
                        int tadvid = Integer.parseInt(args[2]);
                        int tid = Integer.parseInt(args[3]);
                        String tc = args[4].toUpperCase();
                        Adventure ta = plugin.getAdventure(tadvid);
                        CapNPC tnpc = ta.npcm.getNPC(tid);
                        EntityType et = EntityType.valueOf(tc);
                        tnpc.npcType = et;
                        player.sendMessage(ChatColor.GRAY + "Set type of " + ChatColor.GREEN + tnpc.getName() + ChatColor.GRAY + " to " + et.toString());
                        ta.npcm.spawnUnspawned();
                        return true;
                    case "uuid":
                        int uad = Integer.parseInt(args[2]);
                        int un = Integer.parseInt(args[3]);
                        Adventure ua = plugin.getAdventure(uad);
                        CapNPC unp = ua.npcm.getNPC(un);
                        UUID uid = UUID.randomUUID();
                        unp.uuid = uid;
                        player.sendMessage(ChatColor.GRAY + "Set uuid of " + ChatColor.GREEN + unp.getName() + ChatColor.GRAY + " to " + uid.toString());
                        ua.npcm.spawnUnspawned();
                        return true;
                    case "anchor":
                        int aaid = Integer.parseInt(args[2]);
                        int anid = Integer.parseInt(args[3]);
                        Adventure aadv = plugin.getAdventure(aaid);
                        CapNPC anpc = aadv.npcm.getNPC(anid);
                        anpc.anchor = player.getLocation();
                        player.sendMessage(ChatColor.GRAY + "Set anchor of " + ChatColor.GREEN + anpc.getName() + ChatColor.GRAY + " to your current location");
                        aadv.npcm.spawnUnspawned();
                        return true;
                    case "gaze":
                        int gaid = Integer.parseInt(args[2]);
                        int gnid = Integer.parseInt(args[3]);
                        Adventure gadv = plugin.getAdventure(gaid);
                        CapNPC gnpc = gadv.npcm.getNPC(gnid);
                        gnpc.startGaze = player.getLocation();
                        player.sendMessage(ChatColor.GRAY + "Set gaze of " + ChatColor.GREEN + gnpc.getName() + ChatColor.GRAY + " to your current location");
                        gadv.npcm.spawnUnspawned();
                        return true;
                    case "list":
                        int laid = Integer.parseInt(args[2]);
                        Adventure ladv = plugin.getAdventure(laid);
                        for (CapNPC lnpc : ladv.npcm.npcs.values()) {
                            player.sendMessage("" + ChatColor.GREEN + lnpc.getId() + ChatColor.GRAY + " - " + ChatColor.GREEN + lnpc.getName());
                        }
                        break;
                    case "info":
                        int iaid = Integer.parseInt(args[2]);
                        int inid = Integer.parseInt(args[3]);
                        CapNPC inpc = plugin.getAdventure(iaid).npcm.getNPC(inid);
                        player.sendMessage("" + ChatColor.GREEN + inpc.getId() + ChatColor.GRAY + " - " + ChatColor.GREEN + inpc.getName());
                }
            }
            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "live":
                            AdvPlayer sap = plugin.getAdventure(player).getPlayer(player.getUniqueId());
                            ArrayList<String> qlist = new ArrayList();
                            if (sap.getQuests().isEmpty()) {
                                player.sendMessage(ChatColor.GRAY + "No live quests");
                            } else {
                                for (AdvQuest sq : sap.getQuests()) {
                                    String msg = ChatColor.GREEN + sq.getName() + ChatColor.GRAY + "[";
                                    AdvState ste = sap.quests.get(sq).current;
                                    msg += ChatColor.BLUE + upperOne(ste.type.toString()) + ChatColor.GRAY + "-" + ChatColor.RED + String.valueOf(ste.getId()) + ChatColor.GRAY + "]";
                                    qlist.add(msg);
                                }
                                String fmsg = ChatColor.GRAY + "Live Quests: ";
                                for (String strs : qlist) {
                                    fmsg += strs + ", ";
                                }
                                fmsg = fmsg.substring(0, fmsg.length() - 2);
                                player.sendMessage(fmsg);
                            }
                            break;
                        case "char":
                            AdvPlayer ap = plugin.getAdventure(player).getPlayer(player.getUniqueId());
                            player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + ap.name);
                            player.sendMessage(ChatColor.GRAY + "Class: " + ChatColor.GREEN + upperOne(ap.chartype.toString()));
                            player.sendMessage(ChatColor.GRAY + "Xp: " + ChatColor.GREEN + ap.xp);
                            player.sendMessage(ChatColor.GRAY + "Level: " + ChatColor.GREEN + ap.getLevel());
                            player.sendMessage(ChatColor.GRAY + "Xp to Next: " + ChatColor.GREEN + ap.nextlevel());
                            break;
                        case "locations":
                            ArrayList<String> locs = new ArrayList<>();
                            for (AdvLocation al : plugin.getAdventure(player).getLocations()) {
                                String s = ChatColor.GREEN + al.getName() + ChatColor.GRAY;
                                locs.add(s);
                            }
                            String msg = join(locs, ",");
                            player.sendMessage(ChatColor.GRAY + "Locations: [" + msg + "]");
                            break;
                        case "quests":
                            Adventure add = plugin.getAdventure(player);
                            ArrayList<String> quests = new ArrayList<>();
                            AdvPlayer advp = add.getPlayer(player.getUniqueId());
                            ArrayList<AdvQuest> q = (ArrayList) add.getQuests().clone();
                            for (AdvQuest aq : advp.getQuests()) {
                                quests.add(ChatColor.GREEN + aq.getName() + ChatColor.GRAY);
                                q.remove(add.getQuest(aq.getId()));
                            }
                            for (AdvQuest aq : q) {
                                quests.add(ChatColor.RED + aq.getName() + ChatColor.GRAY);
                            }
                            msg = join(quests, ",");
                            player.sendMessage(ChatColor.GRAY + "Quests: [" + msg + "]");
                            break;
                        case "zones":
                            ArrayList<String> zones = new ArrayList<>();
                            for (AdvZone az : plugin.getAdventure(player).getZones()) {
                                String s = ChatColor.GREEN + az.getName() + ChatColor.GRAY;
                                zones.add(s);
                            }
                            msg = join(zones, ",");
                            player.sendMessage(ChatColor.GRAY + "Zones: [" + msg + "]");
                            break;
                        case "perks":
                            //todo
                            break;
                        case "players":
                            ArrayList<String> players = new ArrayList<>();
                            for (Adventure a : plugin.adventures) {
                                for (AdvPlayer aps : a.players) {
                                    String s = ChatColor.GREEN + aps.name + ChatColor.GRAY;
                                    players.add(s);
                                }
                            }
                            msg = join(players, ",");
                            player.sendMessage(ChatColor.GRAY + "Players: [" + msg + "]");
                            break;
                        case "save":
                            if (player.hasPermission("myoa.*")) {
                                plugin.saveAdvs();
                                plugin.saveNPCS();
                                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "MYOA" + ChatColor.GRAY + "]: Adventures saved");
                            }
                            break;
                        default:
                            break;
                    }

                case 2:
                    if (args[0].equals("start")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Adventure a = plugin.getAdventure(aid);
                        if (a != null) {
                            AdvPlayer ap = a.getPlayer(player.getUniqueId());
                            if (ap == null) {
                                ap = new AdvPlayer(player.getUniqueId(), 0);
                                ap.name = player.getDisplayName();
                                ap.chartype = AdvCharType.RANGER;
                                a.players.add(ap);
                                player.sendMessage(ChatColor.GRAY + "Manually started: " + ChatColor.GREEN + a.getName());
                            } else {
                                player.sendMessage(ChatColor.GRAY + "You are already in " + ChatColor.GREEN + a.getName());
                            }
                        }
                    } else if (args[0].equals("class")) {
                        String ctype = args[1].toUpperCase();
                        AdvCharType act = AdvCharType.valueOf(ctype);
                        AdvPlayer ap = plugin.getAdventure(player).getPlayer(player.getUniqueId());
                        ap.chartype = act;
                        player.sendMessage(ChatColor.GRAY + "You are now a " + ChatColor.GREEN + upperOne(act.toString()));
                    } else if (args[0].equals("states")) {
                        int qid = Integer.parseInt(args[1]);
                        Adventure a = plugin.getAdventure(player);
                        AdvPlayer ap = a.getPlayer(player.getUniqueId());
                        AdvQuest aq = a.getQuest(qid);
                        ArrayList<String> states = new ArrayList();
                        for (AdvState as : aq.getStates()) {
                            String msg = ChatColor.GRAY + as.getName() + "[" + ChatColor.GREEN + as.getId() + ChatColor.GRAY + "-" + ChatColor.GREEN + upperOne(as.getType().toString()) + ChatColor.GRAY + "]";
                            states.add(msg);
                        }
                        String tmsg = ChatColor.GRAY + "States: ";
                        for (String s : states) {
                            tmsg += s + ", ";
                        }
                        tmsg = tmsg.substring(0, tmsg.length() - 2);
                        player.sendMessage(tmsg);
                    }
                    break;
                case 3:
                    if (args[0].equals("start")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(Integer.parseInt(args[2]));
                        AdvPlayer ap = a.getPlayer(player.getUniqueId());
                        AdvPlayerProgress app = new AdvPlayerProgress(a, aq, aq.getStart());
                        app.player = player;
                        app.ap = ap;
                        player.sendMessage(ChatColor.GRAY + "Manually started quest: " + ChatColor.GREEN + aq.getName());
                        ap.quests.put(aq, app);
                        app.pre();
                    }
                case 4:
                    if (args[0].equals("info")) {
                        Integer aid = Integer.parseInt(args[2]);
                        Adventure a = plugin.getAdventure(aid);
                        switch (args[1]) {
                            case "loc": {
                                AdvLocation loc = a.getLoc(Integer.parseInt(args[3]));
                                player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + loc.getName());
                                player.sendMessage(ChatColor.GRAY + "Adventure: " + ChatColor.GREEN + a.getName());
                                Location l = loc.getLocation();
                                String s = xyz(l);
                                player.sendMessage(ChatColor.GRAY + "World: " + ChatColor.GREEN + l.getWorld().getName() + ChatColor.GRAY + " XYZ: " + s);
                                if (!loc.getFlavor().isEmpty()) {
                                    player.sendMessage(ChatColor.GRAY + "Message: " + loc.getFlavor().get(0));
                                }
                                break;
                            }
                            case "tp":
                                AdvTeleport tp = a.getTeleport(Integer.parseInt(args[3]));
                                player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + tp.getName());
                                player.sendMessage(ChatColor.GRAY + "Adventure: " + ChatColor.GREEN + a.getName());
                                player.sendMessage(ChatColor.GRAY + "Entrance Zone: " + ChatColor.GREEN + a.getZone(tp.getZone()).getName());
                                player.sendMessage(ChatColor.GRAY + "Exit Location: " + ChatColor.GREEN + a.getLoc(tp.getLoc()).getName());
                                break;
                            case "zone": {
                                AdvZone zone = a.getZone(Integer.parseInt(args[3]));
                                player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + zone.getName());
                                player.sendMessage(ChatColor.GRAY + "Adventure: " + ChatColor.GREEN + a.getName());
                                Location l = zone.getLoc1();
                                Location n = zone.getLoc2();
                                String s = xyz(l);
                                String s2 = xyz(n);
                                player.sendMessage(ChatColor.GRAY + "World: " + ChatColor.GREEN + l.getWorld().getName());
                                player.sendMessage(ChatColor.GRAY + "Coords: " + s + " " + s2);
                                break;
                            }
                            case "quest":
                                AdvQuest q = a.getQuest(Integer.parseInt(args[3]));
                                player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + q.getName());
                                player.sendMessage(ChatColor.GRAY + "Adventure: " + ChatColor.GREEN + a.getName());
                                ArrayList<String> states = new ArrayList<>();
                                for (AdvState s : q.getStates()) {
                                    states.add(ChatColor.GREEN + s.getType().toString() + ChatColor.GRAY + "-" + ChatColor.GREEN + s.getName() + ChatColor.GRAY);
                                }
                                player.sendMessage(ChatColor.GRAY + "States: [" + join(states, ",") + "]");
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case 5:
                    if (args[0].equals("new")) {
                        if (args[1].equals("loc")) {
                            String name = args[2];
                            Integer id;
                            Integer aid;
                            try {
                                id = Integer.parseInt(args[3]);
                                aid = Integer.parseInt(args[4]);
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.GRAY + "Try /adv new loc name id adventureid");
                                return true;
                            }
                            Adventure a = plugin.getAdventure(aid);
                            AdvLocation loc = new AdvLocation(name, id, player.getLocation(), null);
                            a.addLocation(loc);
                            player.sendMessage(ChatColor.GRAY + "Added location " + ChatColor.GREEN + loc.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + a.getName());
                            return true;

                        } else if (args[1].equals("sign")) {
                            String name = args[2];
                            Integer id;
                            Integer aid;
                            try {
                                id = Integer.parseInt(args[3]);
                                aid = Integer.parseInt(args[4]);
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.GRAY + "Try /adv new sign name id adventureid");
                                return true;
                            }
                            Creating c = new Creating(name, id, aid);
                            plugin.cp.put(player, c);
                            player.sendMessage(ChatColor.GRAY + "Added sign " + name + ", click the sign with a diamond to continue");
                        } else if (args[1].equals("quest")) {
                            Integer aid = Integer.parseInt(args[2]);
                            Integer qid = Integer.parseInt(args[3]);
                            Integer xp = Integer.parseInt(args[4]);
                            AdvQuest aq = new AdvQuest(aid, qid, "temp-" + qid.toString(), 0, xp, null, null);
                            Adventure a = plugin.getAdventure(aid);
                            a.addQuest(aq);
                            player.sendMessage(ChatColor.GRAY + "Added quest " + ChatColor.GREEN + aq.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + a.getName());
                        }
                    } else if (args[0].equals("info")) {
                        Integer aid = Integer.parseInt(args[2]);
                        Adventure a = plugin.getAdventure(aid);
                        if (args[1].equals("state")) {
                            AdvQuest q = a.getQuest(Integer.parseInt(args[3]));
                            AdvState s = q.getState(Integer.parseInt(args[4]));
                            player.sendMessage(ChatColor.GRAY + "Name: " + ChatColor.GREEN + s.getName());
                            player.sendMessage(ChatColor.GRAY + "Quest: " + ChatColor.GREEN + q.getName());
                            player.sendMessage(ChatColor.GRAY + "Adventure: " + ChatColor.GREEN + a.getName());
                            player.sendMessage(ChatColor.GRAY + "Type: " + ChatColor.GREEN + s.getType().toString());
                            int l = s.getLoc();
                            if (l != 0) {
                                player.sendMessage(ChatColor.GRAY + "Location: " + ChatColor.GREEN + l);
                            }
                            int n = s.getNPC();
                            if (n != 0) {
                                player.sendMessage(ChatColor.GRAY + "NPC: " + ChatColor.GREEN + n);
                            }
                            int z = s.getZone();
                            if (z != 0) {
                                player.sendMessage(ChatColor.GRAY + "Zone: " + ChatColor.GREEN + z);
                            }
                            ItemStack items = s.getItems();
                            if (items != null) {
                                if (null != s.getType()) {
                                    switch (s.getType()) {
                                        case PLACE:
                                            player.sendMessage(ChatColor.GRAY + "Blocks to place: " + ChatColor.GREEN + items.getAmount() + " " + items.getType().toString());
                                            break;
                                        case BREAK:
                                            player.sendMessage(ChatColor.GRAY + "Blocks to break: " + ChatColor.GREEN + items.getAmount() + " " + items.getType().toString());
                                            break;
                                        case STORE:
                                            player.sendMessage(ChatColor.GRAY + "Items to store: " + ChatColor.GREEN + items.getAmount() + " " + items.getType().toString());
                                            AdvLocation al = plugin.getAdventure(aid).getLoc(s.getLoc());
                                            Location loc = al.getLocation();
                                            player.sendMessage(ChatColor.GRAY + "Plcae to store: " + xyz(loc));
                                            break;
                                        case GATHER:
                                            player.sendMessage(ChatColor.GRAY + "Items to gather: " + ChatColor.GREEN + items.getAmount() + " " + items.getType().toString());
                                            break;
                                        case HANDOFF:
                                            player.sendMessage(ChatColor.GRAY + "Items to handoff: " + ChatColor.GREEN + items.getAmount() + " " + items.getType().toString());
                                            if (!s.getMeta().isEmpty()) {
                                                player.sendMessage(ChatColor.GRAY + "Give items to: " + ChatColor.GREEN + s.getMeta().get(0));
                                            }
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    } else if (args[0].equals("reward")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Integer qid = Integer.parseInt(args[2]);
                        Material mat = Material.valueOf(args[3].toUpperCase());
                        Integer amt = Integer.parseInt(args[4]);
                        ItemStack rw = new ItemStack(mat, amt);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(qid);
                        aq.setRewardItems(rw);
                        player.sendMessage(ChatColor.GRAY + "Set reward items of " + ChatColor.GREEN + aq.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + rw.getAmount() + " " + mat.name());
                    } else if (args[0].equals("required")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Integer qid = Integer.parseInt(args[2]);
                        Material mat = Material.valueOf(args[3].toUpperCase());
                        Integer amt = Integer.parseInt(args[4]);
                        ItemStack rw = new ItemStack(mat, amt);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(qid);
                        aq.setRequiredItems(rw);
                        player.sendMessage(ChatColor.GRAY + "Set required items of " + ChatColor.GREEN + aq.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + rw.getAmount() + " " + mat.name());
                    } else if (args[0].equals("zone")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Integer qid = Integer.parseInt(args[2]);
                        Integer sid = Integer.parseInt(args[3]);
                        Integer zid = Integer.parseInt(args[4]);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(qid);
                        AdvState as = aq.getState(sid);
                        AdvZone az = a.getZone(zid);
                        if (az != null) {
                            as.setZone(zid);
                            player.sendMessage(ChatColor.GRAY + "Set zone of " + ChatColor.GREEN + as.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + az.getName());
                        }
                    } else if (args[0].equals("loc")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Integer qid = Integer.parseInt(args[2]);
                        Integer sid = Integer.parseInt(args[3]);
                        Integer zid = Integer.parseInt(args[4]);
                        Adventure a = plugin.getAdventure(aid);
                        AdvQuest aq = a.getQuest(qid);
                        AdvState as = aq.getState(sid);
                        AdvLocation al = a.getLoc(zid);
                        if (al != null) {
                            as.setLoc(zid);
                            player.sendMessage(ChatColor.GRAY + "Set location of " + ChatColor.GREEN + as.getName() + ChatColor.GRAY + " to " + ChatColor.GREEN + al.getName());
                        }
                    }
                    break;
                case 7:
                    if (args[0].equals("new")) {
                        if (args[1].equals("zone")) {
                            String name = args[2];
                            String node = args[3];
                            Integer id = Integer.parseInt(args[4]);
                            Integer offset = Integer.parseInt(args[5]);
                            Integer aid = Integer.parseInt(args[6]);
                            Creating cz = new Creating(name, node, id, offset, aid);
                            plugin.cp.put(player, cz);
                        } else if (args[1].equals("state")) {
                            Integer advid = Integer.parseInt(args[2]);
                            Integer qid = Integer.parseInt(args[3]);
                            Integer sid = Integer.parseInt(args[4]);
                            Integer nsid = Integer.parseInt(args[5]);
                            AdvStateType type = AdvStateType.valueOf(args[6].toUpperCase());
                            Adventure a = plugin.getAdventure(advid);
                            AdvQuest aq = a.getQuest(qid);
                            AdvState as = new AdvState(a, sid, "", nsid, type, new ArrayList<>());
                            aq.addState(as);
                            player.sendMessage(ChatColor.GRAY + "New state added to " + ChatColor.GREEN + aq.getName());
                        } else if (args[1].equals("tp")) {
                            String name = args[2];
                            Integer id;
                            Integer zid;
                            Integer lid;
                            Integer adid;
                            try {
                                id = Integer.parseInt(args[3]);
                                adid = Integer.parseInt(args[4]);
                                lid = Integer.parseInt(args[5]);
                                zid = Integer.parseInt(args[6]);
                            } catch (Exception e) {
                                player.sendMessage(ChatColor.GRAY + "Try /adv new tp name id advid locid zoneid");
                                return true;
                            }
                            Adventure a = plugin.getAdventure(adid);
                            AdvTeleport at = new AdvTeleport(a.getId(), name, id, lid, zid);
                            a.addTeleport(at);
                            player.sendMessage(ChatColor.GRAY + "Teleport " + ChatColor.GREEN + at.getName() + ChatColor.GRAY + " added to " + ChatColor.GREEN + a.getName());
                            return true;
                        }
                    }
                    break;
            }
        } else {
            switch (args.length) {
                case 0:
                    sender.sendMessage("[MYOA] Loaded adventures:");
                    for (Adventure a : plugin.adventures) {
                        sender.sendMessage(a.getId().toString() + " - " + a.getName());
                    }
                    break;
                case 2:
                    if (args[0].equals("players")) {
                        Integer aid = Integer.parseInt(args[1]);
                        Adventure a = plugin.getAdventure(aid);
                        if (a.players.isEmpty()) {
                            sender.sendMessage("[MYOA] " + a.getName() + " has no current players");
                            break;
                        }
                        sender.sendMessage("[MYOA] " + a.getName() + " currrent players:");
                        String plist = "[";
                        for (AdvPlayer p : a.players) {
                            plist += p.name + ",";
                        }
                        plist = plist.substring(0, plist.length() - 1);
                        plist += "]";
                        sender.sendMessage(plist);
                    }
                    break;
            }
        }
        return true;
    }

    public String upperOne(String s) {
        String ctypec = s.substring(0, 1);
        ctypec = ctypec.toUpperCase();
        s = s.substring(1).toLowerCase();
        String ctypef = ctypec + s;
        return ctypef;
    }

    public String name(String[] args, int p) {
        ArrayList<String> namelist = new ArrayList<>();
        namelist.addAll(Arrays.asList(args).subList(p, args.length));
        String name = "";
        name = namelist.stream().map((s) -> s + " ").reduce(name, String::concat);
        String fname = name.substring(0, name.length() - 1);
        return fname;
    }

    public String join(Collection collection, String delimiter) {
        return (String) collection.stream()
                .map(Object::toString)
                .collect(Collectors.joining(delimiter));
    }

    public String xyz(Location loc) {
        String str = ChatColor.GRAY + "[" + ChatColor.GREEN + loc.getBlockX() + ChatColor.GRAY + "," + ChatColor.GREEN + loc.getBlockY() + ChatColor.GRAY + "," + ChatColor.GREEN + loc.getBlockZ() + ChatColor.GRAY + "]";
        return str;
    }
}
