package org.canoestudios.mobsiege.ai.additions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.canoestudios.mobsiege.api.ITaskAddition;
import org.canoestudios.mobsiege.ai.SiegeAIDemolition;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class AdditionDemolition implements ITaskAddition
{
    @Override
    public boolean isTargetTask() { return false; }

    @Override
    public int getTaskPriority(EntityLiving entityLiving) { return 4; }

    @Override
    public boolean isValid(EntityLiving entityLiving) {
        return SiegeProps.DEMOLITION.get(entityLiving).doubleValue() > 0.0;
    }

    @Override
    public EntityAIBase getAdditionalAI(EntityLiving entityLiving) {
        if (!entityLiving.world.isRemote && entityLiving.getRNG().nextDouble() < SiegeProps.DEMOLITION.get(entityLiving).doubleValue()) {
            entityLiving.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, new ItemStack(Blocks.TNT));
        }
        return new SiegeAIDemolition(entityLiving);
    }
}
