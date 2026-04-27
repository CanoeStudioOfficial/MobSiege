package org.canoestudios.mobsiege.ai.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntitySenses;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EntitySensesProxy extends EntitySenses
{
    private final EntityLiving entityObj;
    private final List<Entity> seenEntities = new ArrayList<>();
    private final List<Entity> unseenEntities = new ArrayList<>();

    public EntitySensesProxy(EntityLiving entityObjIn) {
        super(entityObjIn);
        this.entityObj = entityObjIn;
    }

    public void clearSensingCache() {
        seenEntities.clear();
        unseenEntities.clear();
    }

    @Override
    public boolean canSee(@Nonnull Entity entityIn) {
        if (seenEntities.contains(entityIn)) return true;
        if (unseenEntities.contains(entityIn)) return false;
        entityObj.world.profiler.startSection("canSee");
        int xray = SiegeProps.XRAY_VIEW.get(entityIn).intValue();
        boolean flag = entityIn.getDistance(entityObj) <= xray || (entityObj.getDistance(entityIn) < 128.0f && entityObj.canEntityBeSeen(entityIn));
        entityObj.world.profiler.endSection();
        if (flag) {
            seenEntities.add(entityIn);
        } else {
            unseenEntities.add(entityIn);
        }
        return flag;
    }
}
