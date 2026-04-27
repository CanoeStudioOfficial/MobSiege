package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityCreeper;
import org.canoestudios.mobsiege.ai.utils.CreeperHooks;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAIJohnCena extends EntityAIBase
{
    private EntityCreeper creeper;
    private CreeperHooks creeperHooks;
    private int blastSize = -1;

    public SiegeAIJohnCena(EntityCreeper creeper) {
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

    @Override
    public boolean shouldContinueExecuting() {
        return true;
    }

    private boolean canBreachEntity(EntityLivingBase target) {
        return creeper.ticksExisted > 60 && target != null && !creeper.isRiding() && !creeper.hasPath() && creeper.getDistance(target) < 64.0f && SiegeProps.CR_BREACH.get(creeper);
    }

    @Override
    public void startExecuting() {
        creeper.setCustomNameTag("John Cena");
    }

    @Override
    public void resetTask() {
    }

    @Override
    public void updateTask() {
        creeper.setCreeperState(1);
    }
}
