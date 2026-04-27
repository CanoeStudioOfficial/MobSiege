package org.canoestudios.mobsiege.ai.additions;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import org.canoestudios.mobsiege.api.ITaskAddition;
import org.canoestudios.mobsiege.ai.SiegeAIDigging;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class AdditionDigger implements ITaskAddition
{
    @Override
    public boolean isTargetTask() { return false; }

    @Override
    public int getTaskPriority(EntityLiving entityLiving) { return 4; }

    @Override
    public boolean isValid(EntityLiving entityLiving) {
        return SiegeProps.DIGGING.get(entityLiving);
    }

    @Override
    public EntityAIBase getAdditionalAI(EntityLiving entityLiving) {
        if (!entityLiving.world.isRemote && entityLiving.getRNG().nextInt(20) == 0) {
            entityLiving.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
        }
        return new SiegeAIDigging(entityLiving);
    }
}
