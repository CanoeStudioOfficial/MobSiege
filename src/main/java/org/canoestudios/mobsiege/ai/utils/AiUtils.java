package org.canoestudios.mobsiege.ai.utils;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AiUtils
{
    public static float getBreakSpeed(EntityLivingBase entity, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        ItemStack heldItem = entity.getHeldItem(EnumHand.MAIN_HAND);
        float f = heldItem.isEmpty() ? 1.0f : heldItem.getDestroySpeed(state);
        if (f > 1.0f) {
            int i = EnchantmentHelper.getEfficiencyModifier(entity);
            if (i > 0 && !heldItem.isEmpty()) {
                f += i * i + 1;
            }
        }
        if (entity.isPotionActive(MobEffects.HASTE)) {
            f *= 1.0f + (entity.getActivePotionEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2f;
        }
        if (entity.isPotionActive(MobEffects.MINING_FATIGUE)) {
            float f2;
            switch (entity.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: f2 = 0.3f; break;
                case 1: f2 = 0.09f; break;
                case 2: f2 = 0.0027f; break;
                default: f2 = 8.1E-4f; break;
            }
            f *= f2;
        }
        if (entity.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(entity)) {
            f /= 5.0f;
        }
        if (!entity.onGround) {
            f /= 5.0f;
        }
        return f < 0.0f ? 0.0f : f;
    }

    public static float getBlockStrength(EntityLivingBase entity, World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        float hardness = state.getBlockHardness(world, pos);
        if (hardness <= 0.0f) return 0.0f;
        ItemStack heldItem = entity.getHeldItem(EnumHand.MAIN_HAND);
        boolean canHarvest = state.getMaterial().isToolNotRequired() || (!heldItem.isEmpty() && heldItem.canHarvestBlock(state));
        return getBreakSpeed(entity, world, pos) / hardness / (canHarvest ? 30.0f : 100.0f);
    }
}
