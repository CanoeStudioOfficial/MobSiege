package org.canoestudios.mobsiege.api;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public interface ITaskModifier
{
    boolean isValid(EntityLiving entityLiving, EntityAIBase task);
    EntityAIBase getReplacement(EntityLiving entityLiving, EntityAIBase task);
}
