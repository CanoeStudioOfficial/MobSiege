package org.canoestudios.mobsiege.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.canoestudios.mobsiege.ai.utils.AiUtils;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAIGrief extends EntityAIBase
{
    private EntityLiving entityLiving;
    private BlockPos markedLoc;
    private int digTick;

    public SiegeAIGrief(EntityLiving entity) {
        this.entityLiving = entity;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (entityLiving.getRNG().nextInt(4) != 0) return false;
        BlockPos curPos = entityLiving.getPosition();
        ItemStack item = entityLiving.getHeldItemMainhand();
        BlockPos tarPos = curPos.add(entityLiving.getRNG().nextInt(32) - 16, entityLiving.getRNG().nextInt(16) - 8, entityLiving.getRNG().nextInt(32) - 16);
        IBlockState state = entityLiving.world.getBlockState(tarPos);
        ResourceLocation regName = Block.REGISTRY.getNameForObject(state.getBlock());
        if ((SiegeProps.GRIEF_BLOCKS.get(entityLiving).contains(regName.toString()) || state.getLightValue(entityLiving.world, tarPos) > 0) && state.getBlockHardness(entityLiving.world, tarPos) >= 0.0f && !state.getMaterial().isLiquid() && (!SiegeProps.DIG_TOOLS.get(entityLiving) || (!item.isEmpty() && item.getItem().canHarvestBlock(state, item)) || state.getMaterial().isToolNotRequired())) {
            markedLoc = tarPos;
            entityLiving.getNavigator().tryMoveToXYZ(markedLoc.getX(), markedLoc.getY(), markedLoc.getZ(), 1.0);
            digTick = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting() {
        if (markedLoc == null || !entityLiving.isEntityAlive()) {
            markedLoc = null;
            return false;
        }
        IBlockState state = entityLiving.world.getBlockState(markedLoc);
        ResourceLocation regName = Block.REGISTRY.getNameForObject(state.getBlock());
        if (state.getBlock() == Blocks.AIR || (!SiegeProps.GRIEF_BLOCKS.get(entityLiving).contains(regName.toString()) && state.getLightValue(entityLiving.world, markedLoc) <= 0)) {
            markedLoc = null;
            return false;
        }
        ItemStack item = entityLiving.getHeldItemMainhand();
        return !SiegeProps.DIG_TOOLS.get(entityLiving) || (!item.isEmpty() && item.getItem().canHarvestBlock(state, item)) || state.getMaterial().isToolNotRequired();
    }

    @Override
    public void updateTask() {
        if (!shouldContinueExecuting()) {
            digTick = 0;
            return;
        }
        if (entityLiving.getDistance(markedLoc.getX(), markedLoc.getY(), markedLoc.getZ()) >= 3.0) {
            if (entityLiving.getNavigator().noPath()) {
                entityLiving.getNavigator().tryMoveToXYZ(markedLoc.getX(), markedLoc.getY(), markedLoc.getZ(), 1.0);
            }
            digTick = 0;
            return;
        }
        IBlockState state = entityLiving.world.getBlockState(markedLoc);
        digTick++;
        float str = AiUtils.getBlockStrength(entityLiving, entityLiving.world, markedLoc) * (digTick + 1);
        if (str >= 1.0f) {
            digTick = 0;
            if (markedLoc != null) {
                ItemStack item = entityLiving.getHeldItemMainhand();
                boolean canHarvest = state.getMaterial().isToolNotRequired() || (!item.isEmpty() && item.getItem().canHarvestBlock(state, item));
                entityLiving.world.destroyBlock(markedLoc, canHarvest);
                markedLoc = null;
            }
        } else if (digTick % 5 == 0) {
            net.minecraft.block.SoundType sndType = state.getBlock().getSoundType(state, entityLiving.world, markedLoc, entityLiving);
            entityLiving.playSound(sndType.getHitSound(), sndType.volume, sndType.pitch);
            entityLiving.swingArm(EnumHand.MAIN_HAND);
        }
    }
}
