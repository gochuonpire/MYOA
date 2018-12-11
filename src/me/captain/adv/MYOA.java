package me.captain.adv;

/**
 * @author andrew b
 */
import me.captain.adv.quest.AdvStateType;
import me.captain.adv.quest.AdvPlayerProgress;
import me.captain.adv.quest.AdvQuest;
import me.captain.adv.quest.AdvState;
import me.captain.adv.npc.CapNPC;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import me.captain.adv.dialogue.FlagType;
import me.captain.adv.dialogue.Line;
import me.captain.adv.dialogue.ResponseType;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.entity.Player;
import me.captain.chat.CaptainChat;
import me.captain.chat.core.ChatChannel;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.npc.NPC;
import org.mcmonkey.sentinel.SentinelPlugin;

public class MYOA extends JavaPlugin {

    public CaptainChat chat;
    public SentinelPlugin sentinel;
    public Citizens citizens;
    public ArrayList<Adventure> adventures;
    public Adventure defaultAdv;
    public ArrayList<String> paths;
    public HashMap<Player, Creating> cp;
    public Connection conn;
    public boolean debug = true;
    private long chatdelay = 20;

    @Override
    public void onEnable() {
        cp = new HashMap<>();
        PluginManager pluginm = getServer().getPluginManager();
        chat = null;
        citizens = null;
        try {
            chat = (CaptainChat) pluginm.getPlugin("CaptainChat");
            System.out.println("[MYOA] CaptainChat hooked for NPC messages");
            citizens = (Citizens) pluginm.getPlugin("Citizens");
            System.out.println("[MYOA] Citizens hooked for NPC creation");
            sentinel = (SentinelPlugin) pluginm.getPlugin("Sentinel");
            System.out.println("[MYOA] Sentinel hooked for NPC behavior");
        } catch (Exception e) {
            this.getServer().getConsoleSender().sendMessage("[MYOA] CaptainChat hook failed.");
        }
        setupDatabase();
        loadConfig();
        loadAdvs();
        this.getCommand("adv").setExecutor(new AdvCmd(this));
        pluginm.registerEvents(new AdvListener(this), this);
        enableAdventures();
    }

    @Override
    public void onDisable() {
        //test
        printUsers();

        saveAdvs();
        saveNPCS();
        /*try {
            System.out.println("[MYOA] Committing changes to progress.db");
        } catch (Exception e) {
            System.out.println("[MYOA] Error committing changes to progress.db");
            e.printStackTrace();
        }*/
    }

    public void printUsers() {
        for (Adventure a : adventures) {
            for (AdvPlayer ap : a.players) {
                System.out.println("\n User: " + ap.name + " - ID: " + ap.getUserid().toString() + "\n");
            }
        }
    }

    public void loadConfig() {
        paths = new ArrayList<>();
        try {
            File f = new File(getDataFolder(), "config.yml");
            YamlConfiguration warpf = new YamlConfiguration();
            warpf.load(f);
            for (String s : warpf.getKeys(false)) {
                String path = warpf.getString(s + ".path");
                paths.add(path);
            }
        } catch (Exception e) {
            System.out.println("[MYOA] Error loading config.yml");
        }
    }

