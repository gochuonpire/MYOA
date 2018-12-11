//Do want you want with this trash code :)
package me.captain.adv.quest;

import me.captain.adv.npc.CapNPC;
import java.util.ArrayList;
import me.captain.adv.AdvLocation;
import me.captain.adv.AdvPlayer;
import me.captain.adv.Adventure;
import static me.captain.adv.quest.AdvStateType.*;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class AdvPlayerProgress {

    public AdvPlayer ap;
    public Player player;
    public Adventure a;
    public AdvQuest quest;
    public AdvState current;

    public AdvPlayerProgress(Adventure adv, AdvQuest quest, AdvState currentstate) {
        a = adv;
        this.quest = quest;
        current = currentstate;
    }

    public void moveAlong() {
        AdvState next = quest.getState(current.getNextstate());
        this.current = next;
        pre();
    }

    public void pre() {
        ItemStack si = quest.getRequiredItems();
        switch (current.type) {
            case FAIL:
                a.finish(quest, ap);
                ap.quests.remove(quest);
                break;
            case MESSAGE:
                sendFlavor();
                execute();
                break;
            case GATHER:
                sendFlavor();
                break;
            case KILL:
                sendFlavor();
                break;
            case START:
                Runnable rs = () -> {
                    execute();
                };
                a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, rs, 40);
                break;
            case END:
                Runnable re = () -> {
                    execute();
                };
                a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, re, 40);
                break;
            case ARRIVE:
                sendFlavor();
                break;
            case PLACE:
                sendFlavor();
                break;
            case BREAK:
                sendFlavor();
                break;
            case TELEPORT:
                sendFlavor();
                execute();
                break;
            case CONVERSE:
                System.out.println("[MYOA] Adding player to stop list");
                if (a.npcm.getNPC(current.getNPC()).shouldStop.containsKey(player.getUniqueId())) {
                    ArrayList<AdvQuest> quests = a.npcm.getNPC(current.getNPC()).shouldStop.get(player.getUniqueId());
                    quests.add(quest);
                } else {
                    ArrayList<AdvQuest> quests = new ArrayList<>();
                    quests.add(quest);
                    a.npcm.getNPC(current.getNPC()).shouldStop.put(player.getUniqueId(), quests);
                }
                break;
        }
    }

    public void execute() {
        ItemStack si = quest.getRequiredItems();
        switch (current.type) {
            case START:
                if (si != null) {
                    PlayerInventory pi = player.getInventory();
                    if (pi.contains(si, si.getAmount())) {
                        pi.remove(si);
                        a.plugin.sendMessage(a, ap, "Removing " + si.getAmount() + " of " + si.getType().name());
                    } else {
                        a.plugin.sendMessage(a, ap, "You need " + si.getAmount() + " of " + si.getType().name() + " to do this quest!");
                        //end
                    }
                }
                if (!current.flavor.isEmpty()) {
                    a.plugin.sendMessage(a, ap, current.flavor.get(0));
                }
                moveAlong();
                break;
            case END:
                if (!current.flavor.isEmpty()) {
                    a.plugin.sendMessage(a, ap, current.flavor.get(0));
                }
                Boolean levelup = ap.addCheck(quest.getXp());
                a.plugin.sendMessage(a, ap, "Earned " + ChatColor.GREEN + quest.getXp() + ChatColor.GRAY + " xp!");
                if (levelup) {
                    a.plugin.sendMessage(a, ap, "You leveled up! You are now level " + ChatColor.GREEN + ap.getLevel() + ChatColor.GRAY + "!");
                } else {
                    a.plugin.sendMessage(a, ap, "You need " + ChatColor.GREEN + ap.nextlevel() + ChatColor.GRAY + " xp to level up");
                }

                ItemStack rw = quest.getRewardItems();
                if (rw != null) {
                    player.getInventory().addItem(rw);
                    a.plugin.sendMessage(a, ap, "Reward: " + ChatColor.GREEN + rw.getType().name() + ChatColor.GRAY + "- Amount: " + ChatColor.GREEN + rw.getAmount());
                }
                a.finish(quest, ap);
                for (CapNPC cn : a.npcm.npcs.values()) {
                    if (cn.shouldStop.containsKey(player.getUniqueId())) {
                        cn.shouldStop.remove(player.getUniqueId());
                    }
                }
                ap.quests.remove(quest);
                break;
            case KILL:
                String kmsg = "";
                if (!current.flavor.isEmpty()) {
                    kmsg = current.flavor.get(0);
                }
                if (kmsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You have killed " + ChatColor.GREEN + a.npcm.getNPC(current.getNPC()).getName());
                } else {
                    a.plugin.sendMessage(a, ap, kmsg);
                }
                moveAlong();
                break;
            case SPEAK:
                CapNPC npc = current.adventure.npcm.getNPC(current.getNPC());
                if (!current.flavor.isEmpty()) {
                    a.plugin.delayedChat(a, current.flavor, ap, npc);
                } else {
                    a.plugin.delayedChat(a, "" + npc.getName() + ChatColor.GRAY + " doesn't have anything else to say.", ap, npc);
                }
                Runnable r = () -> {
                    moveAlong();
                };
                a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, r, 40);
                break;
            case ARRIVE:
                String armsg = "";
                if (!current.flavor.isEmpty()) {
                    armsg = current.flavor.get(0);
                }
                if (armsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You have arrived at " + ChatColor.GREEN + a.getZone(current.getZone()).getName());
                } else {
                    a.plugin.sendMessage(a, ap, armsg);
                }
                moveAlong();
                break;
            case LEAVE:
                String lmsg = "";
                if (!current.flavor.isEmpty()) {
                    lmsg = current.flavor.get(0);
                }
                if (lmsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You have left " + ChatColor.GREEN + a.getZone(current.getZone()).getName());
                } else {
                    a.plugin.sendMessage(a, ap, lmsg);
                }
                moveAlong();
            case PLACE:
                String pmsg = "";
                if (!current.flavor.isEmpty()) {
                    pmsg = current.flavor.get(0);
                }
                if (pmsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You have placed a " + ChatColor.GREEN + current.items.getType().name());
                } else {
                    a.plugin.sendMessage(a, ap, pmsg);
                }
                moveAlong();
                break;
            case BREAK:
                String bmsg = "";
                if (!current.flavor.isEmpty()) {
                    bmsg = current.flavor.get(0);
                }
                if (bmsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You broke a " + ChatColor.GREEN + current.items.getType().name());
                } else {
                    a.plugin.sendMessage(a, ap, bmsg);
                }
                moveAlong();
                break;
            case SIGN:
                String smsg = "";
                if (!current.flavor.isEmpty()) {
                    smsg = current.flavor.get(0);
                }
                if (smsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You read the sign and learned nothing..");
                } else {
                    a.plugin.sendMessage(a, ap, smsg);
                }
                moveAlong();
                break;
            case GATHER:
                String gmsg = "";
                if (!current.flavor.isEmpty()) {
                    gmsg = current.flavor.get(0);
                }
                if (gmsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You gathered " + ChatColor.GREEN + current.items.getAmount() + " " + current.items.getType().name());
                } else {
                    a.plugin.sendMessage(a, ap, gmsg);
                }
                moveAlong();
                break;
            case SLEEP:
                String slmsg = "";
                if (!current.flavor.isEmpty()) {
                    slmsg = current.flavor.get(0);
                }
                if (slmsg.equals("")) {
                    a.plugin.sendMessage(a, ap, "You awoke and felt nothing..");
                } else {
                    a.plugin.sendMessage(a, ap, slmsg);
                }
                moveAlong();
                break;
            case TELEPORT:
                int locs = current.getLoc();
                AdvLocation al = a.getLoc(locs);
                player.teleport(al.getLocation());
                moveAlong();
                break;
            case MESSAGE:
                String mmsg = "";
                if (!current.flavor.isEmpty()) {
                    mmsg = current.flavor.get(0);
                }
                a.plugin.sendMessage(a, ap, mmsg);
                moveAlong();
                break;
            case CONVERSE:
                moveAlong();
                break;
        }
    }

    public void sendFlavor() {
        if (current.flavor.size() > 1) {
            ArrayList<String> sending = (ArrayList<String>) current.flavor.clone();
            sending.remove(0);
            for (String s : sending) {
                a.plugin.sendMessage(a, ap, s);
            }
        }
    }
}
