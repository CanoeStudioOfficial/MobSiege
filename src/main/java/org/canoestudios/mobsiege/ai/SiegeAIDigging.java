package org.canoestudios.mobsiege.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.canoestudios.mobsiege.ai.utils.AiUtils;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAIDigging extends EntityAIBase
{
    private EntityLivingBase target;
    private EntityLiving digger;
    private BlockPos curBlock;
    private int scanTick;
    private int digTick;
    private BlockPos obsPos;
    private int obsTick;

    public SiegeAIDigging(EntityLiving digger) {
        this.digger = digger;
    }

    @Override
    public boolean shouldExecute() {
        target = digger.getAttackTarget();
        if (target == null || !target.isEntityAlive() || !digger.getNavigator().noPath()) return false;
        double dist = digger.getDistanceSq(target);
        double navDist = digger.getNavigator().getPathSearchRange();
        if (dist < 1.0 || dist > navDist * navDist) return false;
        if (obsPos == null) obsPos = digger.getPosition();
        if (!obsPos.equals(digger.getPosition())) {
            obsTick = 0;
            obsPos = null;
            return false;
        }
        if (++obsTick < 20) return false;
        curBlock = curBlock != null && digger.getDistanceSq(curBlock) <= 16.0 && canHarvest(digger, curBlock) ? curBlock : getNextBlock(digger, target, 2.0);
        return curBlock != null;
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        digger.getNavigator().clearPath();
        obsTick = 0;
        obsPos = null;
    }

    @Override
    public void resetTask() {
        curBlock = null;
        digTick = 0;
        obsTick = 0;
        obsPos = null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return target != null && curBlock != null && digger.getDistanceSq(curBlock) <= 16.0 && canHarvest(digger, curBlock);
    }

    @Override
    public void updateTask() {
        digger.getLookHelper().setLookPosition(target.posX, target.posY + target.getEyeHeight(), target.posZ, digger.getHorizontalFaceSpeed(), digger.getVerticalFaceSpeed());
        digger.getNavigator().clearPath();
        digTick++;
        float str = AiUtils.getBlockStrength(digger, digger.world, curBlock) * (digTick + 1.0f);
        ItemStack heldItem = digger.getHeldItem(EnumHand.MAIN_HAND);
        IBlockState state = digger.world.getBlockState(curBlock);
        if (digger.world.isAirBlock(curBlock)) {
            resetTask();
        } else if (str >= 1.0f) {
            boolean canHarvest = state.getMaterial().isToolNotRequired() || (!heldItem.isEmpty() && heldItem.canHarvestBlock(state));
            digger.world.destroyBlock(curBlock, false);
            if (canHarvest && digger.world instanceof WorldServer) {
                FakePlayer player = FakePlayerFactory.getMinecraft((WorldServer) digger.world);
                player.setHeldItem(EnumHand.MAIN_HAND, heldItem);
                player.setHeldItem(EnumHand.OFF_HAND, digger.getHeldItem(EnumHand.OFF_HAND));
                player.setPosition(digger.getPosition().getX(), digger.getPosition().getY(), digger.getPosition().getZ());
                TileEntity tile = digger.world.getTileEntity(curBlock);
                state.getBlock().harvestBlock(digger.world, player, curBlock, state, tile, heldItem);
            }
            digger.getNavigator().setPath(digger.getNavigator().getPathToEntityLiving(target), digger.getMoveHelper().getSpeed());
            resetTask();
        } else if (digTick % 5 == 0) {
            digger.world.playSound(null, curBlock, state.getBlock().getSoundType(state, digger.world, curBlock, digger).getHitSound(), SoundCategory.BLOCKS, 1.0f, 1.0f);
            digger.swingArm(EnumHand.MAIN_HAND);
            digger.world.sendBlockBreakProgress(digger.getEntityId(), curBlock, (int) (str * 10.0f));
        }
    }

    private BlockPos getNextBlock(EntityLiving entityLiving, EntityLivingBase target, double dist) {
        int digWidth = MathHelper.ceil(entityLiving.width);
        int digHeight = MathHelper.ceil(entityLiving.height);
        int passMax = digWidth * digWidth * digHeight;
        if (passMax <= 0) return null;
        int y = scanTick % digHeight;
        int x = scanTick % (digWidth * digHeight) / digHeight;
        int z = scanTick / (digWidth * digHeight);
        double rayX = x + Math.floor(entityLiving.posX) + 0.5 - digWidth / 2.0;
        double rayY = y + Math.floor(entityLiving.posY) + 0.5;
        double rayZ = z + Math.floor(entityLiving.posZ) + 0.5 - digWidth / 2.0;
        Vec3d rayOrigin = new Vec3d(rayX, rayY, rayZ);
        Vec3d rayOffset = new Vec3d(Math.floor(target.posX) + 0.5, Math.floor(target.posY) + 0.5, Math.floor(target.posZ) + 0.5);
        rayOffset = rayOffset.add(x - digWidth / 2.0, y, z - digWidth / 2.0);
        Vec3d norm = rayOffset.subtract(rayOrigin).normalize();
        if (Math.abs(norm.x) == Math.abs(norm.z) && norm.x != 0.0) {
            norm = new Vec3d(norm.x, norm.y, 0.0).normalize();
        }
        rayOffset = rayOrigin.add(norm.scale(dist));
        BlockPos p1 = entityLiving.getPosition();
        BlockPos p2 = target.getPosition();
        if (p1.getDistance(p2.getX(), p1.getY(), p2.getZ()) < 4.0) {
            if (p2.getY() - p1.getY() > 2.0) {
                rayOffset = rayOrigin.add(0.0, dist, 0.0);
            } else if (p2.getY() - p1.getY() < -2.0) {
                rayOffset = rayOrigin.add(0.0, -dist, 0.0);
            }
        }
        RayTraceResult ray = entityLiving.world.rayTraceBlocks(rayOrigin, rayOffset, false, true, false);
        scanTick = (scanTick + 1) % passMax;
        if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = ray.getBlockPos();
            IBlockState state = entityLiving.world.getBlockState(pos);
            if (canHarvest(entityLiving, pos) && SiegeProps.DIG_BL.get(entityLiving).contains(state.getBlock().getRegistryName().toString()) == SiegeProps.DIG_BL_INV.get(entityLiving)) {
                return pos;
            }
        }
        return null;
    }

    private boolean canHarvest(EntityLiving entity, BlockPos pos) {
        IBlockState state = entity.world.getBlockState(pos);
        if (!state.getMaterial().isSolid() || state.getBlockHardness(entity.world, pos) < 0.0f) return false;
        if (state.getMaterial().isToolNotRequired() || !SiegeProps.DIG_TOOLS.get(entity)) return true;
        ItemStack held = entity.getHeldItem(EnumHand.MAIN_HAND);
        return !held.isEmpty() && held.getItem().canHarvestBlock(state, held);
    }
}
