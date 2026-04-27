package org.canoestudios.mobsiege.ai.additions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import org.canoestudios.mobsiege.api.ITaskAddition;
import org.canoestudios.mobsiege.ai.SiegeAIGrief;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class AdditionGrief implements ITaskAddition
{
    @Override
    public boolean isTargetTask() { return false; }

    @Override
    public int getTaskPriority(EntityLiving entityLiving) { return 4; }

    @Override
    public boolean isValid(EntityLiving entityLiving) {
        return SiegeProps.GRIEF.get(entityLiving);
    }

    @Override
    public EntityAIBase getAdditionalAI(EntityLiving entityLiving) {
        return new SiegeAIGrief(entityLiving);
    }
}
