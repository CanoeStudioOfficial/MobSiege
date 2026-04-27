package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class EndermanHandler
{
    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent event) {
        if (event.getEntity().world.isRemote || !(event.getEntityLiving() instanceof EntityEnderman)) return;
        EntityEnderman enderman = (EntityEnderman) event.getEntityLiving();
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(enderman.getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        EntityLivingBase target = enderman.getAttackTarget();
        if (target == null || enderman.getDistance(target) > 2.0f) return;
        if (enderman.getRNG().nextFloat() < 0.5f && SiegeProps.EN_TELE.get(enderman)) {
            event.setCanceled(true);
            target.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        }
    }
}
