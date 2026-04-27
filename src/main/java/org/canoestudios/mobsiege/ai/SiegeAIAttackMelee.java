package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAIAttackMelee extends EntityAIBase
{
    private EntityLiving attacker;
    int attackTick;
    private double speedTowardsTarget;
    private boolean longMemory;
    private Path entityPathEntity;
    private int delayCounter;
    private boolean strafeClockwise;
    private boolean canStrafe;

    public SiegeAIAttackMelee(EntityLiving creature, double speedIn, boolean useLongMemory) {
        this.attacker = creature;
        this.speedTowardsTarget = speedIn;
        this.longMemory = useLongMemory;
        this.setMutexBits(3);
        this.strafeClockwise = true;
        this.canStrafe = creature.getRNG().nextInt(10) != 0;
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        float dist = attacker.getDistance(target);
        delayCounter--;
        if (delayCounter > 0 || dist <= MathHelper.sqrt(getAtkRangeSq(target))) return true;
        entityPathEntity = attacker.getNavigator().getPathToEntityLiving(target);
        delayCounter = Math.max(4 + attacker.getRNG().nextInt(7), (int) Math.ceil(dist / 8.0) * 10);
        return entityPathEntity != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        if (!longMemory && attacker.getDistance(target) > SiegeProps.XRAY_VIEW.get(attacker).intValue()) {
            return !attacker.getNavigator().noPath();
        }
        return (!(attacker instanceof EntityCreature) || ((EntityCreature) attacker).isWithinHomeDistanceFromPosition(target.getPosition())) && (!(target instanceof EntityPlayer) || (!((EntityPlayer) target).isSpectator() && !((EntityPlayer) target).isCreative()));
    }

    @Override
    public void startExecuting() {
        attacker.getNavigator().setPath(entityPathEntity, speedTowardsTarget);
        delayCounter = 0;
    }

    @Override
    public void resetTask() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target instanceof EntityPlayer && (((EntityPlayer) target).isSpectator() || ((EntityPlayer) target).isCreative())) {
            attacker.setAttackTarget(null);
        }
        attacker.getNavigator().clearPath();
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return;
        attacker.getLookHelper().setLookPositionWithEntity(target, 30.0f, 30.0f);
        double distSq = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        float dist = MathHelper.sqrt(distSq);
        double atkRangeSq = getAtkRangeSq(target);
        delayCounter--;
        if ((longMemory || attacker.getEntitySenses().canSee(target)) && delayCounter <= 0 && (distSq >= 1.0 || attacker.getRNG().nextFloat() < 0.05f)) {
            delayCounter = 4 + attacker.getRNG().nextInt(7);
            int pathingPenalty = 0;
            boolean onTarget = false;
            if (attacker.getNavigator().getPath() != null) {
                PathPoint finalPathPoint = attacker.getNavigator().getPath().getFinalPathPoint();
                if (finalPathPoint != null && target.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                    onTarget = true;
                } else {
                    pathingPenalty = (int) Math.ceil(dist / 8.0) * 10;
                }
            } else {
                pathingPenalty = (int) Math.ceil(dist / 8.0) * 10;
            }
            delayCounter += pathingPenalty;
            if (!onTarget && !attacker.getNavigator().tryMoveToEntityLiving(target, speedTowardsTarget)) {
                delayCounter += 15;
            }
            if (delayCounter >= 60) delayCounter = 60;
        }
        if (attackTick > 0) attackTick--;
        if (canStrafe && SiegeProps.STRAFE.get(attacker) && attackTick > 0 && dist < 2.0f && attacker.canEntityBeSeen(target)) {
            attacker.getMoveHelper().strafe(0.5f, strafeClockwise ? 0.5f : -0.5f);
            attacker.faceEntity(target, 30.0f, 30.0f);
        }
        if (distSq <= atkRangeSq && attackTick <= 0) {
            strafeClockwise = attacker.getRNG().nextBoolean();
            attackTick = 10 + attacker.getRNG().nextInt(10);
            attacker.swingArm(EnumHand.MAIN_HAND);
            if (attacker instanceof EntityAnimal) {
                target.attackEntityFrom(DamageSource.causeMobDamage(attacker), 1.0f);
            } else {
                attacker.attackEntityAsMob(target);
            }
            delayCounter = 0;
        }
    }

    private double getAtkRangeSq(EntityLivingBase attackTarget) {
        return attacker.width * 2.0f * attacker.width * 2.0f + attackTarget.width;
    }
}
