package org.canoestudios.mobsiege.api;

import java.util.ArrayList;
import java.util.List;

public final class TaskRegistry
{
    public static final TaskRegistry INSTANCE = new TaskRegistry();
    private List<ITaskAddition> additions = new ArrayList<>();
    private List<ITaskModifier> modifiers = new ArrayList<>();

    public void registerTaskModifier(ITaskModifier mod) {
        if (mod != null && !modifiers.contains(mod)) {
            modifiers.add(mod);
        }
    }

    public void registerTaskAddition(ITaskAddition add) {
        if (add != null && !additions.contains(add)) {
            additions.add(add);
        }
    }

    public List<ITaskModifier> getAllModifiers() {
        return modifiers;
    }

    public List<ITaskAddition> getAllAdditions() {
        return additions;
    }
}
