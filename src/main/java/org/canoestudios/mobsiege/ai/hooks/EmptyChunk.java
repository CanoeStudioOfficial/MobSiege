package org.canoestudios.mobsiege.ai.hooks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class EmptyChunk extends Chunk
{
    public EmptyChunk(World worldIn, int x, int z) {
        super(worldIn, x, z);
    }

    @Override
    public boolean isAtLocation(int x, int z) {
        return x == this.x && z == this.z;
    }

    @Override
    public int getHeightValue(int x, int z) {
        return 0;
    }

    @Override
    public void generateHeightMap() {
    }

    @Override
    public void generateSkylightMap() {
    }

    @Override
    public IBlockState getBlockState(BlockPos pos) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public int getBlockLightOpacity(BlockPos pos) {
        return 255;
    }

    @Override
    public int getLightFor(net.minecraft.world.EnumSkyBlock type, BlockPos pos) {
        return type.defaultLightValue;
    }

    @Override
    public void setLightFor(net.minecraft.world.EnumSkyBlock type, BlockPos pos, int value) {
    }

    @Override
    public int getLightSubtracted(BlockPos pos, int amount) {
        return 0;
    }

    @Override
    public void addEntity(Entity entityIn) {
    }

    @Override
    public void removeEntity(Entity entityIn) {
    }

    @Override
    public void removeEntityAtIndex(Entity entityIn, int index) {
    }

    @Override
    public boolean canSeeSky(BlockPos pos) {
        return false;
    }

    @Nullable
    @Override
    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType creationMode) {
        return null;
    }

    @Override
    public void addTileEntity(TileEntity tileEntityIn) {
    }

    @Override
    public void addTileEntity(BlockPos pos, TileEntity tileEntityIn) {
    }

    @Override
    public void removeTileEntity(BlockPos pos) {
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onUnload() {
    }

    @Override
    public void markDirty() {
    }

    public void getEntitiesWithinAABBForEntity(@Nullable Entity entityIn, AxisAlignedBB aabb, List<Entity> listToFill, @Nullable Predicate<? super Entity> filter) {
    }

    public <T extends Entity> void getEntitiesOfTypeWithinAABB(Class<? extends T> entityClass, AxisAlignedBB aabb, List<T> listToFill, @Nullable Predicate<? super T> filter) {
    }

    @Override
    public boolean needsSaving(boolean p_76601_1_) {
        return false;
    }

    @Override
    public Random getRandomWithSeed(long seed) {
        return new Random(this.getWorld().getSeed() + this.x * this.x * 4987142L + this.x * 5947611L + this.z * this.z * 4392871L + this.z * 389711L ^ seed);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isEmptyBetween(int startY, int endY) {
        return true;
    }
}
