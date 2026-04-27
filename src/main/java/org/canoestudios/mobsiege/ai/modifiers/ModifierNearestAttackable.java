package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAINearestAttackableTarget;
import org.canoestudios.mobsiege.ai.SiegeAISpiderTarget;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.lang.reflect.Field;

public class ModifierNearestAttackable implements ITaskModifier
{
    private static Field f_targetClass;
    private static Field f_targetChance;
    private static Field f_shouldCheckSight;
    private static Field f_nearbyOnly;

    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return (entityLiving instanceof EntitySpider && task instanceof EntityAINearestAttackableTarget) || (task != null && task.getClass() == EntityAINearestAttackableTarget.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase task) {
        if (SiegeProps.PASSIVE.get(host)) return null;
        boolean hasExisting = false;
        SiegeAINearestAttackableTarget ai = null;
        for (net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry t : host.targetTasks.taskEntries) {
            if (t.action instanceof SiegeAINearestAttackableTarget) {
                ai = (SiegeAINearestAttackableTarget) t.action;
                hasExisting = true;
                break;
            }
        }
        try {
            Class<? extends EntityLivingBase> tarClass = (Class<? extends EntityLivingBase>) f_targetClass.get(task);
            int tarChance = f_targetChance.getInt(task);
            boolean sight = f_shouldCheckSight.getBoolean(task);
            boolean nearby = f_nearbyOnly.getBoolean(task);
            boolean atkAll = SiegeProps.ATK_ALL.get(host);
            if (ai == null) {
                if (host instanceof EntitySpider) {
                    ai = new SiegeAISpiderTarget((EntitySpider) host);
                } else {
                    ai = new SiegeAINearestAttackableTarget(host, tarChance, sight, nearby, null);
                }
                if (atkAll) {
                    ai.addTarget(EntityLivingBase.class);
                }
            }
            if (!atkAll) {
                ai.addTarget(tarClass);
                if (SiegeProps.ATK_VILLAGER.get(host) && EntityPlayer.class.isAssignableFrom(tarClass)) {
                    ai.addTarget(EntityVillager.class);
                }
            }
        } catch (Exception e) {
            MobSiege.LOGGER.error("Hook failed 'NearestAttackableTarget':", e);
        }
        return hasExisting ? null : ai;
    }

    static {
        try {
            f_targetClass = EntityAINearestAttackableTarget.class.getDeclaredField("targetClass");
            f_targetChance = EntityAINearestAttackableTarget.class.getDeclaredField("targetChance");
            f_targetClass.setAccessible(true);
            f_targetChance.setAccessible(true);
            f_shouldCheckSight = EntityAITarget.class.getDeclaredField("shouldCheckSight");
            f_nearbyOnly = EntityAITarget.class.getDeclaredField("nearbyOnly");
            f_shouldCheckSight.setAccessible(true);
            f_nearbyOnly.setAccessible(true);
        } catch (Exception e) {
            try {
                f_targetClass = EntityAINearestAttackableTarget.class.getDeclaredField("targetClass");
                f_targetChance = EntityAINearestAttackableTarget.class.getDeclaredField("targetChance");
                f_targetClass.setAccessible(true);
                f_targetChance.setAccessible(true);
                f_shouldCheckSight = EntityAITarget.class.getDeclaredField("shouldCheckSight");
                f_nearbyOnly = EntityAITarget.class.getDeclaredField("nearbyOnly");
                f_shouldCheckSight.setAccessible(true);
                f_nearbyOnly.setAccessible(true);
            } catch (Exception e2) {
                MobSiege.LOGGER.error("Unable to enable access to AI targeting variables", e);
            }
        }
    }
}