    public void loadAdvs() {
        adventures = new ArrayList<>();
        for (String s : paths) {
            try {
                File f = new File(getDataFolder(), s);
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                String name = advf.getString("name");
                Boolean def = advf.getBoolean("default");
                Integer id = advf.getInt("id");
                String owner = advf.getString("owner");
                String prefix = advf.getString("prefix");
                Adventure adv = new Adventure(this, name, owner, id, s, prefix);
                ArrayList<String> players = (ArrayList<String>) advf.getStringList("players");
                System.out.println("Will load progress for players:");
                System.out.println(Arrays.toString(players.toArray()));
                for (String k : advf.getKeys(false)) {
                    if (k.contains("location")) {
                        String lname = advf.getString(k + ".name");
                        String wn = advf.getString(k + ".world");
                        Integer lid = advf.getInt(k + ".id");
                        World world = getServer().getWorld(wn);
                        Double x = advf.getDouble(k + ".x");
                        Double y = advf.getDouble(k + ".y");
                        Double z = advf.getDouble(k + ".z");
                        List<Float> direction = advf.getFloatList(k + ".direction");
                        float yaw;
                        float pitch;
                        if (direction.isEmpty()) {
                            yaw = 0;
                            pitch = 0;
                        } else {
                            yaw = direction.get(0);
                            pitch = direction.get(1);
                        }
                        Location loc = new Location(world, x, y, z, yaw, pitch);
                        List<String> meta = advf.getStringList(k + ".meta");
                        AdvLocation al = new AdvLocation(lname, lid, loc, (ArrayList<String>) meta);
                        adv.addLocation(al);
                    } else if (k.contains("zone")) {
                        String zname = advf.getString(k + ".name");
                        Integer zid = advf.getInt(k + ".id");
                        String wn = advf.getString(k + ".world");
                        World world = getServer().getWorld(wn);
                        String node = advf.getString(k + ".node");
                        String flag = advf.getString(k + ".flag");
                        FlagType ft = FlagType.NONE;
                        if (!flag.equals("") || !flag.equals("none")) {
                            ft = FlagType.valueOf(flag.toUpperCase());
                        }
                        Double x = advf.getDouble(k + ".x1");
                        Double y = advf.getDouble(k + ".y1");
                        Double z = advf.getDouble(k + ".z1");
                        Double x2 = advf.getDouble(k + ".x2");
                        Double y2 = advf.getDouble(k + ".y2");
                        Double z2 = advf.getDouble(k + ".z2");
                        Location loc = new Location(world, x, y, z);
                        Location loc2 = new Location(world, x2, y2, z2);
                        int off = advf.getInt(k + ".offset");
                        List<String> meta = advf.getStringList(k + ".meta");
                        AdvZone zone = new AdvZone(adv.getId(), zname, zid, node, loc, loc2, off, (ArrayList<String>) meta);
                        zone.areaFlag = ft;
                        adv.addZone(zone);
                    } else if (k.contains("teleport")) {
                        String wname = advf.getString(k + ".name");
                        Integer wid = advf.getInt(k + ".id");
                        Integer lid = advf.getInt(k + ".loc");
                        Integer zone = advf.getInt(k + ".zone");
                        String msg = advf.getString(k + ".message");
                        AdvTeleport tp = new AdvTeleport(adv.getId(), name, id, lid, zone);
                        tp.addMessage(msg);
                        adv.addTeleport(tp);
                    } else if (k.contains("quest")) {
                        Integer qid = advf.getInt(k + ".id");
                        String qname = advf.getString(k + ".name");
                        Integer qgiver = advf.getInt(k + ".giver");
                        Integer wxp = advf.getInt(k + ".xp");
                        ItemStack is = advf.getItemStack(k + ".items");
                        ItemStack rw = advf.getItemStack(k + ".rewards");
                        List<String> states = advf.getStringList(k + ".states");
                        ArrayList<AdvState> advStates = new ArrayList<>();
                        for (String state : states) {
                            Integer stateid = advf.getInt(k + "." + state + ".id");
                            String statename = advf.getString(k + "." + state + ".name");
                            Integer nextstate = advf.getInt(k + "." + state + ".next");
                            AdvStateType type = AdvStateType.valueOf(advf.getString(k + "." + state + ".type").toUpperCase());
                            ArrayList<String> flavor = (ArrayList) advf.getStringList(k + "." + state + ".meta");
                            AdvState as = new AdvState(adv, stateid, statename, nextstate, type, flavor);
                            ItemStack sits = advf.getItemStack(k + "." + state + ".items");
                            if (sits != null) {
                                as.setItems(sits);
                            }
                            Integer zoneid = advf.getInt(k + "." + state + ".zone");
                            if (zoneid != 0) {
                                as.setZone(zoneid);
                            }
                            Integer locid = advf.getInt(k + "." + state + ".loc");
                            if (locid != 0) {
                                as.setLoc(locid);
                            }
                            Integer npcid = advf.getInt(k + "." + state + ".npc");
                            if (npcid != 0) {
                                as.setNPC(npcid);
                            }
                            advStates.add(as);
                        }
                        AdvQuest aq = new AdvQuest(adv.getId(), qid, qname, qgiver, wxp, is, rw);
                        aq.setStates(advStates);
                        adv.addQuest(aq);
                    }
                }
                loadProgress(adv, players);
                adventures.add(adv);
                if(def) {
                    defaultAdv = adv;
                }
                System.out.println("[MYOA] Loaded " + adv.getName());
            } catch (Exception e) {
                System.out.println("[MYOA] Error loading adventure " + s);
                e.printStackTrace();
            }
        }
    }

