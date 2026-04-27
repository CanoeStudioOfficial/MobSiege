package org.canoestudios.mobsiege.ai.modifiers;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySkeleton;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.lang.reflect.Field;

public class ModifierBowAttack implements ITaskModifier
{
    private static Field f_moveSpeedAmp;
    private static Field f_attackCooldown;

    @Override
    public boolean isValid(EntityLiving entityLiving, EntityAIBase task) {
        return entityLiving instanceof EntitySkeleton && task.getClass() == EntityAIAttackRangedBow.class;
    }

    @Override
    public EntityAIBase getReplacement(EntityLiving host, EntityAIBase entry) {
        EntitySkeleton skeleton = (EntitySkeleton) host;
        try {
            return new EntityAIAttackRangedBow((EntityMob) skeleton, f_moveSpeedAmp.getDouble(entry), f_attackCooldown.getInt(entry), SiegeProps.RANGE_DIST.get(skeleton).floatValue());
        } catch (Exception e) {
            return new EntityAIAttackRangedBow((EntityMob) skeleton, 1.0, 20, SiegeProps.RANGE_DIST.get(skeleton).floatValue());
        }
    }

    static {
        try {
            f_moveSpeedAmp = EntityAIAttackRangedBow.class.getDeclaredField("moveSpeedAmp");
            f_attackCooldown = EntityAIAttackRangedBow.class.getDeclaredField("attackCooldown");
            f_moveSpeedAmp.setAccessible(true);
            f_attackCooldown.setAccessible(true);
        } catch (Exception e) {
            try {
                f_moveSpeedAmp = EntityAIAttackRangedBow.class.getDeclaredField("moveSpeedAmp");
                f_attackCooldown = EntityAIAttackRangedBow.class.getDeclaredField("attackCooldown");
                f_moveSpeedAmp.setAccessible(true);
                f_attackCooldown.setAccessible(true);
            } catch (Exception e2) {
                // Fallback
            }
        }
    }
}
