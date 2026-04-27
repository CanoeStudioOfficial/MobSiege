package org.canoestudios.mobsiege.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.apache.logging.log4j.Level;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.api.ITaskAddition;
import org.canoestudios.mobsiege.api.SiegeTaskEvent;
import org.canoestudios.mobsiege.ai.hooks.EntityAITasksProxy;
import org.canoestudios.mobsiege.ai.hooks.EntitySensesProxy;
import org.canoestudios.mobsiege.ai.hooks.ProxyNavigator;
import org.canoestudios.mobsiege.api.TaskRegistry;
import org.canoestudios.mobsiege.capabilities.combat.AttackerHandler;
import org.canoestudios.mobsiege.capabilities.combat.CapabilityAttackerHandler;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.capabilities.modified.ProviderModifiedHandler;
import org.canoestudios.mobsiege.capabilities.combat.ProviderAttackerHandler;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.lang.reflect.Field;
import java.util.List;

public class MainHandler
{
    private static boolean hooksReady = false;
    public static Field f_modifiers;
    private static Field f_tasks;
    private static Field f_targetTasks;
    private static Field f_senses;
    private static Field f_navigator;

    @SubscribeEvent
    public void onEntityConstruct(EntityJoinWorldEvent event) {
        if (!hooksReady) return;
        if (!(event.getEntity() instanceof EntityLiving)) return;
        EntityLiving entityLiving = (EntityLiving) event.getEntity();
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entityLiving.getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        try {
            f_tasks.set(entityLiving, new EntityAITasksProxy(entityLiving, entityLiving.tasks));
            f_senses.set(entityLiving, new EntitySensesProxy(entityLiving));
            f_targetTasks.set(entityLiving, new EntityAITasksProxy(entityLiving, entityLiving.targetTasks));
            if (entityLiving.getNavigator().getClass() == PathNavigateGround.class) {
                f_navigator.set(entityLiving, new ProxyNavigator(entityLiving, entityLiving.world));
            }
        } catch (Exception e) {
            MobSiege.LOGGER.log(Level.ERROR, "Unable to set AI hooks in " + entityLiving.getName(), e);
            return;
        }
        for (ITaskAddition add : TaskRegistry.INSTANCE.getAllAdditions()) {
            if (!add.isValid(entityLiving)) continue;
            EntityAIBase entry = add.getAdditionalAI(entityLiving);
            if (entry == null) continue;
            SiegeTaskEvent taskEvent = new SiegeTaskEvent.Addition(entityLiving, add);
            if (MinecraftForge.EVENT_BUS.post(taskEvent)) continue;
            if (add.isTargetTask()) {
                entityLiving.targetTasks.addTask(add.getTaskPriority(entityLiving), entry);
            } else {
                entityLiving.tasks.addTask(add.getTaskPriority(entityLiving), entry);
            }
        }
        IAttributeInstance att = entityLiving.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE);
        if (att.getBaseValue() < SiegeProps.AWARENESS.get(entityLiving).doubleValue()) {
            att.setBaseValue(SiegeProps.AWARENESS.get(entityLiving).doubleValue());
        }
    }

    @SubscribeEvent
    public void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLiving) {
            event.addCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_ID, new ProviderAttackerHandler((EntityLiving) event.getObject()));
            event.addCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_ID, new ProviderModifiedHandler());
        } else if (event.getObject().getClass() == EntityTippedArrow.class || event.getObject().getClass() == EntityPotion.class) {
            event.addCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_ID, new ProviderModifiedHandler());
        }
    }

    @SubscribeEvent
    public void onTargetSet(LivingSetAttackTargetEvent event) {
        if (event.getTarget() == null || !(event.getEntityLiving() instanceof EntityLiving)) return;
        if (SiegeProps.DEBUG_TARGET.get(event.getEntityLiving())) {
            net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(event.getEntityLiving().getClass());
            MobSiege.LOGGER.info("Entity " + (ee == null ? event.getEntityLiving().getClass().getName() : ee.getRegistryName()) + " targeted " + event.getTarget().getName());
        }
        AttackerHandler atkHandle = event.getTarget().getCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null);
        if (atkHandle != null) {
            atkHandle.addAttacker((EntityLiving) event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving().world.isRemote || event.getEntityLiving().ticksExisted % 20 != 0) return;
        AttackerHandler atkHandle = event.getEntityLiving().getCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null);
        if (atkHandle != null) {
            atkHandle.updateAttackers();
        }
    }

    static {
        try {
            f_modifiers = Field.class.getDeclaredField("modifiers");
            f_modifiers.setAccessible(true);
        } catch (Exception e) {
            MobSiege.LOGGER.log(Level.ERROR, "Unable to enable write access to variable modifiers", e);
        }
        try {
            f_tasks = EntityLiving.class.getDeclaredField("tasks");
            f_targetTasks = EntityLiving.class.getDeclaredField("targetTasks");
            f_senses = EntityLiving.class.getDeclaredField("senses");
            f_navigator = EntityLiving.class.getDeclaredField("navigator");
            f_modifiers.set(f_tasks, f_tasks.getModifiers() & 0xFFFFFFEF);
            f_modifiers.set(f_targetTasks, f_targetTasks.getModifiers() & 0xFFFFFFEF);
            f_tasks.setAccessible(true);
            f_targetTasks.setAccessible(true);
            f_senses.setAccessible(true);
            f_navigator.setAccessible(true);
            hooksReady = true;
        } catch (Exception e) {
            try {
                f_tasks = EntityLiving.class.getDeclaredField("tasks");
                f_targetTasks = EntityLiving.class.getDeclaredField("targetTasks");
                f_senses = EntityLiving.class.getDeclaredField("senses");
                f_navigator = EntityLiving.class.getDeclaredField("navigator");
                f_modifiers.set(f_tasks, f_tasks.getModifiers() & 0xFFFFFFEF);
                f_modifiers.set(f_targetTasks, f_targetTasks.getModifiers() & 0xFFFFFFEF);
                f_tasks.setAccessible(true);
                f_targetTasks.setAccessible(true);
                f_senses.setAccessible(true);
                f_navigator.setAccessible(true);
                hooksReady = true;
            } catch (Exception e2) {
                MobSiege.LOGGER.log(Level.ERROR, "Unable to enable write access to AI. Hooks disabled!", e);
            }
        }
    }
}
