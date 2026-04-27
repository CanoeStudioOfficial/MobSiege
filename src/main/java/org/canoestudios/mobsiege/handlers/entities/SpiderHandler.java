package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

public class SpiderHandler
{
    @SubscribeEvent
    public void onAttacked(LivingHurtEvent event) {
        if (event.getEntity().world.isRemote || event.getSource() == null || !(event.getSource().getTrueSource() instanceof EntitySpider)) return;
        EntitySpider spider = (EntitySpider) event.getSource().getTrueSource();
        EntityLivingBase target = event.getEntityLiving();
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(spider.getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        if (spider.getRNG().nextDouble() < SiegeProps.SP_WEB.get(spider).doubleValue() && spider.world.getBlockState(target.getPosition()).getMaterial().isReplaceable()) {
            spider.world.setBlockState(target.getPosition(), Blocks.WEB.getDefaultState());
        }
    }
}
