package org.canoestudios.mobsiege.ai.utils;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;

public class PredicateExplosives implements Predicate<Entity>
{
    private final Entity host;

    public PredicateExplosives(Entity host) {
        this.host = host;
    }

    @Override
    public boolean apply(Entity input) {
        if (input == host) return false;
        if (input instanceof EntityCreeper) {
            return ((EntityCreeper) input).getCreeperState() > 0;
        }
        return input instanceof EntityTNTPrimed;
    }
}
