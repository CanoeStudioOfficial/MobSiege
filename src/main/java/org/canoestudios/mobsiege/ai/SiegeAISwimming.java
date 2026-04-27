package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public class SiegeAISwimming extends EntityAIBase
{
    private EntityLiving host;

    public SiegeAISwimming(EntityLiving host) {
        this.host = host;
        this.setMutexBits(4);
        if (host.getNavigator() instanceof net.minecraft.pathfinding.PathNavigateGround) {
            ((net.minecraft.pathfinding.PathNavigateGround) host.getNavigator()).setCanSwim(true);
        }
    }

    @Override
    public boolean shouldExecute() {
        if (!host.isInWater() && !host.isInLava()) return false;
        BlockPos pos = host.getPosition();
        Path path = host.getNavigator().getPath();
        EntityLivingBase target = host.getAttackTarget();
        return host.getAir() < 150 || ((path == null || path.getFinalPathPoint() == null || path.getFinalPathPoint().y >= pos.getY()) && (target == null || target.getPosition().getY() >= pos.getY() || host.getDistance(target) >= 8.0f));
    }

    @Override
    public void updateTask() {
        host.getJumpHelper().setJumping();
    }
}
