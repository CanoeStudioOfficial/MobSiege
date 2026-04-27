package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;

public class SiegeAIWander extends EntityAIBase
{
    private final EntityAIWander wander;
    private final EntityCreature creature;
    private int delay;

    public SiegeAIWander(EntityCreature entity, EntityAIWander wander) {
        this.wander = wander;
        this.creature = entity;
        this.setMutexBits(wander.getMutexBits());
    }

    @Override
    public boolean shouldExecute() {
        if (delay > 0) {
            delay--;
            return false;
        }
        return wander.shouldExecute() && creature.getNavigator().noPath() && creature.getAttackTarget() == null && creature.getPassengers().size() <= 0;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return wander.shouldContinueExecuting();
    }

    @Override
    public void startExecuting() {
        delay = 20;
        wander.startExecuting();
    }
}
