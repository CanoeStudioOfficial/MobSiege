package org.canoestudios.mobsiege.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import org.canoestudios.mobsiege.ai.utils.PredicateTargetBasic;
import org.canoestudios.mobsiege.capabilities.combat.AttackerHandler;
import org.canoestudios.mobsiege.capabilities.combat.CapabilityAttackerHandler;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class SiegeAINearestAttackableTarget extends SiegeAITarget
{
    private EntityLiving taskOwner;
    private final List<Predicate<EntityLivingBase>> targetChecks = new ArrayList<>();
    private final int targetChance;
    private final Comparator<EntityLivingBase> theNearestAttackableTargetSorter;
    private Predicate<? super EntityLivingBase> targetEntitySelector;
    private EntityLivingBase targetEntity;

    public SiegeAINearestAttackableTarget(EntityLiving host, boolean checkSight) {
        this(host, checkSight, false);
    }

    public SiegeAINearestAttackableTarget(EntityLiving host, boolean checkSight, boolean onlyNearby) {
        this(host, 10, checkSight, onlyNearby, null);
    }

    public SiegeAINearestAttackableTarget(EntityLiving host, int chance, boolean checkSight, boolean onlyNearby, Predicate<? super EntityLivingBase> targetSelector) {
        super(host, checkSight, onlyNearby);
        this.taskOwner = host;
        this.targetChance = chance;
        this.theNearestAttackableTargetSorter = Comparator.comparingDouble(e -> e.getDistanceSq(taskOwner));
        this.setMutexBits(1);
        this.targetEntitySelector = p_apply_1_ -> p_apply_1_ != null && (targetSelector == null || targetSelector.test(p_apply_1_)) && !(p_apply_1_ instanceof EntityPlayer && ((EntityPlayer)p_apply_1_).isSpectator()) && isSuitableTarget(p_apply_1_, false);
    }

    @Override
    public boolean shouldExecute() {
        if (taskOwner.ticksExisted % 10 != 0) return false;
        if (targetChance > 0 && taskOwner.getRNG().nextInt(targetChance) != 0) return false;
        List<EntityLivingBase> list = taskOwner.world.getEntitiesWithinAABB(EntityLivingBase.class, getTargetableArea(getTargetDistance()), targetEntitySelector::test);
        if (list.isEmpty()) return false;
        list.sort(theNearestAttackableTargetSorter);
        targetEntity = list.get(0);
        return true;
    }

    private AxisAlignedBB getTargetableArea(double targetDistance) {
        return taskOwner.getEntityBoundingBox().grow(targetDistance, 16.0, targetDistance);
    }

    @Override
    public void startExecuting() {
        taskOwner.setAttackTarget(targetEntity);
        super.startExecuting();
    }

    @Override
    public boolean isSuitableTarget(EntityLivingBase target, boolean includeInvincibles) {
        if (!super.isSuitableTarget(target, includeInvincibles)) return false;
        if (target.hasCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null)) {
            AttackerHandler ah = target.getCapability(CapabilityAttackerHandler.ATTACKER_HANDLER_CAPABILITY, null);
            if (ah != null && !ah.canAttack(taskOwner)) return false;
        }
        Double visObj = getVisibility(taskOwner, target);
        if (visObj != null && taskOwner.getDistance(target) > getTargetDistance() * visObj) return false;
        boolean flag = false;
        for (Predicate<EntityLivingBase> p : targetChecks) {
            if (p.test(target)) {
                flag = true;
                break;
            }
        }
        if (!flag && SiegeProps.ATK_PETS.get(taskOwner) && target instanceof IEntityOwnable && ((IEntityOwnable) target).getOwner() instanceof EntityPlayer) {
            flag = true;
        }
        return flag;
    }

    @SuppressWarnings("unchecked")
    public void addTarget(Class<? extends EntityLivingBase> target) {
        targetChecks.add((Predicate<EntityLivingBase>)(Predicate<?>)new PredicateTargetBasic<>(target));
    }

    public static Double getVisibility(EntityLivingBase host, EntityLivingBase input) {
        double visibility = 1.0;
        ItemStack itemstack = input.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (itemstack.getItem() == Items.SKULL) {
            int i = itemstack.getItemDamage();
            boolean flag0 = i == 0 && host instanceof EntitySkeleton;
            boolean flag2 = i == 2 && host instanceof EntityZombie;
            boolean flag3 = i == 4 && host instanceof EntityCreeper;
            if (flag0 || flag2 || flag3) visibility *= 0.5;
        } else if (itemstack.getItem() == Item.getItemFromBlock(Blocks.PUMPKIN) && host instanceof EntityEnderman) {
            return 0.0;
        }
        if (input.isSneaking()) visibility *= 0.8;
        if (input.isInvisible()) {
            double av = 0.1;
            int total = 0;
            int num = 0;
            for (ItemStack a : input.getArmorInventoryList()) {
                total++;
                if (a != null && !a.isEmpty()) num++;
            }
            if (total > 0) av = Math.max(0.1, total / (double) num);
            visibility *= av;
        }
        return visibility;
    }
}
