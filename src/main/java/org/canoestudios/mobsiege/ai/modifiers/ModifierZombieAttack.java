package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIZombieAttack;
import net.minecraft.entity.monster.EntityZombie;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAIZombieAttack;

public class ModifierZombieAttack implements ITaskModifier
{
    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return task != null && task.getClass() == EntityAIZombieAttack.class && entityLiving instanceof EntityZombie;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        try {
            return new SiegeAIZombieAttack((EntityZombie) host, ModifierAttackMelee.f_speed.getDouble(entry), true);
        } catch (Exception e) {
            return new SiegeAIZombieAttack((EntityZombie) host, 1.0, true);
        }
    }
}
