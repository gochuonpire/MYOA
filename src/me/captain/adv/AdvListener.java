//Do want you want with this trash code :)
package me.captain.adv;

import me.captain.adv.quest.AdvStateType;
import me.captain.adv.quest.AdvPlayerProgress;
import me.captain.adv.quest.AdvQuest;
import me.captain.adv.quest.AdvState;
import me.captain.adv.npc.CapNPC;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import me.captain.adv.dialogue.DialoguePack;
import me.captain.adv.faction.Faction;
import me.captain.adv.faction.FactionType;
import net.citizensnpcs.api.event.NPCDamageByEntityEvent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Entity;
import org.bukkit.Material;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import net.citizensnpcs.api.event.NPCDeathEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.mcmonkey.sentinel.SentinelTrait;

/**
 *
 * @author andre
 */
public class AdvListener implements Listener {

    public MYOA plugin;

    public AdvListener(MYOA instance) {
        plugin = instance;
    }

    @EventHandler
    public void onNPCRightClickEvent(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        Adventure a = plugin.getAdventure(player);
        if (a != null) {
            AdvPlayer ap = a.getPlayer(player.getUniqueId());
            CapNPC cn = a.npcm.getNPC(npc.getId());
            if (cn.shouldStop.containsKey(ap.getUserid())) {
                System.out.println("[MYOA] " + cn.getName() + " shouldstop " + player.getUniqueId().toString());
                ArrayList<AdvQuest> quests = cn.shouldStop.get(ap.getUserid());
                for (AdvQuest q : quests) {
                    if (ap.quests.containsKey(q)) {
                        if (ap.quests.get(q).current.getNPC() == npc.getId()) {
                            if (ap.quests.get(q).current.type == AdvStateType.CONVERSE) {
                                System.out.println("[MYOA] Npc related to quest, setting up convo");
                                a.npcm.dm.setupConvo(cn, ap, ap.quests.get(q));
                            }
                        }
                    }

                }
            } else {
                for (FactionType ft : cn.factions) {
                    Faction f = a.npcm.fm.factions.get(ft);
                    if (f.shouldStop.containsKey(ap.getUserid())) {
                        DialoguePack dp = f.shouldStop.get(ap.getUserid());
                        System.out.println("[MYOA] Serving stopped DP");
                        a.npcm.dm.serveDP(dp, cn, player);
                    }
                }
                System.out.println("[MYOA] Serving random convo");
                a.npcm.dm.serveUpRandomConvo(cn, ap);
                return;
            }
        }
    }

