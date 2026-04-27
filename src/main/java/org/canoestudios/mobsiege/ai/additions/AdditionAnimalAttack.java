package org.canoestudios.mobsiege.ai.additions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.passive.EntityAnimal;
import org.canoestudios.mobsiege.api.ITaskAddition;
import org.canoestudios.mobsiege.ai.SiegeAIAttackMelee;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class AdditionAnimalAttack implements ITaskAddition
{
    @Override
    public boolean isTargetTask() { return false; }

    @Override
    public int getTaskPriority(EntityLiving entityLiving) { return 3; }

    @Override
    public boolean isValid(EntityLiving entityLiving) {
        if (!SiegeProps.ANIMAL_RET.get(entityLiving) || !(entityLiving instanceof EntityAnimal)) return false;
        for (net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry entry : entityLiving.tasks.taskEntries) {
            if (entry.action.getClass() == EntityAIAttackMelee.class) return false;
        }
        return true;
    }

    @Override
    public EntityAIBase getAdditionalAI(EntityLiving entityLiving) {
        return new SiegeAIAttackMelee(entityLiving, 1.5, true);
    }
}
