package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAIAttackRanged extends EntityAIBase
{
    private final EntityLiving entityHost;
    private final IRangedAttackMob rangedHost;
    private EntityLivingBase attackTarget;
    private int rangedAttackTime = -1;
    private final double entityMoveSpeed;
    private int seeTime;
    private final int attackIntervalMin;
    private final int maxRangedAttackTime;
    private boolean strafingClockwise;
    private int strafingTime = -1;
    private int navDelay;

    public SiegeAIAttackRanged(IRangedAttackMob attacker, double movespeed, int attackIntervalMin, int maxAttackTime) {
        if (!(attacker instanceof EntityLivingBase)) {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        }
        this.rangedHost = attacker;
        this.entityHost = (EntityLiving) attacker;
        this.entityMoveSpeed = movespeed;
        this.attackIntervalMin = attackIntervalMin;
        this.maxRangedAttackTime = maxAttackTime;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase entitylivingbase = entityHost.getAttackTarget();
        if (entitylivingbase == null) return false;
        attackTarget = entitylivingbase;
        return true;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute() || !entityHost.getNavigator().noPath();
    }

    @Override
    public void resetTask() {
        attackTarget = null;
        seeTime = 0;
        rangedAttackTime = -1;
    }

    @Override
    public void updateTask() {
        double distSq = entityHost.getDistanceSq(attackTarget.posX, attackTarget.getEntityBoundingBox().minY, attackTarget.posZ);
        float dist = MathHelper.sqrt(distSq);
        boolean canSee = entityHost.canEntityBeSeen(attackTarget);
        float atkDist = getAttackDistance();
        float atkDistSq = atkDist * atkDist;
        seeTime = canSee ? seeTime + 1 : 0;
        navDelay--;
        boolean onTarget = false;
        if (entityHost.getNavigator().getPath() != null) {
            PathPoint finalPathPoint = entityHost.getNavigator().getPath().getFinalPathPoint();
            if (finalPathPoint != null && attackTarget.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < (distSq > 1024.0 ? 16 : 1)) {
                onTarget = true;
            } else {
                navDelay = Math.max(10, (int) distSq - 6);
            }
        }
        if (distSq <= atkDistSq && seeTime >= 20) {
            entityHost.getNavigator().clearPath();
        } else if (!onTarget && navDelay <= 0) {
            entityHost.getNavigator().tryMoveToEntityLiving(attackTarget, entityMoveSpeed);
            navDelay = Math.max(10, (int) distSq - 6);
        }
        if (strafingTime >= 60) {
            if (entityHost.getRNG().nextFloat() < 0.3f) strafingClockwise = !strafingClockwise;
            strafingTime = entityHost.getRNG().nextInt(20);
        } else {
            strafingTime++;
        }
        if (dist <= atkDist) {
            boolean strafingBackwards = dist < atkDist * 0.25f;
            entityHost.getMoveHelper().strafe(strafingBackwards ? -0.5f : 0.5f, strafingClockwise ? 0.5f : -0.5f);
            entityHost.faceEntity(attackTarget, 30.0f, 30.0f);
        } else {
            entityHost.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0f, 30.0f);
        }
        rangedAttackTime--;
        if (rangedAttackTime == 0) {
            if (distSq > atkDistSq || !canSee) return;
            float f = MathHelper.sqrt(distSq) / atkDist;
            float distFactor = MathHelper.clamp(f, 0.1f, 1.0f);
            rangedHost.attackEntityWithRangedAttack(attackTarget, distFactor);
            rangedAttackTime = MathHelper.floor(f * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
        } else if (rangedAttackTime < 0) {
            float f2 = MathHelper.sqrt(distSq) / atkDist;
            rangedAttackTime = MathHelper.floor(f2 * (maxRangedAttackTime - attackIntervalMin) + attackIntervalMin);
        }
    }

    private float getAttackDistance() {
        return SiegeProps.RANGE_DIST.get(entityHost).floatValue();
    }
}
