package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public abstract class SiegeAITarget extends EntityAIBase
{
    private final EntityLiving taskOwner;
    private boolean shouldCheckSight;
    private boolean nearbyOnly;
    private int targetSearchStatus;
    private int targetSearchDelay;
    private int targetUnseenTicks;
    private EntityLivingBase target;
    private int unseenMemoryTicks = 60;

    public SiegeAITarget(EntityLiving creature, boolean checkSight, boolean onlyNearby) {
        this.taskOwner = creature;
        this.shouldCheckSight = checkSight;
        this.nearbyOnly = onlyNearby;
    }

    @Override
    public boolean shouldContinueExecuting() {
        EntityLivingBase entitylivingbase = taskOwner.getAttackTarget();
        if (entitylivingbase == null && target != null) {
            entitylivingbase = target;
        }
        if (entitylivingbase == null) return false;
        if (!entitylivingbase.isEntityAlive()) return false;
        Team team = taskOwner.getTeam();
        Team team2 = entitylivingbase.getTeam();
        if (team != null && team2 == team) return false;
        double d0 = getTargetDistance();
        if (taskOwner.getDistanceSq(entitylivingbase) > d0 * d0) return false;
        if (shouldCheckSight) {
            if (taskOwner.getEntitySenses().canSee(entitylivingbase)) {
                targetUnseenTicks = 0;
            } else if (++targetUnseenTicks > unseenMemoryTicks) {
                return false;
            }
        }
        if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer) entitylivingbase).capabilities.disableDamage) {
            return false;
        }
        taskOwner.setAttackTarget(entitylivingbase);
        return true;
    }

    public double getTargetDistance() {
        return taskOwner.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
    }

    @Override
    public void startExecuting() {
        targetSearchStatus = 0;
        targetSearchDelay = 0;
        targetUnseenTicks = 0;
    }

    @Override
    public void resetTask() {
        taskOwner.setAttackTarget(null);
        target = null;
    }

    protected boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles) {
        if (target == null || target == taskOwner || !target.isEntityAlive()) return false;
        if (!taskOwner.canAttackClass(target.getClass())) return false;
        if (taskOwner.isOnSameTeam(target)) return false;
        if (taskOwner instanceof net.minecraft.entity.IEntityOwnable) {
            net.minecraft.entity.IEntityOwnable ownable = (net.minecraft.entity.IEntityOwnable) taskOwner;
            if (ownable.getOwnerId() != null) {
                if (target instanceof net.minecraft.entity.IEntityOwnable && target.getUniqueID().equals(ownable.getOwnerId())) return false;
                if (target == ownable.getOwner()) return false;
            }
        } else if (target instanceof EntityPlayer && !includeInvincibles && ((EntityPlayer) target).capabilities.disableDamage) {
            return false;
        }
        if (!shouldCheckSight && !taskOwner.getEntitySenses().canSee(target)) return false;
        if (taskOwner instanceof net.minecraft.entity.EntityCreature) {
            if (!((net.minecraft.entity.EntityCreature) taskOwner).isWithinHomeDistanceFromPosition(new BlockPos(target))) return false;
        }
        if (nearbyOnly) {
            if (--targetSearchDelay <= 0) targetSearchStatus = 0;
            if (targetSearchStatus == 0) targetSearchStatus = canEasilyReach(target) ? 1 : 2;
            return targetSearchStatus != 2;
        }
        return true;
    }

    private boolean canEasilyReach(EntityLivingBase target) {
        targetSearchDelay = Math.max(10 + taskOwner.getRNG().nextInt(5), (int) taskOwner.getDistance(target) - 16);
        Path pathentity = taskOwner.getNavigator().getPathToEntityLiving(target);
        if (pathentity == null) return false;
        PathPoint pathpoint = pathentity.getFinalPathPoint();
        if (pathpoint == null) return false;
        int i = pathpoint.x - MathHelper.floor(target.posX);
        int j = pathpoint.z - MathHelper.floor(target.posZ);
        return i * i + j * j <= 2.25;
    }
}
