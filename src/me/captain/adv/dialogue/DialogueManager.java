//Do want you want with this trash code :)
package me.captain.adv.dialogue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import me.captain.adv.AdvDuo;
import me.captain.adv.AdvZone;
import me.captain.adv.Adventure;
import me.captain.adv.faction.Faction;
import me.captain.adv.faction.FactionType;
import me.captain.adv.npc.CapNPC;
import org.bukkit.entity.Player;
import me.captain.adv.npc.NPCCareerType;
import me.captain.adv.quest.AdvQuest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.concurrent.ThreadLocalRandom;
import me.captain.adv.AdvPlayer;
import me.captain.adv.npc.Career;
import me.captain.adv.quest.AdvPlayerProgress;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 *
 * @author andre
 */
public class DialogueManager {

    public Adventure a;

    public HashMap<Integer, ArrayList<Line>> playerLines;
    public HashMap<FlagType, ArrayList<Line>> playerres;

    public ArrayList<DialoguePack> currentConvos;

    public HashMap<AdvPlayer, ShopDialogue> shopping;

    public DialogueManager(Adventure adv) {
        a = adv;
        playerLines = new HashMap<>();
        currentConvos = new ArrayList<>();
        shopping = new HashMap<>();
        playerres = new HashMap<>();
    }

    public void loadDialogue() {
        System.out.println("[MYOA] Loading dialogue");
        for (Faction f : a.npcm.fm.factions.values()) {
            loadFactionDialogue(f);
        }
        for (NPCCareerType career : NPCCareerType.values()) {
            loadCareerDialogue(career);
        }
        for (CapNPC npc : a.npcm.npcs.values()) {
            loadNPCDialogue(npc);
        }
        loadPlayerDialogue();
        System.out.println("[MYOA] Loaded dialogue");
    }

