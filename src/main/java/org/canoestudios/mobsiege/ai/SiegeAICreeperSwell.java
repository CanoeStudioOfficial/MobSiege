package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import org.canoestudios.mobsiege.ai.utils.CreeperHooks;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAICreeperSwell extends EntityAIBase
{
    private EntityCreeper creeper;
    private EntityLivingBase attackTarget;
    private CreeperHooks creeperHooks;
    private boolean detLocked;
    private int blastSize;

    public SiegeAICreeperSwell(EntityCreeper creeper) {
        this.creeper = creeper;
        this.creeperHooks = new CreeperHooks(creeper);
        if (!SiegeProps.CR_FUSE.get(creeper)) {
            this.setMutexBits(1);
        }
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = creeper.getAttackTarget();
        if (blastSize < 0) blastSize = creeperHooks.getExplosionSize();
        return creeper.getCreeperState() > 0 || canBreachEntity(target) || (target != null && creeper.getDistanceSq(target) < blastSize * blastSize);
    }

    private boolean canBreachEntity(EntityLivingBase target) {
        return creeper.ticksExisted > 60 && target != null && !creeper.isRiding() && !creeper.hasPath() && creeper.getDistance(target) < 64.0f && SiegeProps.CR_BREACH.get(creeper);
    }

    @Override
    public void startExecuting() {
        attackTarget = creeper.getAttackTarget();
    }

    @Override
    public void resetTask() {
        attackTarget = null;
    }

    @Override
    public void updateTask() {
        if (detLocked) {
            creeper.setCreeperState(1);
            return;
        }
        int finalBlastSize = blastSize * (creeperHooks.isPowered() ? 2 : 1);
        boolean breaching = canBreachEntity(attackTarget);
        if (attackTarget == null) {
            creeper.setCreeperState(-1);
        } else if (creeper.getDistanceSq(attackTarget) > finalBlastSize * finalBlastSize && !breaching) {
            creeper.setCreeperState(-1);
        } else if (!creeper.getEntitySenses().canSee(attackTarget) && !breaching) {
            creeper.setCreeperState(-1);
        } else {
            detLocked = true;
            creeper.setCreeperState(1);
        }
    }
}
