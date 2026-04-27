package org.canoestudios.mobsiege.core.proxies;

import net.minecraftforge.common.MinecraftForge;
import org.canoestudios.mobsiege.ai.additions.*;
import org.canoestudios.mobsiege.ai.modifiers.*;
import org.canoestudios.mobsiege.api.TaskRegistry;
import org.canoestudios.mobsiege.capabilities.combat.CapabilityAttackerHandler;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.handlers.MainHandler;
import org.canoestudios.mobsiege.handlers.entities.*;

public class CommonProxy
{
    public boolean isClient() {
        return false;
    }

    public void registerHandlers() {
        CapabilityAttackerHandler.register();
        CapabilityModifiedHandler.register();
        MinecraftForge.EVENT_BUS.register(new MainHandler());
        MinecraftForge.EVENT_BUS.register(new CreeperHandler());
        MinecraftForge.EVENT_BUS.register(new SkeletonHandler());
        MinecraftForge.EVENT_BUS.register(new WitchHandler());
        MinecraftForge.EVENT_BUS.register(new SpiderHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerHandler());
        MinecraftForge.EVENT_BUS.register(new ZombieHandler());
        MinecraftForge.EVENT_BUS.register(new EndermanHandler());
        MinecraftForge.EVENT_BUS.register(new GeneralEntityHandler());

        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierSwimming());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierNearestAttackable());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierNoPanic());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierCreeperSwell());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierVillagerAvoid());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierAttackMelee());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierZombieAttack());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierRangedAttack());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierBowAttack());
        TaskRegistry.INSTANCE.registerTaskModifier(new ModifierWander());

        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionAnimalRetaliate());
        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionAnimalAttack());
        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionAvoidExplosives());
        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionDigger());
        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionDemolition());
        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionPillaring());
        TaskRegistry.INSTANCE.registerTaskAddition(new AdditionGrief());
    }

    public void registerRenderers() {
    }
}
