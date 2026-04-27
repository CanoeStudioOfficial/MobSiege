package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.capabilities.modified.IModifiedHandler;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.util.List;

public class WitchHandler
{
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || !(event.getEntity() instanceof EntityPotion)) return;
        EntityPotion potion = (EntityPotion) event.getEntity();
        if (potion.getThrower() == null || potion.getThrower() instanceof EntityPlayer) return;
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(potion.getThrower().getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        List<String> potList = SiegeProps.POTION_TH.get(event.getEntity());
        if (potList.size() <= 0) return;
        String[] type = potList.get(potion.getThrower().getRNG().nextInt(potList.size())).split(":");
        PotionType potType = getPotion(type);
        if (potType == null || potType == PotionTypes.EMPTY) return;
        ItemStack itemPotion = new ItemStack(Items.SPLASH_POTION);
        PotionUtils.addPotionToItemStack(itemPotion, potType);
        potion.setItem(itemPotion);
        IModifiedHandler handler = event.getEntity().getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if (handler == null || handler.isModified()) return;
        handler.setModified(true);
    }

    protected static PotionType getPotion(String[] type) {
        if (type.length != 4) return PotionTypes.EMPTY;
        PotionEffect effect = null;
        try {
            Potion p = Potion.getPotionFromResourceLocation(type[0] + ":" + type[1]);
            if (p != null) {
                effect = new PotionEffect(p, Integer.parseInt(type[2]), Integer.parseInt(type[3]));
            }
        } catch (Exception e) {
            MobSiege.LOGGER.error("Unable to read potion type " + type[0] + ":" + type[1] + ":" + type[2] + ":" + type[3]);
        }
        return effect == null ? PotionTypes.EMPTY : new PotionType(effect);
    }
}
