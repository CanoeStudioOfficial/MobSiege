package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityVillager;
import org.canoestudios.mobsiege.api.ITaskModifier;

public class ModifierVillagerAvoid implements ITaskModifier
{
    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return entityLiving instanceof EntityVillager && task.getClass() == EntityAIAvoidEntity.class;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        return new EntityAIAvoidEntity<>((EntityCreature) host, EntityMob.class, 12.0f, 0.6, 0.6);
    }
}
