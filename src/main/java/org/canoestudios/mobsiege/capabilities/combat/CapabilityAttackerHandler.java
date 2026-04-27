package org.canoestudios.mobsiege.capabilities.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityAttackerHandler
{
    @CapabilityInject(AttackerHandler.class)
    public static Capability<AttackerHandler> ATTACKER_HANDLER_CAPABILITY = null;
    public static ResourceLocation ATTACKER_HANDLER_ID = new ResourceLocation("mobsiege:attack_handler");

    public static void register() {
        CapabilityManager.INSTANCE.register(AttackerHandler.class, new Capability.IStorage<AttackerHandler>() {
            @Override
            public NBTBase writeNBT(Capability<AttackerHandler> capability, AttackerHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<AttackerHandler> capability, AttackerHandler instance, EnumFacing side, NBTBase nbt) {
            }
        }, () -> new AttackerHandler(null));
    }
}
