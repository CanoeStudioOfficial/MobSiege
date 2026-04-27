package org.canoestudios.mobsiege.capabilities.modified;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModifiedHandler implements IModifiedHandler
{
    private NBTTagCompound modTags = new NBTTagCompound();
    private boolean modified = false;

    @Override
    public boolean isModified() {
        return modified;
    }

    @Override
    public void setModified(boolean state) {
        modified = state;
    }

    @Override
    public void readFromNBT(NBTTagCompound tags) {
        modified = tags.getBoolean("modified");
        modTags = tags.getCompoundTag("data");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tags) {
        tags.setBoolean("modified", modified);
        tags.setTag("data", modTags);
        return tags;
    }

    @Override
    public NBTTagCompound getModificationData(ResourceLocation resource) {
        if (!modTags.hasKey(resource.toString(), 10)) {
            NBTTagCompound tags = new NBTTagCompound();
            modTags.setTag(resource.toString(), tags);
            return tags;
        }
        return modTags.getCompoundTag(resource.toString());
    }
}
