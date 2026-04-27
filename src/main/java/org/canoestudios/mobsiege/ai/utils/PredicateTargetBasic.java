package org.canoestudios.mobsiege.ai.utils;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;

public class PredicateTargetBasic<T extends EntityLivingBase> implements Predicate<T>
{
    private final Class<T> target;

    public PredicateTargetBasic(Class<T> target) {
        this.target = target;
    }

    @Override
    public boolean apply(T input) {
        return input != null && target.isAssignableFrom(input.getClass());
    }
}
