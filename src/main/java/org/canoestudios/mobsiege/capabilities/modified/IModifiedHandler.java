package org.canoestudios.mobsiege.capabilities.modified;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public interface IModifiedHandler
{
    boolean isModified();
    void setModified(boolean state);
    void readFromNBT(NBTTagCompound tags);
    NBTTagCompound writeToNBT(NBTTagCompound tags);
    NBTTagCompound getModificationData(ResourceLocation resource);
}
