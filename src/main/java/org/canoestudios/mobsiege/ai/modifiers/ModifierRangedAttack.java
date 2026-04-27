package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.ai.SiegeAIAttackRanged;

import java.lang.reflect.Field;

public class ModifierRangedAttack implements ITaskModifier
{
    private static Field f_entityMoveSpeed;
    private static Field f_attackIntervalMin;
    private static Field f_maxRangedAttackTime;

    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return entityLiving instanceof IRangedAttackMob && task.getClass() == EntityAIAttackRanged.class;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        try {
            return new SiegeAIAttackRanged((IRangedAttackMob) host, f_entityMoveSpeed.getDouble(entry), f_attackIntervalMin.getInt(entry), f_maxRangedAttackTime.getInt(entry));
        } catch (Exception e) {
            return new SiegeAIAttackRanged((IRangedAttackMob) host, 1.0, 1, 1);
        }
    }

    static {
        try {
            f_entityMoveSpeed = EntityAIAttackRanged.class.getDeclaredField("entityMoveSpeed");
            f_attackIntervalMin = EntityAIAttackRanged.class.getDeclaredField("attackIntervalMin");
            f_maxRangedAttackTime = EntityAIAttackRanged.class.getDeclaredField("maxRangedAttackTime");
            f_entityMoveSpeed.setAccessible(true);
            f_attackIntervalMin.setAccessible(true);
            f_maxRangedAttackTime.setAccessible(true);
        } catch (Exception e) {
            try {
                f_entityMoveSpeed = EntityAIAttackRanged.class.getDeclaredField("entityMoveSpeed");
                f_attackIntervalMin = EntityAIAttackRanged.class.getDeclaredField("attackIntervalMin");
                f_maxRangedAttackTime = EntityAIAttackRanged.class.getDeclaredField("maxRangedAttackTime");
                f_entityMoveSpeed.setAccessible(true);
                f_attackIntervalMin.setAccessible(true);
                f_maxRangedAttackTime.setAccessible(true);
            } catch (Exception e2) {
                // Fallback
            }
        }
    }
}
