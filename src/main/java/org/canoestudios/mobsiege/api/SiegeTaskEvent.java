package org.canoestudios.mobsiege.api;

import net.minecraft.entity.EntityLiving;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Event.HasResult
public abstract class SiegeTaskEvent extends Event
{
    private final EntityLiving entity;

    public SiegeTaskEvent(EntityLiving entity) {
        this.entity = entity;
    }

    public EntityLiving getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }

    @Cancelable
    public static class Addition extends SiegeTaskEvent
    {
        private final ITaskAddition addition;

        public Addition(EntityLiving entity, ITaskAddition addition) {
            super(entity);
            this.addition = addition;
        }

        public ITaskAddition getAddition() {
            return addition;
        }
    }

    @Cancelable
    public static class Modified extends SiegeTaskEvent
    {
        private final ITaskModifier modifier;

        public Modified(EntityLiving entity, ITaskModifier modifier) {
            super(entity);
            this.modifier = modifier;
        }

        public ITaskModifier getModifier() {
            return modifier;
        }
    }
}