    public void saveAdvs() {
        for (Adventure adv : adventures) {
            try {
                File f = new File(getDataFolder(), adv.getPath());
                YamlConfiguration advf = new YamlConfiguration();
                advf.set("name", adv.getName());
                advf.set("id", adv.getId());
                advf.set("owner", adv.getOwner());
                advf.set("prefix", adv.getPrefix());
                List<String> players = adv.getPlayers();
                advf.set("players", players);
                System.out.println("[MYOA] Saved Progress for players:");
                System.out.println(Arrays.toString(players.toArray()));
                for (AdvLocation loc : adv.getLocations()) {
                    advf.set("location-" + loc.getId() + ".id", loc.getId());
                    advf.set("location-" + loc.getId() + ".name", loc.getName());
                    advf.set("location-" + loc.getId() + ".world", loc.getLocation().getWorld().getName());
                    advf.set("location-" + loc.getId() + ".x", loc.getLocation().getX());
                    advf.set("location-" + loc.getId() + ".y", loc.getLocation().getY());
                    advf.set("location-" + loc.getId() + ".z", loc.getLocation().getZ());
                    ArrayList<Float> direction = new ArrayList<>();
                    direction.add(loc.getLocation().getYaw());
                    direction.add(loc.getLocation().getPitch());
                    advf.set("location-" + loc.getId() + ".direction", (List<Float>) direction);
                    advf.set("location-" + loc.getId() + ".meta", (List<String>) loc.getFlavor());
                }
                for (AdvZone z : adv.getZones()) {
                    advf.set("zone-" + z.getId() + ".id", z.getId());
                    advf.set("zone-" + z.getId() + ".name", z.getName());
                    advf.set("zone-" + z.getId() + ".node", z.getNode());
                    advf.set("zone-" + z.getId() + ".flag", z.areaFlag.toString().toLowerCase());
                    advf.set("zone-" + z.getId() + ".world", z.getLoc1().getWorld().getName());
                    advf.set("zone-" + z.getId() + ".x1", z.getLoc1().getBlockX());
                    advf.set("zone-" + z.getId() + ".x2", z.getLoc2().getBlockX());
                    advf.set("zone-" + z.getId() + ".y1", z.getLoc1().getBlockY());
                    advf.set("zone-" + z.getId() + ".y2", z.getLoc2().getBlockY());
                    advf.set("zone-" + z.getId() + ".z1", z.getLoc1().getBlockZ());
                    advf.set("zone-" + z.getId() + ".z2", z.getLoc2().getBlockZ());
                    advf.set("zone-" + z.getId() + ".offset", z.getOffsetY());
                    advf.set("zone-" + z.getId() + ".meta", (List<String>) z.getFlavor());
                }
                for (AdvTeleport tp : adv.getTeleports()) {
                    advf.set("teleport-" + tp.getId() + ".name", tp.getName());
                    advf.set("teleport-" + tp.getId() + ".id", tp.getId());
                    advf.set("teleport-" + tp.getId() + ".loc", tp.getLoc());
                    advf.set("teleport-" + tp.getId() + ".zone", tp.getZone());
                    advf.set("teleport-" + tp.getId() + ".message", tp.getMsg());
                }
                for (AdvQuest aq : adv.getQuests()) {
                    advf.set("quest-" + aq.getId() + ".name", aq.getName());
                    advf.set("quest-" + aq.getId() + ".id", aq.getId());
                    advf.set("quest-" + aq.getId() + ".giver", aq.getGiver());
                    advf.set("quest-" + aq.getId() + ".xp", aq.getXp());
                    advf.set("quest-" + aq.getId() + ".items", aq.getRequiredItems());
                    advf.set("quest-" + aq.getId() + ".rewards", aq.getRewardItems());
                    ArrayList<String> states = new ArrayList<>();
                    for (AdvState as : aq.getStates()) {
                        String sname = as.getName().replace(" ", "_");
                        states.add(sname);
                        advf.set("quest-" + aq.getId() + "." + sname + ".id", as.getId());
                        advf.set("quest-" + aq.getId() + "." + sname + ".name", as.getName());
                        advf.set("quest-" + aq.getId() + "." + sname + ".next", as.getNextstate());
                        advf.set("quest-" + aq.getId() + "." + sname + ".type", as.getType().toString());
                        advf.set("quest-" + aq.getId() + "." + sname + ".meta", as.getMeta());
                        Integer zid = as.getZone();
                        if (zid != 0) {
                            advf.set("quest-" + aq.getId() + "." + sname + ".zone", zid);
                        }
                        Integer lid = as.getLoc();
                        if (lid != 0) {
                            advf.set("quest-" + aq.getId() + "." + sname + ".loc", lid);
                        }
                        ItemStack rits = as.getItems();
                        if (rits != null) {
                            advf.set("quest-" + aq.getId() + "." + sname + ".items", as.getItems());
                        }
                        Integer nid = as.getNPC();
                        if (nid != 0) {
                            advf.set("quest-" + aq.getId() + "." + sname + ".npc", nid);
                        }
                    }
                    advf.set("quest-" + aq.getId() + ".states", (List<String>) states);
                }
                advf.save(f);
                System.out.println("[MYOA] Saved " + adv.getName());
            } catch (Exception e) {
                System.out.println("[MYOA] Error saving adventure " + adv.getPath());
                e.printStackTrace();
            }
            saveProgress(adv);
        }
    }

