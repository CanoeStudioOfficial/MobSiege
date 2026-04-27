package org.canoestudios.mobsiege.capabilities.combat;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProviderAttackerHandler implements ICapabilityProvider
{
    private final AttackerHandler handler;

    public ProviderAttackerHandler(EntityLiving host) {
        this.handler = new AttackerHandler(host);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY;
    }

    @Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability != CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY) {
            return null;
        }
        return CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY.cast(handler);
    }
}
