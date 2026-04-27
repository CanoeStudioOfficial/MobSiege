package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIWander;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAIWander;

public class ModifierWander implements ITaskModifier
{
    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return entityLiving instanceof EntityCreature && task instanceof EntityAIWander;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        return new SiegeAIWander((EntityCreature) host, (EntityAIWander) entry);
    }
}
