package org.canoestudios.mobsiege.capabilities.modified;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProviderModifiedHandler implements ICapabilityProvider, INBTSerializable<NBTTagCompound>
{
    private ModifiedHandler handler = new ModifiedHandler();

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return handler != null && capability == CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability != CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY) {
            return null;
        }
        return CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY.cast(handler);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return handler.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        handler.readFromNBT(tag);
    }
}
