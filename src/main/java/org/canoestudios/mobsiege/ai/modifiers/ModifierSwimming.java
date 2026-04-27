package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.pathfinding.PathNavigateGround;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAISwimming;

public class ModifierSwimming implements ITaskModifier
{
    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return task.getClass() == EntityAISwimming.class && entityLiving.getNavigator() instanceof PathNavigateGround;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        return new SiegeAISwimming(host);
    }
}