    private void enableAdventures() {
        for (Adventure a : adventures) {
            a.enabler();
        }
    }

    private void setupDatabase() {
        conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + this.getDataFolder().getAbsolutePath() + File.separator + "progress.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            System.out.println("[MYOA] Connection to SQLite has been established.");
            //Test tables
            String sql = "CREATE TABLE IF NOT EXISTS progress (\n"
                    + " id integer PRIMARY KEY, \n"
                    + "	uuid text,\n"
                    + "	aid integer,\n"
                    + "	qid integer,\n"
                    + "	sid integer"
                    + ");";
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            String sqll = "CREATE TABLE IF NOT EXISTS players (\n"
                    + " id integer PRIMARY KEY, \n"
                    + " uuid text,\n"
                    + " aid integer, \n"
                    + " xp integer, \n"
                    + " chartype text,\n"
                    + " level integer"
                    + ");";
            Statement sss = conn.createStatement();
            sss.execute(sqll);
            String fsql = "CREATE TABLE IF NOT EXISTS finished (\n"
                    + " id integer PRIMARY KEY, \n"
                    + " uuid text, \n"
                    + " aid integer, \n"
                    + " qid integer"
                    + "); ";
            Statement fs = conn.createStatement();
            fs.execute(fsql);
        } catch (SQLException e) {
            System.out.println("[MYOA]" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loadProgress(Adventure a, ArrayList<String> players) {
        if (!players.isEmpty()) {
            System.out.println("[MYOA] Loading progress for " + a.getName());
            Integer aid = a.getId();
            for (String uuid : players) {
                AdvPlayer ap = new AdvPlayer(UUID.fromString(uuid), 0);
                String xpq = "SELECT * FROM players WHERE uuid = ? AND aid = ?";
                try {
                    PreparedStatement ps = conn.prepareStatement(xpq);
                    ps.setString(1, uuid);
                    ps.setInt(2, aid);
                    ResultSet rs = ps.executeQuery();
                    Integer xp = rs.getInt("xp");
                    Integer level = rs.getInt("level");
                    AdvCharType ac = AdvCharType.valueOf(rs.getString("chartype"));
                    ap.chartype = ac;
                    ap.setLevel(level);
                    ap.setXp(xp);
                    System.out.println("[MYOA] Created AP for char " + ap.getUserid().toString());
                } catch (Exception e) {
                    System.out.println("[MYOA] " + e.getMessage());
                    e.printStackTrace();
                }
                String query = "SELECT * FROM progress WHERE uuid = ? AND aid = ?";
                try {
                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, uuid);
                    pstmt.setInt(2, aid);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        Integer qid = rs.getInt("qid");
                        Integer sid = rs.getInt("sid");
                        AdvQuest aq = a.getQuest(qid);
                        AdvState as = aq.getState(sid);
                        AdvPlayerProgress app = new AdvPlayerProgress(a, aq, as);
                        app.ap = ap;
                        ap.quests.put(aq, app);
                        if (!a.players.contains(ap)) {
                            a.players.add(ap);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("[MYOA] " + e.getMessage());
                    e.printStackTrace();
                }
                if (!a.players.contains(ap)) {
                    a.players.add(ap);
                }
            }
        }
    }

    public void saveProgress(Adventure a) {
        System.out.println("[MYOA] Saving progress for " + a.getName());
        Integer aid = a.getId();
        String query = "SELECT uuid FROM players";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            String pQuery = "SELECT uuid FROM progress";
            PreparedStatement pPs = conn.prepareStatement(pQuery);
            ResultSet ps = pPs.executeQuery();
            ArrayList<AdvPlayer> inserted = new ArrayList<>();
            while (rs.next()) {
                AdvPlayer ap = a.getPlayer(rs.getString("uuid"));
                if (ap != null) {
                    updateProgress(a, ap);
                    inserted.add(ap);
                    if (ap.quests.isEmpty()) {
                        a.players.remove(ap);
                    }
                }
            }
            while (ps.next()) {
                AdvPlayer ap = a.getPlayer(ps.getString("uuid"));
                if (ap != null) {
                    updateQuests(a, ap);
                }
            }
            for (AdvPlayer ap : a.players) {
                if (!inserted.contains(ap)) {
                    insertProgress(a, ap);
                }
                insertQuests(a, ap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*for (AdvPlayer ap : a.players) {
            try {
                if (a.getUpdates().contains(ap.getUserid().toString())) {
                    updateProgress(a, ap);
                    if (!ap.getQuests().isEmpty()) {
                        updateQuests(a, ap);
                        if (!ap.getQuests().isEmpty()) {
                            insertQuests(a, ap);
                        }
                    }
                } else {
                    insertProgress(a, ap);
                    if (!ap.getQuests().isEmpty()) {
                        insertQuests(a, ap);
                    }
                }
            } catch (Exception e) {
                System.out.println("[MYOA] " + e.getMessage());
                e.printStackTrace();
            }
        }*/
    }

    public Adventure getAdventure(int id) {
        for (Adventure a : adventures) {
            if (a.getId() == id) {
                return a;
            }
        }
        return null;
    }

    public Adventure getAdventure(Player p) {
        for (Adventure a : adventures) {
            AdvPlayer ap = a.getPlayer(p.getUniqueId());
            if (ap != null) {
                return a;
            }
        }
        return null;
    }

    public Adventure getAdventure(NPC npc) {
        for (Adventure a : adventures) {
            if (a.npcm.npcs.containsKey(npc.getId())) {
                return a;
            }
        }
        return null;
    }

    public void insertQuests(Adventure a, AdvPlayer ap) {
        try {
            for (AdvQuest aq : ap.getQuests()) {
                String iquery = "INSERT INTO progress(uuid,aid,qid,sid) VALUES(?,?,?,?)";
                PreparedStatement ps2 = conn.prepareStatement(iquery);
                ps2.setString(1, ap.getUserid().toString());
                ps2.setInt(2, a.getId());
                ps2.setInt(3, aq.getId());
                ps2.setInt(4, ap.quests.get(aq).current.getId());
                ps2.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("[MYOA] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertProgress(Adventure a, AdvPlayer ap) {
        try {
            String xquery = "INSERT INTO players(uuid, aid, xp, chartype, level) VALUES(?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(xquery);
            pstmt.setString(1, ap.getUserid().toString());
            pstmt.setInt(2, a.getId());
            pstmt.setLong(3, ap.xp);
            pstmt.setString(4, ap.chartype.toString().toUpperCase());
            pstmt.setLong(5, ap.getLevel());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("[MYOA] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateQuests(Adventure a, AdvPlayer ap) {
        try {
            String query = "SELECT * FROM progress WHERE uuid = ? AND aid = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, ap.getUserid().toString());
            pstmt.setInt(2, a.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int qid = rs.getInt("qid");
                int oid = rs.getInt("sid");
                AdvQuest aq = a.getQuest(qid);
                AdvPlayerProgress app = ap.quests.get(aq);
                if (app != null) {
                    int sid = app.current.getId();
                    String iquery = "UPDATE progress SET sid = ? WHERE uuid = ? AND aid = ? AND qid = ?";
                    PreparedStatement ust = conn.prepareStatement(iquery);
                    ust.setInt(1, sid);
                    ust.setString(2, ap.getUserid().toString());
                    ust.setInt(3, a.getId());
                    ust.setInt(4, qid);
                    ust.executeUpdate();
                    ap.quests.remove(aq);
                } else {
                    String dquery = "DELETE FROM progress WHERE uuid = ? AND aid = ? AND qid = ?";
                    PreparedStatement dst = conn.prepareStatement(dquery);
                    dst.setString(1, ap.getUserid().toString());
                    dst.setInt(2, a.getId());
                    dst.setInt(3, aq.getId());
                    dst.execute();
                }
            }
            a.players.remove(ap);
        } catch (Exception e) {
            System.out.println("[MYOA] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateProgress(Adventure a, AdvPlayer ap) {
        try {
            String xquery = "UPDATE players SET xp = ?, chartype = ? WHERE uuid = ? AND aid = ?";
            PreparedStatement ps = conn.prepareStatement(xquery);
            ps.setLong(1, ap.xp);
            ps.setString(2, ap.chartype.toString().toUpperCase());
            ps.setString(3, ap.getUserid().toString());
            ps.setInt(4, a.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("[MYOA] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertFinished(Adventure a, AdvQuest aq, AdvPlayer ap) {
        try {
            String fquery = "INSERT INTO finished(uuid, aid, qid) VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(fquery);
            ps.setString(1, ap.getUserid().toString());
            ps.setInt(2, a.getId());
            ps.setInt(3, aq.getId());
            ps.execute();
        } catch (Exception e) {
            System.out.println("[MYOA] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeProgress(Adventure a, AdvQuest aq, AdvPlayer ap) {
        try {
            String rQuery = "DELETE FROM progress WHERE uuid = ? AND aid = ? AND qid = ?";
            PreparedStatement ps = conn.prepareStatement(rQuery);
            ps.setString(1, ap.getUserid().toString());
            ps.setInt(2, a.getId());
            ps.setInt(3, aq.getId());
            ps.execute();
        } catch (Exception e) {
            System.out.println("[MYOA] " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMessage(Adventure a, AdvPlayer p, String msg) {

        String pmsg = ChatColor.GRAY + "[" + ChatColor.GREEN + a.getPrefix() + ChatColor.GRAY + "] ";
        p.sendMessage(pmsg + ChatColor.GRAY + msg);
    }

    public void sendOptions(Adventure a, AdvPlayer p, ArrayList<Line> msg) {
        if (msg.size() > 1) {
            for (Line l : msg) {
                Integer option = msg.indexOf(l);
                option++;
                String pmsg = ChatColor.GRAY + "[" + ChatColor.GREEN + a.getPrefix() + ChatColor.GRAY + "] " + option.toString() + " - ";
                pmsg += l.getText();
                if (l.rt != ResponseType.NONE) {
                    if (l.rt == ResponseType.ACCEPT) {
                        pmsg += "(" + ChatColor.GREEN + "Accept" + ChatColor.GRAY + ")";
                    } else if (l.rt == ResponseType.DECLINE) {
                        pmsg += "(" + ChatColor.RED + "Decline" + ChatColor.GRAY + ")";
                    }
                }
                p.sendMessage(pmsg);
            }
        } else {
            Line l = msg.get(0);
            String pmsg = ChatColor.GRAY + "[" + ChatColor.GREEN + a.getPrefix() + ChatColor.GRAY + "] 1 - ";
            pmsg += l.getText();
            if (l.rt != ResponseType.NONE) {
                if (l.rt == ResponseType.ACCEPT) {
                    pmsg += "(" + ChatColor.GREEN + "Accept" + ChatColor.GRAY + ")";
                } else if (l.rt == ResponseType.DECLINE) {
                    pmsg += "(" + ChatColor.RED + "Accept" + ChatColor.GRAY + ")";
                }
            }
            p.sendMessage(pmsg);
        }
    }

    public String getName(Adventure a, int id, AdvPlayer p) {
        AdvQuest quest = a.getQuest(id);
        if (quest != null) {
            return quest.getName();
        } else {
            AdvZone az = a.getZone(id);
            if (az != null) {
                String name = az.getName();
                String d = getDistance(az, p.getLocation());
                return name + ChatColor.GRAY + "(" + ChatColor.GREEN + d + "m" + ChatColor.GRAY + ")";
            } else {
                AdvLocation al = a.getLoc(id);
                if (al != null) {
                    String name = al.getName();
                    String d = getDistance(al, p.getLocation());
                    return name + ChatColor.GRAY + "(" + ChatColor.GREEN + d + "m" + ChatColor.GRAY + ")";
                } else {
                    AdvState as = a.getState(id);
                    if (as != null) {
                        return as.getName();
                    }
                }
            }
        }
        return null;
    }

    public String getDistance(AdvZone az, Location loc2) {
        double d1 = az.getLoc1().distance(loc2);
        double d2 = az.getLoc2().distance(loc2);
        double d = (d1 + d2) / 2;
        return String.format("%.0f", d);
    }

    public String getDistance(AdvLocation al, Location loc2) {
        double d = al.getLocation().distance(loc2);
        return String.format("%.0f", d);
    }

    public void debug(String msg) {
        System.out.println("\n[MYOA Debug] " + msg);
    }

    public void saveNPCS() {
        for (Adventure a : adventures) {
            a.npcm.saveNPCS();
            a.pm.saveProperties();
            a.npcm.fm.saveFactionData();
            a.npcm.purgeNPCs();
        }
    }

    public void fakeChat(Adventure a, AdvPlayer player, CapNPC npc, String message) {
        ArrayList<ChatChannel> chans = chat.channelHandler.getChannels();
        for (ChatChannel chan : chans) {
            if (chan.containsPlayer(player.player)) {
                String chatmsg = formatChat(a, npc, chan, message);
                chatmsg = chatmsg.replace("%player", player.displayName);
                chan.sendMessage(chatmsg);
                return;
            }
        }
    }
    
    public void fakeChat(AdvPlayer player, String message) {
        ArrayList<ChatChannel> chans = chat.channelHandler.getChannels();
        for (ChatChannel chan : chans) {
            if (chan.containsPlayer(player.player)) {
                String chatmsg = formatChat(player, chan, message);
                chatmsg = chatmsg.replace("%player", player.displayName);
                chan.sendMessage(chatmsg);
                return;
            }
        }
    }

    public String formatChat(AdvPlayer player, ChatChannel channel, String message) {
        String format = channel.getFormat().replace("+senderName", player.displayName);
        format = format.replace("+worldName", player.getLocation().getWorld().getName());
        format = format.replace("+channelName", channel.getName());
        String prefix = chat.pm.getDefaultGroups(player.getLocation().getWorld().getName()).get(0).getPrefix();
        format = format.replace("+prefix", prefix);
        format = format.replace("+message", message);
        format = format.replaceAll("(&([a-f0-9]))", "§$2");
        return format;
    }
    
    public String formatChat(Adventure a, CapNPC npc, ChatChannel channel, String message) {
        String format = channel.getFormat().replace("+senderName", a.npcm.fm.getFactionName(npc));
        format = format.replace("+worldName", npc.anchor.getWorld().getName());
        format = format.replace("+channelName", channel.getName());
        String prefix = chat.pm.getDefaultGroups(npc.anchor.getWorld().getName()).get(0).getPrefix();
        format = format.replace("+prefix", prefix);
        if(a.pm.properties.containsKey(npc)) {
            message = message.replace("+property", a.pm.properties.get(npc).getName());
        }
        format = format.replace("+message", message);
        format = format.replaceAll("(&([a-f0-9]))", "§$2");
        return format;
    }

    public void delayedChat(Adventure a, ArrayList<String> messages, AdvPlayer player, CapNPC npc) {
        for (int i = 0; i < messages.size(); i++) {
            String msg = messages.get(i);
            Runnable r = () -> {
                fakeChat(a, player, npc, msg);
            };
            long delay = chatdelay;
            if (i > 0) {
                delay += (i * 10);
            }
            getServer().getScheduler().scheduleSyncDelayedTask(this, r, delay);
        }
    }

    public void delayedChat(Adventure a, String msg, AdvPlayer player, CapNPC npc) {
        Runnable r = () -> {
            fakeChat(a, player, npc, msg);
        };
        getServer().getScheduler().scheduleSyncDelayedTask(this, r, chatdelay);
    }

    public CapNPC getNPC(NPC npc) {
        for (Adventure a : adventures) {
            CapNPC c = a.npcm.getNPC(npc.getId());
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public AdvDuo isInside(Location loc, AdvPlayer p) {
        Adventure a = getAdventure(p.player);
        if (a == null) {
            return null;
        }
        for (AdvZone z : a.getZones()) {
            if (inZone(z, loc)) {
                AdvDuo ad = new AdvDuo(a, z);
                ad.p = a.getPlayer(p.getUserid());
                return ad;
            }
        }
        return null;
    }

    public boolean inZone(AdvZone zone, Location loc) {
        return inArea(loc, zone.getLoc1(), zone.getLoc2(), true, zone);
    }

    public boolean inArea(Location targetLocation, Location inAreaLocation1, Location inAreaLocation2, boolean checkY, AdvZone z) {
        if (inAreaLocation1.getWorld().getName().equals(inAreaLocation2.getWorld().getName())) { // Check for worldName location1, location2
            if (targetLocation.getWorld().getName().equals(inAreaLocation1.getWorld().getName())) { // Check for worldName targetLocation, location1
                if ((targetLocation.getBlockX() >= inAreaLocation1.getBlockX() && targetLocation.getBlockX() <= inAreaLocation2.getBlockX()) || (targetLocation.getBlockX() <= inAreaLocation1.getBlockX() && targetLocation.getBlockX() >= inAreaLocation2.getBlockX())) { // Check X value
                    if ((targetLocation.getBlockZ() >= inAreaLocation1.getBlockZ() && targetLocation.getBlockZ() <= inAreaLocation2.getBlockZ()) || (targetLocation.getBlockZ() <= inAreaLocation1.getBlockZ() && targetLocation.getBlockZ() >= inAreaLocation2.getBlockZ())) { // Check Z value
                        if (checkY == true) { // If should check for Y value
                            if ((targetLocation.getBlockY() >= inAreaLocation1.getBlockY() - z.getOffsetY() && targetLocation.getBlockY() <= inAreaLocation2.getBlockY() + z.getOffsetY()) || (targetLocation.getBlockY() <= inAreaLocation1.getBlockY() + z.getOffsetY() && targetLocation.getBlockY() >= inAreaLocation2.getBlockY() - z.getOffsetY())) { // Check Y value
                                return true;
                            }
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