    @EventHandler
    public void onNPCDeathEvent(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        CapNPC cn = plugin.getNPC(npc);
        Player player = cn.lastDamaged;
        if (player != null) {
            Adventure a = plugin.getAdventure(player);
            if (a != null) {
                AdvPlayer ap = a.getPlayer(player.getUniqueId());
                Location loc = player.getLocation();
                Collection<Entity> witnesses = loc.getWorld().getNearbyEntities(loc, 20, 20, 20);
                System.out.println("[MYOA] NPC " + npc.getName() + "-" + npc.getId() + " killed by " + player.getDisplayName());
                if (!cn.factions.isEmpty()) {
                    for (Entity e : witnesses) {
                        if (e != null) {
                            CapNPC c = a.npcm.getNPC(e);
                            if (c.getId() > 0) {
                                if (c.getId() != cn.getId()) {
                                    System.out.println("[MYOA] " + ap.name + " witnessed killing " + cn.getName() + "by " + c.getName());
                                    a.npcm.fm.addShitlist(ap, c);
                                }
                            }
                        }
                    }
                }
                for (AdvQuest aq : ap.getQuests()) {
                    AdvPlayerProgress app = ap.quests.get(aq);
                    if (app.current.type == AdvStateType.KILL) {
                        if (app.current.getNPC() == npc.getId()) {
                            npc.destroy();
                            plugin.citizens.getNPCRegistry().deregister(npc);
                            cn.spawned = false;
                            cn.killed = true;
                            app.execute();
                            return;

                        }
                    }
                }
                if (npc.hasTrait(SentinelTrait.class
                )) {
                    npc.removeTrait(SentinelTrait.class
                    );
                    npc.despawn();
                    npc.destroy();
                }
            }
        } else {
            System.out.println("[MYOA] " + npc.getName() + "-" + npc.getId() + " has died, respawning in 10 seconds");
            Runnable r = () -> {
                npc.despawn();
                npc.spawn(cn.anchor);
            };
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r, 200);
        }
    }

    @EventHandler
    public void onNPCDamageByEntityEvent(NPCDamageByEntityEvent event) {
        NPC npc = event.getNPC();
        DamageCause dc = event.getCause();
        Entity damager = (Entity) event.getDamager();
        if (damager instanceof Player) {
            Player player = (Player) damager;
            Adventure a = plugin.getAdventure(player);
            if (a != null) {
                CapNPC cp = a.npcm.getNPC(npc.getId());
                cp.lastDamaged = player;
                System.out.println("[MYOA] Allowed damage to " + npc.getName() + "-" + npc.getId());
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Entity e = event.getEntity();
        if (e.hasMetadata("NPC")) {
            event.setDeathMessage("");
        }
    }

    @EventHandler
    public void onPlayerChatEvent(PlayerChatEvent event) {
        Player player = event.getPlayer();
        Adventure a = plugin.getAdventure(player);
        System.out.println("[MYOA] Player chatted");
        if (a != null) {
            DialoguePack dp = a.npcm.dm.getDP(player.getUniqueId());
            if (dp != null) {
                if (dp.player_turn) {
                    int highest = dp.playerlines.currentPlayerOptions.size();
                    try {
                        Integer pos = Integer.parseInt(event.getMessage().substring(event.getMessage().length() - 1));
                        pos = pos - 1;
                        dp.reply(pos);
                        event.setCancelled(true);
                    } catch (NumberFormatException e) {
                        plugin.sendMessage(a, a.getPlayer(player.getUniqueId()), "Invalid chat option, enter a number <= " + highest);
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    System.out.println("[MYOA] Not the player's turn");
                }
            } else {
                System.out.println("[MYOA] No dialogue pack found for player");
            }
            AdvPlayer ap = a.getPlayer(player.getUniqueId());
            if (ap != null) {
                if (a.npcm.dm.shopping.containsKey(player)) {
                    Integer pos = Integer.parseInt(event.getMessage().substring(event.getMessage().length() - 1));
                    pos = pos - 1;
                    if (pos == 0) {
                        a.npcm.dm.sendShopLine(ap, a.npcm.dm.shopping.get(player).accept);
                        event.setCancelled(true);
                        return;
                    } else if (pos == 1) {
                        a.npcm.dm.sendShopLine(ap, a.npcm.dm.shopping.get(player).deny);
                        event.setCancelled(true);
                        return;
                    }
                }
                for (AdvQuest aq : ap.getQuests()) {
                    AdvPlayerProgress app = ap.quests.get(aq);
                    if (app.current.type == AdvStateType.SPEAK) {
                        CapNPC npc = a.npcm.getNPC(app.current.getNPC());
                        Location npcloc = npc.anchor;
                        if (player.getLocation().distance(npcloc) < 3) {
                            app.execute();
                        }
                    }
                }
            }
        } else {
            System.out.println("[MYOA] No adventure found for player");
        }
    }

    @EventHandler
    public void onPlayerBedLeaveEvent(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        Adventure a = plugin.getAdventure(player);
        if (a != null) {
            AdvPlayer ap = a.getPlayer(player.getUniqueId());
            if (ap != null) {
                for (AdvQuest aq : ap.getQuests()) {
                    AdvPlayerProgress app = ap.quests.get(aq);
                    if (app.current.type == AdvStateType.SLEEP) {
                        World w = player.getWorld();
                        long time = w.getTime();
                        if (time < 1000) {
                            if (event.getBed().getLocation().distance(a.getLoc(app.current.getLoc()).getLocation()) < 2) {
                                app.execute();
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() == Material.WALL_SIGN || event.getClickedBlock().getType() == Material.SIGN) {
            Adventure a = plugin.getAdventure(player);
            if (a != null) {
                AdvPlayer ap = a.getPlayer(player.getUniqueId());
                if (ap != null) {
                    for (AdvQuest aq : ap.getQuests()) {
                        AdvPlayerProgress app = ap.quests.get(aq);
                        if (app.current.type == AdvStateType.SIGN) {
                            if (event.getClickedBlock().getLocation().distance(a.getLoc(app.current.getLoc()).getLocation()) < 0.03) {
                                app.execute();
                            }
                        }
                    }
                }
            }
        }
        if (player.hasPermission("myoa.*") && plugin.cp.containsKey(player) && event.getPlayer().getInventory().getItemInMainHand().getType() == Material.DIAMOND) {
            Creating c = plugin.cp.get(player);
            if (c.loc1 == null && c.type == 0) {
                c.loc1 = event.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.GRAY + "Set loc1 to (" + ChatColor.GREEN + c.loc1.getBlockX() + ChatColor.GRAY + "," + ChatColor.GREEN + c.loc1.getBlockY() + ChatColor.GRAY + "," + ChatColor.GREEN + c.loc1.getBlockZ() + ChatColor.GRAY + ")");
            } else if (c.type == 0) {
                c.loc2 = event.getClickedBlock().getLocation();
                player.sendMessage(ChatColor.GRAY + "Set loc2 to (" + ChatColor.GREEN + c.loc2.getBlockX() + ChatColor.GRAY + "," + ChatColor.GREEN + c.loc2.getBlockY() + ChatColor.GRAY + "," + ChatColor.GREEN + c.loc2.getBlockZ() + ChatColor.GRAY + ")");
                AdvZone az = new AdvZone(c.advid, c.name, c.id, c.node, c.loc1, c.loc2, c.offset, new ArrayList<>());
                Adventure a = plugin.getAdventure(c.advid);
                a.addZone(az);
                plugin.cp.remove(player);
                player.sendMessage(ChatColor.GRAY + "Zone " + ChatColor.GREEN + az.getName() + ChatColor.GRAY + " finished");
            } else if (c.type == 2) {
                AdvLocation al = new AdvLocation(c.name, c.id, event.getClickedBlock().getLocation(), null);
                Adventure a = plugin.getAdventure(c.advid);
                a.addLocation(al);
                plugin.cp.remove(player);
                player.sendMessage(ChatColor.GRAY + "Added sign location, set name with /adv name loc <name goes here>");
            }
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Adventure a = plugin.getAdventure(player);
        if (a != null) {
            for (AdvPlayer ap : a.players) {
                ap.name = player.getDisplayName();
                if (ap.getUserid().compareTo(player.getUniqueId()) == 0) {
                    for (Faction f : a.npcm.fm.factions.values()) {
                        for (UUID u : f.playerEnemies) {
                            if (player.getUniqueId().compareTo(u) == 0) {
                                Runnable re = () -> {
                                    System.out.println("[MYOA] UUIDs Equal, adding " + player.getDisplayName() + " to " + f.getName() + " target list");
                                    a.npcm.fm.updateTargets(f, ap);
                                };
                                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, re, 20);
                            }
                        }
                    }
                    for (AdvQuest aq : ap.getQuests()) {
                        AdvPlayerProgress app = ap.quests.get(aq);
                        app.player = player;
                    }
                    //a.npcm.reskinNPCs();
                }
            }
        } else {
            if (plugin.defaultAdv != null) {
                AdvPlayer ap = new AdvPlayer(player.getUniqueId(), 0);
                plugin.sendMessage(plugin.defaultAdv, ap, "You have just started " + plugin.defaultAdv.getName());
                plugin.sendMessage(plugin.defaultAdv, ap, "Create a character with /adv class <chartype>");
                plugin.sendMessage(plugin.defaultAdv, ap, "You can currently choose from:");
                String msg = "[";
                for (AdvCharType t : AdvCharType.values()) {
                    msg += t.toString().toLowerCase() + ", ";
                }
                msg = msg.substring(0, msg.length() - 1);
                plugin.sendMessage(plugin.defaultAdv, ap, msg);
            }
        }
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Adventure adv = plugin.getAdventure(player);
        if (adv != null) {
            AdvPlayer ap = adv.getPlayer(player.getUniqueId());
            AdvDuo ad = plugin.isInside(event.getTo(), ap);
            if (ad != null) {
                Adventure a = ad.a;
                AdvTeleport at = a.getTeleport(ad.z);
                if (at != null) {
                    AdvLocation loc = a.getLoc(at.getLoc());
                    Location tp = loc.getLocation();
                    player.teleport(tp);
                    if (loc.getFlavor().isEmpty()) {
                        plugin.sendMessage(a, ap, "Welcome to " + ChatColor.GREEN + loc.getName());
                    } else {
                        plugin.sendMessage(a, ap, loc.getFlavor().get(0));
                    }
                } else {
                    for (AdvQuest aq : ad.p.getQuests()) {
                        AdvPlayerProgress adp = ad.p.quests.get(aq);
                        AdvState cur = adp.current;
                        if (cur.getType() == AdvStateType.ARRIVE) {
                            AdvZone arzone = a.getZone(cur.getZone());
                            if (Objects.equals(arzone.getId(), ad.z.getId())) {
                                adp.execute();
                            }
                        }
                    }
                }
            }
            Block b = player.getTargetBlock(null, 2);
            AdvQuest aq = plugin.getAdventure(player).getQuestByGiver(b.getLocation());
            if (aq != null) {
                if (!ap.quests.containsKey(aq)) {
                    AdvPlayerProgress app = new AdvPlayerProgress(adv, aq, aq.getStart());
                    app.player = player;
                    app.ap = ap;
                    ap.quests.put(aq, app);
                    app.pre();
                }
            }
        }
    }
}
