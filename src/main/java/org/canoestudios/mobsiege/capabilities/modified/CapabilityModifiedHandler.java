package org.canoestudios.mobsiege.capabilities.modified;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.concurrent.Callable;

public class CapabilityModifiedHandler
{
    @CapabilityInject(IModifiedHandler.class)
    public static Capability<IModifiedHandler> MODIFIED_HANDLER_CAPABILITY = null;
    public static ResourceLocation MODIFIED_HANDLER_ID = new ResourceLocation("mobsiege:modified_handler");

    public static void register() {
        CapabilityManager.INSTANCE.register(IModifiedHandler.class, new Capability.IStorage<IModifiedHandler>() {
            @Override
            public NBTBase writeNBT(Capability<IModifiedHandler> capability, IModifiedHandler instance, EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IModifiedHandler> capability, IModifiedHandler instance, EnumFacing side, NBTBase nbt) {
            }
        }, ModifiedHandler::new);
    }
}
