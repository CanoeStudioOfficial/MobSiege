package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.monster.EntityCreeper;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAICreeperSwell;
import org.canoestudios.mobsiege.ai.SiegeAIJohnCena;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class ModifierCreeperSwell implements ITaskModifier
{
    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return entityLiving instanceof EntityCreeper && task.getClass() == EntityAICreeperSwell.class;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        if (host.world.rand.nextDouble() < SiegeProps.CR_CENA.get(host).doubleValue()) {
            return new SiegeAIJohnCena((EntityCreeper) host);
        }
        return new SiegeAICreeperSwell((EntityCreeper) host);
    }
}
