package org.canoestudios.mobsiege.ai.additions;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import org.canoestudios.mobsiege.api.ITaskAddition;
import org.canoestudios.mobsiege.ai.SiegeAIAvoidExplosion;

public class AdditionAvoidExplosives implements ITaskAddition
{
    @Override
    public boolean isTargetTask() { return false; }

    @Override
    public int getTaskPriority(EntityLiving entityLiving) { return 1; }

    @Override
    public boolean isValid(EntityLiving entityLiving) {
        return entityLiving instanceof EntityCreature;
    }

    @Override
    public EntityAIBase getAdditionalAI(EntityLiving entityLiving) {
        return new SiegeAIAvoidExplosion((EntityCreature) entityLiving, 12.0f, 1.25, 1.25);
    }
}
