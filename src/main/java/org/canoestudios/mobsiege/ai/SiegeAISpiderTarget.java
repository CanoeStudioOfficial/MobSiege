package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.ai.EntityAIBase;

public class SiegeAISpiderTarget extends SiegeAINearestAttackableTarget
{
    private final EntitySpider spider;

    public SiegeAISpiderTarget(EntitySpider spider) {
        super(spider, true);
        this.spider = spider;
    }

    @Override
    public boolean shouldExecute() {
        float f = spider.getBrightness();
        return f < 0.5f && super.shouldExecute();
    }
}
