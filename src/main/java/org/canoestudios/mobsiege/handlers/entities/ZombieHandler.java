package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.capabilities.modified.IModifiedHandler;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class ZombieHandler
{
    @SubscribeEvent
    public void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityLivingBase attacker;
        if (event.getSource().getImmediateSource() instanceof EntityLivingBase && SiegeProps.INFECTIONS.get(event.getSource().getImmediateSource())) {
            attacker = (EntityLivingBase) event.getSource().getImmediateSource();
        } else {
            if (!(event.getSource().getTrueSource() instanceof EntityLivingBase) || !SiegeProps.INFECTIONS.get(event.getSource().getTrueSource())) return;
            attacker = (EntityLivingBase) event.getSource().getTrueSource();
        }
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(attacker.getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        Entity zombie = ee.newInstance(event.getEntity().world);
        zombie.setPosition(event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ);
        if (zombie instanceof EntityLiving) {
            ((EntityLiving) zombie).setCanPickUpLoot(true);
        }
        zombie.setCustomNameTag(event.getEntity().getName() + " (" + attacker.getName() + ")");
        IModifiedHandler handler = zombie.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if (handler != null) handler.setModified(true);
        event.getEntity().world.spawnEntity(zombie);
    }
}