    public void loadFactionDialogue(Faction fac) {
        try {
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-dialogue" + File.separator + fac.type.toString().toLowerCase() + "-dialogue.yml");
            //System.out.println("[MYOA] Searching path: " + f.getAbsolutePath());
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                for (String k : advf.getKeys(false)) {
                    FlagType ft = FlagType.valueOf(k.toUpperCase());
                    ConfigurationSection cs = advf.getConfigurationSection(k);
                    ArrayList<Line> lines = new ArrayList<>();
                    for (String kd : cs.getKeys(true)) {

                        if (!kd.contains(".")) {
                            Integer id = cs.getInt(kd + ".id");
                            String text = cs.getString(kd + ".line");
                            Line line = new Line(id, ft, text);
                            //System.out.println("[MYOA] Adding line \"" + text + "\" for " + fac.getName());
                            lines.add(line);
                        }

                    }
                    fac.lines.put(ft, lines);
                }

            } else {
                System.out.println("[MYOA] No dialogue found for " + fac.getName());
            }
        } catch (Exception e) {
            System.out.println("[MYOA] Error loading dialogue for " + fac.getName());
            e.printStackTrace();
        }
    }

    public void loadCareerDialogue(NPCCareerType career) {
        try {
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-dialogue" + File.separator + career.toString().toLowerCase() + "-dialogue.yml");
            //System.out.println("[MYOA] Searching path: " + f.getAbsolutePath());
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                Career c = a.npcm.careers.get(career);
                System.out.println("[MYOA] Searching for dialogue for " + career.toString());
                for (String k : advf.getKeys(false)) {
                    FlagType ft = FlagType.valueOf(k.toUpperCase());
                    ConfigurationSection cs = advf.getConfigurationSection(k);
                    ArrayList<Line> lines = new ArrayList<>();
                    for (String kd : cs.getKeys(true)) {
                        if (!kd.contains(".")) {
                            Integer id = cs.getInt(kd + ".id");
                            String text = cs.getString(kd + ".line");
                            Line line = new Line(id, ft, text);
                            //System.out.println("[MYOA] Adding line \"" + text + "\" for " + career.name());
                            lines.add(line);
                        }
                    }
                    c.lines.put(ft, lines);
                }
            } else {
                System.out.println("[MYOA] No dialogue found for " + career.toString().toLowerCase());
            }
        } catch (Exception e) {
            System.out.println("[MYOA] Error loading dialogue for " + career.toString().toLowerCase());
            e.printStackTrace();
        }
    }

    public void loadNPCDialogue(CapNPC npc) {
        try {
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-dialogue" + File.separator + "npc-" + npc.getId() + "-dialogue.yml");
            //System.out.println("[MYOA] Searching path: " + f.getAbsolutePath());
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                for (String k : advf.getKeys(false)) {
                    FlagType ft = FlagType.valueOf(k.toUpperCase());
                    ConfigurationSection cs = advf.getConfigurationSection(k);
                    ArrayList<Line> lines = new ArrayList<>();
                    for (String kd : cs.getKeys(true)) {
                        if (!kd.contains(".")) {
                            //System.out.println("[MYOA] " + kd);
                            Integer id = cs.getInt(kd + ".id");
                            String text = cs.getString(kd + ".line");
                            Line line = new Line(id, ft, text);
                            if (ft == FlagType.QUEST) {
                                Integer qid = cs.getInt(kd + ".quest");
                                line.treespot = cs.getInt(kd + ".tree");
                                line.quest_id = qid;
                                String rt = cs.getString(kd + ".type");
                                if (rt != null) {
                                    ResponseType r = ResponseType.valueOf(rt.toUpperCase());
                                    line.rt = r;
                                }
                            }
                            lines.add(line);
                        }
                    }
                    npc.lines.put(ft, lines);
                    //System.out.println("[MYOA] NPC dialogue loaded:");
                    for (Line l : lines) {
                        //System.out.println("[" + l.getId() + "] - " + l.getText() + " - " + l.quest_id);
                    }
                }
            } else {
                System.out.println("[MYOA] No dialogue found for " + npc.getName());
            }
        } catch (Exception e) {
            System.out.println("[MYOA] Error loading dialogue for " + npc.getName());
            e.printStackTrace();
        }
    }

    public void loadPlayerDialogue() {
        try {
            String path = a.getPath();
            File f = new File(a.plugin.getDataFolder(), path.substring(0, path.length() - 4) + "-dialogue" + File.separator + "player-dialogue.yml");
            //System.out.println("[MYOA] Searching path: " + f.getAbsolutePath());
            if (f.exists()) {
                YamlConfiguration advf = new YamlConfiguration();
                advf.load(f);
                for (String k : advf.getKeys(false)) {
                    FlagType ft = FlagType.valueOf(k.toUpperCase());
                    ConfigurationSection cs = advf.getConfigurationSection(k);
                    ArrayList<Line> lines = new ArrayList<>();
                    for (String kd : cs.getKeys(true)) {
                        if (!kd.contains(".")) {
                            //System.out.println("[MYOA] " + kd);
                            Integer id = cs.getInt(kd + ".id");
                            String text = cs.getString(kd + ".line");
                            Line line = new Line(id, ft, text);
                            String rt = cs.getString(kd + ".type");
                            if (rt != null) {
                                ResponseType r = ResponseType.valueOf(rt.toUpperCase());
                                line.rt = r;
                            }
                            if (ft == FlagType.QUEST) {
                                Integer qid = cs.getInt(kd + ".quest");
                                line.treespot = cs.getInt(kd + ".tree");
                                if (playerLines.containsKey(qid)) {
                                    ArrayList<Line> ql = playerLines.get(qid);
                                    ql.add(line);
                                } else {
                                    ArrayList<Line> ql = new ArrayList<>();
                                    ql.add(line);
                                    playerLines.put(qid, ql);
                                }
                            } else {
                                if(playerres.containsKey(ft)) {
                                    ArrayList<Line> fl = playerres.get(ft);
                                    fl.add(line);
                                } else {
                                    ArrayList<Line> fl = new ArrayList<>();
                                    fl.add(line);
                                    playerres.put(ft, fl);
                                }
                            }
                        }
                    }
                    System.out.println("[MYOA] Player dialogue loaded:");
                    for (ArrayList<Line> ll : playerLines.values()) {
                        for (Line l : ll) {
                            //System.out.println("[" + l.getId() + "] - " + l.getText() + " - " + l.quest_id);
                        }
                    }
                }
            }
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("[MYOA] Error loading dialogue for player");
            e.printStackTrace();
        }
    }

    public void serveShopResponse(CapNPC npc, AdvPlayer player) {
        ArrayList<Line> alines = new ArrayList<>();
        for (Line l : playerres.get(FlagType.SHOP_ACCEPT)) {
            alines.add(l);
        }
        ArrayList<Line> dlines = new ArrayList<>();
        for (Line l : playerres.get(FlagType.SHOP_DENY)) {
            dlines.add(l);
        }
        Collections.shuffle(alines);
        Collections.shuffle(dlines);
        int maxa = alines.size();
        int maxd = dlines.size();
        int aline = 0;
        int dline = 0;
        if (maxa > 0) {
            aline = ThreadLocalRandom.current().nextInt(0, maxa);
            System.out.println("[MYOA] Shuffled to accept line " + aline);
        }
        if (maxd > 0) {
            dline = ThreadLocalRandom.current().nextInt(0, maxd);
            System.out.println("[MYOA] Shuffled to deny line " + dline);
        }
        Line al = alines.get(aline);
        Line dl = dlines.get(dline);
        ShopDialogue sd = new ShopDialogue(npc, al, dl);
        shopping.put(player, sd);
        AdvPlayer ap = a.getPlayer(player.getUserid());
        ArrayList<Line> lines = new ArrayList<>();
        lines.add(al);
        lines.add(dl);
        a.plugin.sendOptions(a, player, lines);
    }

    public void enterShopInventory(CapNPC npc, AdvPlayer player) {
        System.out.println("[MYOA] entering shop inventory");
        a.pm.serveInventory(player, npc);
    }

    public void sendShopLine(CapNPC npc, AdvPlayer player, FlagType ft) {
        ArrayList<Line> outrolines = new ArrayList<>();
        Career r = a.npcm.careers.get(npc.career);
        if (r.lines.isEmpty()) {
            System.out.println("[MYOA] Lines for " + r.getType().toString() + " not found");
        } else {
            for (Line l : r.lines.get(ft)) {
                outrolines.add(l);
            }
        }
        Collections.shuffle(outrolines); 
        int upper = outrolines.size();
        if (upper > 0) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, upper);
            System.out.println("[MYOA] Shuffled to line " + npc.getName() + " " + ft.toString() + " line " + randomNum + ", serving..");
            serveLine(outrolines.get(randomNum), npc, player);
        } else {
            System.out.println("[MYOA] No dialogue to serve..");
        }
        System.out.println("[MYOA] Found " + outrolines.size() + " outro lines for " + npc.getName());
        if(ft == FlagType.SHOP_START) {
            Runnable run = () -> {
            serveShopResponse(npc, player);
        };
        a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, run, 30);
        }
    }

    public void sendShopLine(AdvPlayer player, Line line) {
        a.plugin.fakeChat(player, line.getText());
        if (line.rt == ResponseType.ACCEPT) {
            enterShopInventory(shopping.get(player).npc, player);
        } else if (line.rt == ResponseType.DENY) {
            sendShopLine(shopping.get(player).npc, player, FlagType.SHOP_END);
            this.shopping.remove(player);
        }
    }

    public void serveUpRandomConvo(CapNPC npc, AdvPlayer player) {
        if (npc.career == NPCCareerType.INNKEEP || npc.career == NPCCareerType.BLACKSMITH) {
            sendShopLine(npc, player, FlagType.SHOP_START);
            return;
        }
        ArrayList<Line> randomlines = new ArrayList<>();
        if (npc.lines.containsKey(FlagType.NONE)) {
            for (Line l : npc.lines.get(FlagType.NONE)) {
                randomlines.add(l);
            }
        }
        ArrayList<FlagType> keys = determineKeys(npc, player);
        HashMap<FlagType, ArrayList<Line>> clines = a.npcm.careers.get(npc.career).lines;
        if (clines.containsKey(FlagType.NONE)) {
            if (!clines.get(FlagType.NONE).isEmpty()) {
                for (Line l : clines.get(FlagType.NONE)) {
                    randomlines.add(l);
                }
            }
        }
        for (FactionType fact : npc.factions) {
            Faction f = a.npcm.fm.factions.get(fact);
            if (f.lines.containsKey(FlagType.NONE)) {
                for (Line l : f.lines.get(FlagType.NONE)) {
                    randomlines.add(l);
                }
            }
        }
        for (FlagType ft : keys) {
            System.out.println("[MYOA] Traversing dialogue flag type: " + ft.toString().toLowerCase());
            if (ft == FlagType.NONE) {

            } else {
                if (npc.lines.containsKey(ft)) {
                    if (!npc.lines.get(ft).isEmpty()) {
                        for (Line l : npc.lines.get(ft)) {
                            randomlines.add(l);
                            randomlines.add(l);
                        }
                    }
                }
                if (!clines.isEmpty()) {
                    if (!clines.get(ft).isEmpty()) {
                        for (Line l : clines.get(ft)) {
                            randomlines.add(l);
                            randomlines.add(l);
                        }
                    }
                }
                for (FactionType fact : npc.factions) {
                    Faction f = a.npcm.fm.factions.get(fact);
                    if (!f.lines.isEmpty()) {
                        for (Line l : f.lines.get(ft)) {
                            randomlines.add(l);
                            randomlines.add(l);
                        }
                    }
                }
            }
        }
        Collections.shuffle(randomlines);
        int upper = randomlines.size();
        if (upper > 0) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, upper);
            System.out.println("[MYOA] Shuffled to line " + randomNum + ", serving..");
            serveLine(randomlines.get(randomNum), npc, player);
        } else {
            System.out.println("[MYOA] No dialogue to serve..");
        }
    }

    public void serveQuestConvo(AdvQuest aq, CapNPC npc, AdvPlayer player) {

    }

    public void serveInterruptConvo(CapNPC npc, Player player) {

    }

    public void serveDP(DialoguePack dp, CapNPC npc, Player player) {

    }

    public ArrayList<FlagType> determineKeys(CapNPC npc, AdvPlayer player) {
        ArrayList<FlagType> keys = new ArrayList<>();
        keys.add(FlagType.NONE);
        AdvDuo ad = a.plugin.isInside(player.player.getLocation(), player);
        if (ad != null) {
            AdvZone az = ad.z;
            if (az.areaFlag != FlagType.NONE) {
                keys.add(az.areaFlag);
            }
        }
        long time = player.getLocation().getWorld().getTime();
        if (time > 12567 && time < 22920) {
            keys.add(FlagType.NIGHT);
            if (time > 16000 && time < 21500) {
                keys.add(FlagType.LATE_NIGHT);
            }
        }
        return keys;
    }

    public void serveLine(Line l, CapNPC npc, AdvPlayer player) {
        a.plugin.delayedChat(a, l.getText(), player, npc);
    }

    public void setupConvo(CapNPC npc, AdvPlayer player, AdvPlayerProgress app) {
        int qid = app.quest.getId();
        int npcid = npc.getId();
        UUID uuid = player.getUserid();
        AdvPlayer ap = a.getPlayer(player.getUserid());

        ArrayList<Line> lines = new ArrayList<>();
        //Get lines related to quest
        for (Line ql : npc.lines.get(FlagType.QUEST)) {
            if (ql.quest_id == qid) {
                lines.add(ql);
            }
        }
        System.out.println("[MYOA] Found " + lines.size() + " lines for " + npc.getName());

        //Build the NPCTree
        HashMap<Integer, ArrayList<Line>> npcOptions = new HashMap<>();
        for (Line l : lines) {
            if (npcOptions.containsKey(l.treespot)) {
                ArrayList<Line> op = npcOptions.get(l.treespot);
                op.add(l);
            } else {
                ArrayList<Line> op = new ArrayList<>();
                op.add(l);
                npcOptions.put(l.treespot, op);
            }
        }
        HashMap<Integer, ArrayList<Line>> playerOptions = new HashMap<>();
        //Get player lines related to quest and build dialogue tree
        if (playerLines.containsKey(qid)) {
            ArrayList<Line> qlines = playerLines.get(qid);
            for (Line l : qlines) {
                if (playerOptions.containsKey(l.treespot)) {
                    ArrayList<Line> op = playerOptions.get(l.treespot);
                    op.add(l);
                } else {
                    ArrayList<Line> op = new ArrayList<>();
                    op.add(l);
                    playerOptions.put(l.treespot, op);
                }
            }
        } else {
            System.out.println("[MYOA] No player lines found for quest " + app.quest.getName());
        }

        //Build the dialogue pack
        DialoguePack dp = new DialoguePack(a, uuid, npcid);
        dp.npclines.allOptions = npcOptions;
        dp.playerlines.allOptions = playerOptions;

        dp.npclines.initiate();
        dp.playerlines.initiate();

        System.out.println("[MYOA] Found " + lines.size() + " lines for " + npc.getName() + " in quest " + app.quest.getName());

        //Start dialogue tree run
        dp.player = player;
        dp.npc = npc;
        dp.app = app;
        dp.player_turn = false;
        dp.start();
        currentConvos.add(dp);
    }

    public DialoguePack getDP(UUID player) {
        for (DialoguePack dp : currentConvos) {
            if (dp.player.getUserid().compareTo(player) == 0) {
                return dp;
            }
        }
        return null;
    }
}
