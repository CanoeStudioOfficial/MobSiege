package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAIAttackMelee;

import java.lang.reflect.Field;

public class ModifierAttackMelee implements ITaskModifier
{
    public static Field f_speed;

    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return task.getClass() == EntityAIAttackMelee.class;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        try {
            return new SiegeAIAttackMelee(host, f_speed.getDouble(entry), true);
        } catch (Exception e) {
            return new SiegeAIAttackMelee(host, 1.0, true);
        }
    }

    static {
        try {
            f_speed = EntityAIAttackMelee.class.getDeclaredField("speedTowardsTarget");
            f_speed.setAccessible(true);
        } catch (Exception e) {
            try {
                f_speed = EntityAIAttackMelee.class.getDeclaredField("speedTowardsTarget");
                f_speed.setAccessible(true);
            } catch (Exception e2) {
                // Fallback to default speed
            }
        }
    }
}
