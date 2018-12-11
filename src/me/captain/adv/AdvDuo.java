//Do want you want with this trash code :)
package me.captain.adv;

import me.captain.adv.quest.AdvState;

/**
 *
 * @author andre
 */
public class AdvDuo {
    public Adventure a;
    public AdvLocation l;
    public AdvTeleport t;
    public AdvZone z;
    public AdvPlayer p;
    public AdvState s;
    
    public AdvDuo(Adventure adv, AdvLocation al) {
        a = adv;
        al = l;
    }
    public AdvDuo(Adventure adv, AdvTeleport tp) {
        a = adv;
        t = tp;
    }
    public AdvDuo(Adventure adv, AdvZone zone) {
        a = adv;
        z = zone;
    }
    public AdvDuo(Adventure adv, AdvPlayer pl) {
        a = adv;
        p = pl;
    }
    public AdvDuo(Adventure adv, AdvState st) {
        a = adv;
        s = st;
    }
}
