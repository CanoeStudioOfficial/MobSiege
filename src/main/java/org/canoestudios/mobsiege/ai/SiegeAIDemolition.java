package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;

public class SiegeAIDemolition extends EntityAIBase
{
    public EntityLiving host;
    private int delay;

    public SiegeAIDemolition(EntityLiving host) {
        this.host = host;
    }

    @Override
    public boolean shouldExecute() {
        int d = delay - 1;
        delay = d;
        if (d > 0) return false;
        delay = 0;
        boolean hasTnt = host.getHeldItemMainhand().getItem() == Item.getItemFromBlock(Blocks.TNT) || host.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.TNT);
        return hasTnt && host.getAttackTarget() != null && host.getAttackTarget().getDistance(host) < 4.0f;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return false;
    }

    @Override
    public void startExecuting() {
        delay = 200;
        EntityTNTPrimed tnt = new EntityTNTPrimed(host.world, host.posX, host.posY, host.posZ, host);
        host.world.spawnEntity(tnt);
        host.world.playSound(null, tnt.posX, tnt.posY, tnt.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }
}
