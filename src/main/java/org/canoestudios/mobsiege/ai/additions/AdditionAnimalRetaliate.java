package org.canoestudios.mobsiege.ai.additions;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.passive.EntityAnimal;
import org.canoestudios.mobsiege.api.ITaskAddition;

public class AdditionAnimalRetaliate implements ITaskAddition
{
    @Override
    public boolean isTargetTask() { return true; }

    @Override
    public int getTaskPriority(EntityLiving entityLiving) { return 3; }

    @Override
    public boolean isValid(EntityLiving entityLiving) {
        return entityLiving instanceof EntityAnimal;
    }

    @Override
    public EntityAIBase getAdditionalAI(EntityLiving entityLiving) {
        return new EntityAIHurtByTarget((EntityCreature) entityLiving, true);
    }
}
