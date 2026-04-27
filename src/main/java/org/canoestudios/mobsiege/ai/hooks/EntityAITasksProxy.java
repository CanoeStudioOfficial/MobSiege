package org.canoestudios.mobsiege.ai.hooks;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraftforge.common.MinecraftForge;
import org.canoestudios.mobsiege.api.ITaskModifier;
import org.canoestudios.mobsiege.api.SiegeTaskEvent;
import org.canoestudios.mobsiege.api.TaskRegistry;

public class EntityAITasksProxy extends EntityAITasks
{
    private final EntityLiving host;

    public EntityAITasksProxy(EntityLiving host, EntityAITasks original) {
        super(host.world == null ? null : host.world.profiler);
        this.host = host;
        for (EntityAITasks.EntityAITaskEntry entry : original.taskEntries) {
            addTask(entry.priority, entry.action);
        }
    }

    @Override
    public void addTask(int priority, EntityAIBase task) {
        for (ITaskModifier mod : TaskRegistry.INSTANCE.getAllModifiers()) {
            if (mod.isValid(host, task)) {
                EntityAIBase ai = mod.getReplacement(host, task);
                if (ai != null) {
                    SiegeTaskEvent event = new SiegeTaskEvent.Modified(host, mod);
                    MinecraftForge.EVENT_BUS.post(event);
                    if (event.getResult() != net.minecraftforge.fml.common.eventhandler.Event.Result.DENY) {
                        super.addTask(priority, ai);
                    }
                }
                return;
            }
        }
        super.addTask(priority, task);
    }
}
