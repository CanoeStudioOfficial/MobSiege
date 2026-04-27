package org.canoestudios.mobsiege.ai.utils;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.canoestudios.mobsiege.MobSiege;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;

public class CreeperHooks
{
    private static final DataParameter<Boolean> POWERED = ReflectionHelper.getPrivateValue(EntityCreeper.class, null, "POWERED");
    private static Field blastSize;
    private final EntityCreeper creeper;

    public CreeperHooks(EntityCreeper creeper) {
        this.creeper = creeper;
    }

    public EntityCreeper getCreeper() {
        return creeper;
    }

    public boolean isPowered() {
        return creeper.getDataManager().get(POWERED);
    }

    public void setPowered(boolean state) {
        creeper.getDataManager().set(POWERED, state);
    }

    public int getExplosionSize() {
        try {
            return blastSize.getInt(creeper);
        } catch (Exception e) {
            MobSiege.LOGGER.log(Level.ERROR, "Unable to get creeper blast radius", e);
            return 3;
        }
    }

    public void setExplosionSize(int value) {
        try {
            blastSize.setInt(creeper, value);
        } catch (Exception e) {
            MobSiege.LOGGER.log(Level.ERROR, "Unable to set creeper blast radius", e);
        }
    }

    static {
        try {
            blastSize = EntityCreeper.class.getDeclaredField("explosionRadius");
            blastSize.setAccessible(true);
        } catch (Exception e) {
            try {
                blastSize = EntityCreeper.class.getDeclaredField("explosionRadius");
                blastSize.setAccessible(true);
            } catch (Exception e2) {
                MobSiege.LOGGER.log(Level.ERROR, "Unable to set Creeper hooks");
            }
        }
    }
}
