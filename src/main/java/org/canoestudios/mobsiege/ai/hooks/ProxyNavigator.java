package org.canoestudios.mobsiege.ai.hooks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.canoestudios.mobsiege.MobSiege;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public class ProxyNavigator extends PathNavigateGround
{
    private static Field f_targetPos;
    private static Field f_pathFinder;

    public ProxyNavigator(EntityLiving entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    @Nullable
    @Override
    public Path getPathToPos(BlockPos pos) {
        if (!canNavigate()) return null;
        if (currentPath != null && !currentPath.isFinished() && pos.equals(getTargetPos())) {
            return currentPath;
        }
        setTargetPos(pos);
        float f = (float) Math.min(entity.getDistance(pos.getX(), pos.getY(), pos.getZ()) + 32.0, getPathSearchRange());
        world.profiler.startSection("pathfind");
        BlockPos blockpos = new BlockPos(entity);
        BlockPos bpMin = new BlockPos(Math.min(blockpos.getX(), pos.getX()) - 32, Math.min(blockpos.getY(), pos.getY()) - 32, Math.min(blockpos.getZ(), pos.getZ()) - 32);
        BlockPos bpMax = new BlockPos(Math.max(blockpos.getX(), pos.getX()) + 32, Math.max(blockpos.getY(), pos.getY()) + 32, Math.max(blockpos.getZ(), pos.getZ()) + 32);
        ChunkCacheFixed chunkcache = new ChunkCacheFixed(world, bpMin, bpMax, 0);
        Path path = getOldPathFinder().findPath(chunkcache, entity, pos, f);
        world.profiler.endSection();
        return path;
    }

    @Nullable
    @Override
    public Path getPathToEntityLiving(Entity entityIn) {
        if (!canNavigate()) return null;
        BlockPos blockpos = new BlockPos(entityIn);
        if (currentPath != null && !currentPath.isFinished() && blockpos.equals(getTargetPos())) {
            return currentPath;
        }
        setTargetPos(blockpos);
        float f = Math.min(entity.getDistance(entityIn) + 32.0f, getPathSearchRange());
        world.profiler.startSection("pathfind");
        BlockPos blockpos2 = new BlockPos(entity).up();
        BlockPos bpMin = new BlockPos(Math.min(blockpos.getX(), blockpos2.getX()) - 32, Math.min(blockpos.getY(), blockpos2.getY()) - 32, Math.min(blockpos.getZ(), blockpos2.getZ()) - 32);
        BlockPos bpMax = new BlockPos(Math.max(blockpos.getX(), blockpos2.getX()) + 32, Math.max(blockpos.getY(), blockpos2.getY()) + 32, Math.max(blockpos.getZ(), blockpos2.getZ()) + 32);
        ChunkCacheFixed chunkcache = new ChunkCacheFixed(world, bpMin, bpMax, 0);
        Path path = getOldPathFinder().findPath(chunkcache, entity, entityIn, f);
        world.profiler.endSection();
        return path;
    }

    private BlockPos getTargetPos() {
        if (f_targetPos == null) return null;
        try {
            return (BlockPos) f_targetPos.get(this);
        } catch (IllegalAccessException e) {
            MobSiege.LOGGER.error(e);
            return null;
        }
    }

    private void setTargetPos(BlockPos value) {
        if (f_targetPos == null) return;
        try {
            f_targetPos.set(this, value);
        } catch (IllegalAccessException e) {
            MobSiege.LOGGER.error(e);
        }
    }

    private PathFinder getOldPathFinder() {
        if (f_pathFinder == null) return getPathFinder();
        try {
            return (PathFinder) f_pathFinder.get(this);
        } catch (IllegalAccessException e) {
            MobSiege.LOGGER.error(e);
            return getPathFinder();
        }
    }

    static {
        try {
            f_targetPos = PathNavigate.class.getDeclaredField("targetPos");
            f_targetPos.setAccessible(true);
            f_pathFinder = PathNavigate.class.getDeclaredField("pathFinder");
            f_pathFinder.setAccessible(true);
        } catch (Exception e) {
            try {
                f_targetPos = PathNavigate.class.getDeclaredField("targetPos");
                f_targetPos.setAccessible(true);
                f_pathFinder = PathNavigate.class.getDeclaredField("pathFinder");
                f_pathFinder.setAccessible(true);
            } catch (Exception e2) {
                MobSiege.LOGGER.error("Unable to hook navigation variables", e);
            }
        }
    }
}
