//Do want you want with this trash code :)
package me.captain.adv.dialogue;

import java.util.ArrayList;
import java.util.UUID;
import me.captain.adv.AdvPlayer;
import me.captain.adv.Adventure;
import me.captain.adv.npc.CapNPC;
import me.captain.adv.quest.AdvPlayerProgress;
import org.bukkit.entity.Player;

/**
 *
 * @author andre
 */
public class DialoguePack {

    private final UUID talkto;
    private final Integer id;

    public LineTree playerlines;
    public NPCTree npclines;

    public int npccount;
    public int playercount;

    public boolean playerdone;
    public boolean npcdone;

    public Adventure a;
    public AdvPlayer player;
    public CapNPC npc;
    public AdvPlayerProgress app;

    public boolean player_turn;

    public DialoguePack(Adventure a, UUID to, int id) {
        this.a = a;
        talkto = to;
        npclines = new NPCTree();
        playerlines = new LineTree();
        this.id = id;
        npccount = 0;
        playercount = 0;
        player_turn = false;
    }

    /**
     * @return the talkto
     */
    public UUID getTalkto() {
        return talkto;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    public void start() {
        playerlines.max = playerlines.allOptions.size();
        npclines.max = npclines.allOptions.size();
        System.out.println("[MYOA] Starting dialogue with " + npc.getName() + " and " + player.displayName);
        if (player_turn) {
            Runnable r = () -> {
                a.plugin.sendOptions(a, player, playerlines.allOptions.get(playercount));
            };
            a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, r, 20);
        } else {
            Runnable r = () -> {
                npcTalk();
            };
            a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, r, 20);
        }
    }

    public void reply(int i) {
        Line l = playerlines.currentPlayerOptions.get(i);
        String text = l.getText();
        a.plugin.fakeChat(player, text);
        playerlines.lastLine = l;
        playerTreePlus();
        Runnable r = () -> {
            npcTalk();
        };
        a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, r, 27);
    }

    public void npcTalk() {
        if (npcdone) {
            end();
        } else {
            ArrayList<Line> current = npclines.currentNPCLines;
            if (current.size() < 2) {
                a.plugin.fakeChat(a, player, npc, current.get(0).getText());
                npclines.lastLine = current.get(0);
                npcTreePlus();
                return;
            } else {
                ResponseType rt = playerlines.lastLine.rt;
                if (rt == ResponseType.ACCEPT) {
                    for (Line l : current) {
                        if (l.rt == ResponseType.ACCEPTED) {
                            a.plugin.fakeChat(a, player, npc, l.getText());
                            app.execute();
                            npcTreePlus();
                            return;
                        }
                    }
                    System.out.println("[MYOA] No suitable response found. Check your dialogue ymls");
                } else if (rt == ResponseType.DECLINE) {
                    for (Line l : current) {
                        if (l.rt == ResponseType.DECLINED) {
                            a.plugin.fakeChat(a, player, npc, l.getText());
                            System.out.println("[MYOA] Quest denied, entering failstate");
                            app.current = app.quest.failstate;
                            app.pre();
                            npcTreePlus();
                            return;
                        }
                    }
                    System.out.println("[MYOA] No suitable response found. Check your dialogue ymls");
                }
            }
        }
    }

    public void playerTreePlus() {
        playercount++;
        player_turn = false;
        if (playercount >= playerlines.max) {
            playerdone = true;
        } else {
            playerlines.currentPlayerOptions = this.playerlines.allOptions.get(playercount);
        }
    }

    public void npcTreePlus() {
        npccount++;
        player_turn = true;
        if (npccount >= npclines.max) {
            npcdone = true;
        } else {
            npclines.currentNPCLines = this.npclines.allOptions.get(npccount);
        }
        if (!playerdone) {
            Runnable r = () -> {
                a.plugin.sendOptions(a, player, playerlines.allOptions.get(playercount));
            };
            a.plugin.getServer().getScheduler().scheduleSyncDelayedTask(a.plugin, r, 20);
        }
    }

    public void end() {
        a.npcm.dm.currentConvos.remove(this);
    }
}
