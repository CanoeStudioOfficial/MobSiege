package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.capabilities.modified.IModifiedHandler;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.util.Collection;
import java.util.List;

public class SkeletonHandler
{
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (!(event.getEntity() instanceof EntitySkeleton) && !(event.getEntity() instanceof EntityTippedArrow)) return;
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(event.getEntity().getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        IModifiedHandler handler = event.getEntity().getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if (handler == null || handler.isModified()) return;
        handler.setModified(true);
        if (event.getEntity() instanceof EntitySkeleton && event.getWorld().provider.getDimension() != -1) {
            EntitySkeleton skeleton = (EntitySkeleton) event.getEntity();
            if (skeleton.getRNG().nextDouble() < SiegeProps.WITHER_SKEL.get(skeleton).doubleValue()) {
                event.setCanceled(true);
                skeleton.setDead();
                EntityWitherSkeleton wSkel = new EntityWitherSkeleton(event.getWorld());
                wSkel.setPosition(skeleton.posX, skeleton.posY, skeleton.posZ);
                event.getWorld().spawnEntity(wSkel);
            }
        } else if (event.getEntity() instanceof EntityTippedArrow) {
            EntityTippedArrow arrow = (EntityTippedArrow) event.getEntity();
            if (arrow.shootingEntity instanceof EntityLiving) {
                EntityLiving shooter = (EntityLiving) arrow.shootingEntity;
                EntityLivingBase target = shooter.getAttackTarget();
                if (target != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    arrow.writeEntityToNBT(tag);
                    PotionType pType = tag.hasKey("Potion", 8) ? PotionType.getPotionTypeForName(tag.getString("Potion")) : PotionTypes.EMPTY;
                    if ((pType == null || pType == PotionTypes.EMPTY) && shooter.getRNG().nextFloat() < 0.3f) {
                        List<String> potList = SiegeProps.ARROW_POT.get(shooter);
                        if (potList.size() > 0) {
                            pType = WitchHandler.getPotion(potList.get(shooter.getRNG().nextInt(potList.size())).split(":"));
                        }
                    }
                    replaceArrowAttack(shooter, target, arrow.getDamage(), pType);
                    arrow.setDead();
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void replaceArrowAttack(EntityLiving shooter, EntityLivingBase targetEntity, double damage, PotionType potions) {
        EntityTippedArrow entityarrow = new EntityTippedArrow(shooter.world, shooter);
        if (potions != null && potions != PotionTypes.EMPTY) {
            ItemStack itemTip = new ItemStack(Items.TIPPED_ARROW);
            PotionUtils.addPotionToItemStack(itemTip, potions);
            PotionUtils.appendEffects(itemTip, (Collection) potions.getEffects());
            entityarrow.setPotionEffect(itemTip);
        }
        double targetDist = shooter.getDistance(targetEntity.posX + (targetEntity.posX - targetEntity.lastTickPosX), targetEntity.getEntityBoundingBox().minY, targetEntity.posZ + (targetEntity.posZ - targetEntity.lastTickPosZ));
        float fireSpeed = (float) (1.3E-4 * targetDist * targetDist + 0.02 * targetDist + 1.25);
        double d0 = targetEntity.posX + (targetEntity.posX - targetEntity.lastTickPosX) * (targetDist / fireSpeed) - shooter.posX;
        double d2 = targetEntity.getEntityBoundingBox().minY + targetEntity.height / 3.0f - entityarrow.posY;
        double d3 = targetEntity.posZ + (targetEntity.posZ - targetEntity.lastTickPosZ) * (targetDist / fireSpeed) - shooter.posZ;
        double d4 = MathHelper.sqrt(d0 * d0 + d3 * d3);
        if (d4 >= 1.0E-7) {
            float f4 = (float) d4 * 0.2f;
            entityarrow.shoot(d0, d2 + f4, d3, fireSpeed, SiegeProps.RANGE_ERR.get(shooter).floatValue());
        }
        int pow = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, shooter.getHeldItem(EnumHand.MAIN_HAND));
        int pun = net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, shooter.getHeldItem(EnumHand.MAIN_HAND));
        entityarrow.setDamage(damage);
        if (pow > 0) entityarrow.setDamage(entityarrow.getDamage() + pow * 0.5 + 0.5);
        if (pun > 0) entityarrow.setKnockbackStrength(pun);
        if (shooter.isBurning() || net.minecraft.enchantment.EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, shooter.getHeldItem(EnumHand.MAIN_HAND)) > 0 || shooter instanceof EntityWitherSkeleton) {
            entityarrow.setFire(100);
        }
        shooter.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0f, 1.0f / (shooter.getRNG().nextFloat() * 0.4f + 0.8f));
        IModifiedHandler modHandler = entityarrow.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if (modHandler != null) modHandler.setModified(true);
        shooter.world.spawnEntity(entityarrow);
    }
}
