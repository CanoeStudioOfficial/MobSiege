package org.canoestudios.mobsiege.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.Vec3d;
import org.canoestudios.mobsiege.ai.utils.PredicateExplosives;

import java.util.List;

public class SiegeAIAvoidExplosion extends EntityAIBase
{
    private EntityCreature theEntity;
    private double farSpeed;
    private double nearSpeed;
    private Entity closestLivingEntity;
    private float avoidDistance;
    private Path entityPathEntity;
    private PathNavigate entityPathNavigate;
    private PredicateExplosives explosiveSelector;

    public SiegeAIAvoidExplosion(EntityCreature theEntityIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn) {
        this.theEntity = theEntityIn;
        this.avoidDistance = avoidDistanceIn;
        this.farSpeed = farSpeedIn;
        this.nearSpeed = nearSpeedIn;
        this.entityPathNavigate = theEntityIn.getNavigator();
        this.explosiveSelector = new PredicateExplosives(theEntityIn);
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (theEntity.ticksExisted % 10 != 0) return false;
        List<Entity> list = theEntity.world.getEntitiesWithinAABB(Entity.class, theEntity.getEntityBoundingBox().grow(avoidDistance, 3.0, avoidDistance), explosiveSelector);
        if (list.isEmpty()) return false;
        closestLivingEntity = list.get(0);
        Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(theEntity, 16, 7, new Vec3d(closestLivingEntity.posX, closestLivingEntity.posY, closestLivingEntity.posZ));
        if (vec3d == null) return false;
        if (closestLivingEntity.getDistanceSq(vec3d.x, vec3d.y, vec3d.z) < closestLivingEntity.getDistanceSq(theEntity)) return false;
        entityPathEntity = entityPathNavigate.getPathToXYZ(vec3d.x, vec3d.y, vec3d.z);
        return entityPathEntity != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return !entityPathNavigate.noPath();
    }

    @Override
    public void startExecuting() {
        entityPathNavigate.setPath(entityPathEntity, farSpeed);
    }

    @Override
    public void resetTask() {
        closestLivingEntity = null;
    }

    @Override
    public void updateTask() {
        if (theEntity.getDistanceSq(closestLivingEntity) < 49.0) {
            theEntity.getNavigator().setSpeed(nearSpeed);
        } else {
            theEntity.getNavigator().setSpeed(farSpeed);
        }
    }
}
