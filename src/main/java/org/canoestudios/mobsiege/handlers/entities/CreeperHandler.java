package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.capabilities.modified.IModifiedHandler;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;
import org.canoestudios.mobsiege.handlers.MainHandler;

import java.lang.reflect.Field;

public class CreeperHandler
{
    private static Field f_isFlaming;
    private static Field f_explosionSize;
    private static Field f_POWERED;

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityCreeper)) return;
        EntityCreeper creeper = (EntityCreeper) event.getEntity();
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(creeper.getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        IModifiedHandler handler = creeper.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if (handler == null || handler.isModified()) return;
        handler.setModified(true);
        if (event.getWorld().rand.nextDouble() < SiegeProps.CR_POWERED.get(creeper).doubleValue()) {
            try {
                creeper.getDataManager().set((DataParameter) f_POWERED.get(creeper), true);
            } catch (Exception e) {
                org.canoestudios.mobsiege.MobSiege.LOGGER.error("Unable to set creeper powered state", e);
            }
        }
    }

    @SubscribeEvent
    public void onExplode(ExplosionEvent.Start event) {
        if (event.getWorld().isRemote || event.isCanceled()) return;
        EntityLivingBase entity = event.getExplosion().getExplosivePlacedBy();
        if (f_isFlaming == null || !(entity instanceof EntityCreeper)) return;
        if (SiegeProps.CR_FIRE.get(entity)) {
            try {
                f_isFlaming.set(event.getExplosion(), true);
            } catch (Exception e) {
                org.canoestudios.mobsiege.MobSiege.LOGGER.error("Failed to set creeper blast to flaming", e);
            }
        }
        if (SiegeProps.CR_CENA.get(entity).doubleValue() > 0.0 && entity.getCustomNameTag().equalsIgnoreCase("John Cena")) {
            try {
                f_explosionSize.set(event.getExplosion(), f_explosionSize.getFloat(event.getExplosion()) * 3.0f);
            } catch (Exception e) {
                org.canoestudios.mobsiege.MobSiege.LOGGER.error("John Cena misfired", e);
            }
        }
    }

    static {
        try {
            f_isFlaming = Explosion.class.getDeclaredField("causesFire");
            f_explosionSize = Explosion.class.getDeclaredField("size");
            f_POWERED = EntityCreeper.class.getDeclaredField("POWERED");
            MainHandler.f_modifiers.set(f_explosionSize, f_explosionSize.getModifiers() & 0xFFFFFFEF);
            f_isFlaming.setAccessible(true);
            f_explosionSize.setAccessible(true);
            f_POWERED.setAccessible(true);
        } catch (Exception e) {
            try {
                f_isFlaming = Explosion.class.getDeclaredField("causesFire");
                f_explosionSize = Explosion.class.getDeclaredField("size");
                f_POWERED = EntityCreeper.class.getDeclaredField("POWERED");
                MainHandler.f_modifiers.set(f_explosionSize, f_explosionSize.getModifiers() & 0xFFFFFFEF);
                f_isFlaming.setAccessible(true);
                f_explosionSize.setAccessible(true);
                f_POWERED.setAccessible(true);
            } catch (Exception e2) {
                org.canoestudios.mobsiege.MobSiege.LOGGER.error("Failed to set Creeper hooks", e2);
            }
        }
    }
}
