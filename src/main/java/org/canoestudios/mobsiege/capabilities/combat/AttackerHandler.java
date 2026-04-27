package org.canoestudios.mobsiege.capabilities.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;

import java.util.Iterator;
import java.util.TreeSet;

public class AttackerHandler
{
    private final EntityLiving host;
    private final TreeSet<EntityLiving> atkMap;

    public AttackerHandler(EntityLiving host) {
        this.host = host;
        this.atkMap = new TreeSet<>((e1, e2) -> host == null ? 0 : (int) Math.signum(e1.getDistanceSq(host) - e2.getDistanceSq(host)));
    }

    public boolean canAttack(EntityLiving attacker) {
        return getAttackers() < SiegeConfigGlobal.TargetCap;
    }

    public void addAttacker(EntityLiving attacker) {
        if (attacker == null || attacker.isDead || attacker.getAttackTarget() != host) {
            return;
        }
        atkMap.add(attacker);
    }

    public int getAttackers() {
        return atkMap.size();
    }

    public void updateAttackers() {
        Iterator<EntityLiving> iter = atkMap.descendingIterator();
        while (iter.hasNext()) {
            EntityLiving att = iter.next();
            if (atkMap.size() > SiegeConfigGlobal.TargetCap || att.isDead || att.getAttackTarget() != host) {
                iter.remove();
            }
        }
    }
}
