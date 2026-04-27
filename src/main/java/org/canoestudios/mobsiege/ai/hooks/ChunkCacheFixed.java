package org.canoestudios.mobsiege.ai.hooks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.TreeMap;

public class ChunkCacheFixed implements IBlockAccess
{
    protected int chunkX;
    protected int chunkZ;
    protected Chunk[][] chunkArray;
    protected boolean empty;
    protected World world;
    private static final TreeMap<Integer, TreeMap<Long, EmptyChunk>> emptyCache = new TreeMap<>();

    public ChunkCacheFixed(World worldIn, BlockPos posFromIn, BlockPos posToIn, int subIn) {
        this.world = worldIn;
        this.chunkX = posFromIn.getX() - subIn >> 4;
        this.chunkZ = posFromIn.getZ() - subIn >> 4;
        int i = posToIn.getX() + subIn >> 4;
        int j = posToIn.getZ() + subIn >> 4;
        this.chunkArray = new Chunk[i - this.chunkX + 1][j - this.chunkZ + 1];
        this.empty = true;
        for (int k = this.chunkX; k <= i; ++k) {
            for (int l = this.chunkZ; l <= j; ++l) {
                if (worldIn.isBlockLoaded(new BlockPos(k << 4, 64, l << 4), false)) {
                    this.chunkArray[k - this.chunkX][l - this.chunkZ] = worldIn.getChunk(k, l);
                    removeEmpty(k, l);
                } else {
                    this.chunkArray[k - this.chunkX][l - this.chunkZ] = getCachedEmpty(k, l);
                }
            }
        }
        for (int i2 = posFromIn.getX() >> 4; i2 <= posToIn.getX() >> 4; ++i2) {
            for (int j2 = posFromIn.getZ() >> 4; j2 <= posToIn.getZ() >> 4; ++j2) {
                Chunk chunk = this.chunkArray[i2 - this.chunkX][j2 - this.chunkZ];
                if (chunk != null && !chunk.isEmptyBetween(posFromIn.getY(), posToIn.getY())) {
                    this.empty = false;
                    return;
                }
            }
        }
    }

    private EmptyChunk getCachedEmpty(int cx, int cy) {
        long loc = ChunkPos.asLong(cx, cy);
        int dim = world.provider.getDimension();
        return emptyCache.computeIfAbsent(dim, k -> new TreeMap<>()).computeIfAbsent(loc, k -> new EmptyChunk(world, cx, cy));
    }

    private void removeEmpty(int cx, int cy) {
        long loc = ChunkPos.asLong(cx, cy);
        int dim = world.provider.getDimension();
        if (emptyCache.containsKey(dim)) {
            TreeMap<Long, EmptyChunk> dMap = emptyCache.get(dim);
            dMap.remove(loc);
            if (dMap.size() <= 0) {
                emptyCache.remove(dim);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean isEmpty() {
        return empty;
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos) {
        return getTileEntity(pos, Chunk.EnumCreateEntityType.CHECK);
    }

    @Nullable
    public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType createType) {
        int i = (pos.getX() >> 4) - chunkX;
        int j = (pos.getZ() >> 4) - chunkZ;
        if (!withinBounds(i, j)) return null;
        return chunkArray[i][j].getTileEntity(pos, createType);
    }

    public IBlockState getBlockState(BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < 256) {
            int i = (pos.getX() >> 4) - chunkX;
            int j = (pos.getZ() >> 4) - chunkZ;
            if (i >= 0 && i < chunkArray.length && j >= 0 && j < chunkArray[i].length) {
                Chunk chunk = chunkArray[i][j];
                if (chunk != null) return chunk.getBlockState(pos);
            }
        }
        return Blocks.AIR.getDefaultState();
    }

    public boolean isAirBlock(BlockPos pos) {
        IBlockState state = getBlockState(pos);
        return state.getBlock().isAir(state, this, pos);
    }

    public int getStrongPower(BlockPos pos, EnumFacing direction) {
        return getBlockState(pos).getStrongPower(this, pos, direction);
    }

    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default) {
        int x = (pos.getX() >> 4) - chunkX;
        int z = (pos.getZ() >> 4) - chunkZ;
        if (pos.getY() < 0 || pos.getY() >= 256) return _default;
        if (!withinBounds(x, z)) return _default;
        IBlockState state = getBlockState(pos);
        return state.getBlock().isSideSolid(state, this, pos, side);
    }

    private boolean withinBounds(int x, int z) {
        return x >= 0 && x < chunkArray.length && z >= 0 && z < chunkArray[x].length && chunkArray[x][z] != null;
    }

    public net.minecraft.world.WorldType getWorldType() {
        return world.getWorldType();
    }

    public net.minecraft.world.biome.Biome getBiome(BlockPos pos) {
        int i = (pos.getX() >> 4) - chunkX;
        int j = (pos.getZ() >> 4) - chunkZ;
        if (!withinBounds(i, j)) return net.minecraft.init.Biomes.PLAINS;
        return chunkArray[i][j].getBiome(pos, world.getBiomeProvider());
    }

    public int getCombinedLight(BlockPos pos, int lightValue) {
        int i = getLightFor(net.minecraft.world.EnumSkyBlock.SKY, pos);
        int j = getLightFor(net.minecraft.world.EnumSkyBlock.BLOCK, pos);
        if (j < lightValue) j = lightValue;
        return i << 20 | j << 4;
    }

    private int getLightFor(net.minecraft.world.EnumSkyBlock type, BlockPos pos) {
        if (pos.getY() < 0 || pos.getY() >= 256) return type.defaultLightValue;
        int i = (pos.getX() >> 4) - chunkX;
        int j = (pos.getZ() >> 4) - chunkZ;
        if (!withinBounds(i, j)) return type.defaultLightValue;
        return chunkArray[i][j].getLightFor(type, pos);
    }
}
