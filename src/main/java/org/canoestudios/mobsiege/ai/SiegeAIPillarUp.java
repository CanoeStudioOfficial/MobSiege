package org.canoestudios.mobsiege.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SiegeAIPillarUp extends EntityAIBase
{
    public static ResourceLocation blockName = new ResourceLocation("minecraft:cobblestone");
    public static int blockMeta = -1;
    public static boolean updateBlock = false;
    private static IBlockState pillarBlock = Blocks.COBBLESTONE.getDefaultState();
    private static final EnumFacing[] placeSurface = {EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    private int placeDelay;
    private EntityLiving builder;
    public EntityLivingBase target;
    private BlockPos blockPos;

    public SiegeAIPillarUp(EntityLiving entity) {
        this.placeDelay = 15;
        this.builder = entity;
    }

    @Override
    public boolean shouldExecute() {
        target = builder.getAttackTarget();
        if (target == null || !target.isEntityAlive()) return false;
        if (!builder.getNavigator().noPath() || ((builder.getDistance(target.posX, builder.posY, target.posZ) >= 4.0 || !builder.onGround) && !builder.isInLava() && !builder.isInWater())) {
            return false;
        }
        BlockPos orgPos;
        BlockPos tmpPos = orgPos = builder.getPosition();
        int xOff = (int) Math.signum(MathHelper.floor(target.posX) - orgPos.getX());
        int zOff = (int) Math.signum(MathHelper.floor(target.posZ) - orgPos.getZ());
        boolean canPlace = false;
        for (EnumFacing dir : placeSurface) {
            if (builder.world.getBlockState(tmpPos.offset(dir)).isNormalCube()) {
                canPlace = true;
                break;
            }
        }
        if (target.posY - builder.posY < 16.0 && builder.world.getBlockState(tmpPos.add(0, -2, 0)).isNormalCube() && builder.world.getBlockState(tmpPos.add(0, -1, 0)).isNormalCube()) {
            if (builder.world.getBlockState(tmpPos.add(xOff, -1, 0)).getMaterial().isReplaceable()) {
                tmpPos = tmpPos.add(xOff, -1, 0);
            } else if (builder.world.getBlockState(tmpPos.add(0, -1, zOff)).getMaterial().isReplaceable()) {
                tmpPos = tmpPos.add(0, -1, zOff);
            } else if (target.posY <= builder.posY) {
                return false;
            }
        } else if (target.posY <= builder.posY) {
            return false;
        }
        if (!canPlace || builder.world.getBlockState(orgPos.add(0, 2, 0)).getMaterial().blocksMovement() || builder.world.getBlockState(tmpPos.add(0, 2, 0)).getMaterial().blocksMovement()) {
            return false;
        }
        blockPos = tmpPos;
        return true;
    }

    @Override
    public void startExecuting() {
        placeDelay = 15;
        if (updateBlock) {
            updatePillarBlock();
            updateBlock = false;
        }
    }

    @Override
    public boolean shouldContinueExecuting() {
        return shouldExecute();
    }

    @Override
    public void updateTask() {
        if (placeDelay > 0 || target == null) {
            placeDelay--;
        } else if (blockPos != null) {
            placeDelay = 15;
            builder.setPositionAndUpdate(blockPos.getX() + 0.5, blockPos.getY() + 1.0, blockPos.getZ() + 0.5);
            if (builder.world.getBlockState(blockPos).getMaterial().isReplaceable()) {
                builder.world.setBlockState(blockPos, pillarBlock);
            }
            builder.getNavigator().setPath(builder.getNavigator().getPathToEntityLiving(target), builder.getMoveHelper().getSpeed());
        }
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    private void updatePillarBlock() {
        String cfgBlockName = SiegeProps.PILLAR_BLOCK.get(builder);
        String[] cfgSplit = cfgBlockName.split(":");
        if (cfgSplit.length == 2 || cfgSplit.length == 3) {
            blockName = new ResourceLocation(cfgSplit[0], cfgSplit[1]);
            if (cfgSplit.length == 3) {
                try {
                    blockMeta = Integer.parseInt(cfgSplit[2]);
                } catch (Exception e) {
                    MobSiege.LOGGER.error("Unable to parse pillar block metadata from: " + cfgBlockName, e);
                    blockMeta = -1;
                }
            } else {
                blockMeta = -1;
            }
        } else {
            MobSiege.LOGGER.error("Incorrectly formatted pillar block config: " + cfgBlockName);
            blockName = new ResourceLocation("minecraft:cobblestone");
            blockMeta = -1;
        }
        try {
            Block b = Block.REGISTRY.getObject(blockName);
            if (b == Blocks.AIR) {
                pillarBlock = Blocks.COBBLESTONE.getDefaultState();
            } else {
                pillarBlock = blockMeta < 0 ? b.getDefaultState() : b.getStateFromMeta(blockMeta);
            }
        } catch (Exception e) {
            MobSiege.LOGGER.error("Unable to read pillaring block from config", e);
            pillarBlock = Blocks.COBBLESTONE.getDefaultState();
        }
    }
}
