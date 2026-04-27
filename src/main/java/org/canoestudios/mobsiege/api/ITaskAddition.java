package org.canoestudios.mobsiege.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public interface ITaskAddition
{
    boolean isTargetTask();
    int getTaskPriority(EntityLiving entityLiving);
    boolean isValid(EntityLiving entityLiving);
    EntityAIBase getAdditionalAI(EntityLiving entityLiving);
}
