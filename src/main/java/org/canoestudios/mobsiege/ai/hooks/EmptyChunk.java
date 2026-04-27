package org.canoestudios.mobsiege.ai.hooks;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class EmptyChunk extends Chunk
{
    public EmptyChunk(World worldIn, int x, int z) {
        super(worldIn, x, z);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}
