package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.capabilities.modified.CapabilityModifiedHandler;
import org.canoestudios.mobsiege.capabilities.modified.IModifiedHandler;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.io.File;
import java.util.UUID;

public class GeneralEntityHandler
{
    private final ResourceLocation DIM_MODIFIER = new ResourceLocation("mobsiege", "general_spawn");
    private final UUID attModHP = UUID.fromString("74dcd479-97f3-4a04-b84a-0ffab0863a4f");
    private final UUID attModSP = UUID.fromString("2e1a9c33-bbd9-4daf-a723-e598e41ddeb9");
    private final UUID attModAT = UUID.fromString("7dd7b301-055b-4bf1-b94a-2a47a6338ca1");
    private final UUID attModKB = UUID.fromString("321eab99-4946-4375-a693-c0dce3706b6d");
    private static float curBossMod = 0.0f;
    private File worldDir;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote || event.getEntity().isDead || event.isCanceled() || !(event.getEntity() instanceof EntityMob)) return;
        EntityMob entityMob = (EntityMob) event.getEntity();
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entityMob.getClass());
        if (ee == null || SiegeConfigGlobal.AIExempt.contains(ee.getRegistryName())) return;
        IModifiedHandler modHandler = entityMob.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
        if (modHandler == null) return;
        double healthMult = SiegeProps.MOD_HEALTH.get(entityMob).doubleValue() + (SiegeProps.BOSS_HEALTH.get(entityMob) ? curBossMod : 0.0);
        double damageMult = SiegeProps.MOD_DAMAGE.get(entityMob).doubleValue() + (SiegeProps.BOSS_DAMAGE.get(entityMob) ? curBossMod : 0.0);
        double speedMult = SiegeProps.MOD_SPEED.get(entityMob).doubleValue() + (SiegeProps.BOSS_SPEED.get(entityMob) ? curBossMod : 0.0);
        double knockbackMult = SiegeProps.MOD_KNOCKBACK.get(entityMob).doubleValue() + (SiegeProps.BOSS_KNOCKBACK.get(entityMob) ? curBossMod : 0.0);
        if (!modHandler.getModificationData(DIM_MODIFIER).getBoolean("hasModifiers")) {
            if (healthMult != 1.0) {
                boolean fullHeal = entityMob.getHealth() < entityMob.getMaxHealth();
                entityMob.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(attModHP, "MS_TWEAK_1", healthMult, 1));
                if (fullHeal) entityMob.setHealth(entityMob.getMaxHealth());
            }
            if (speedMult != 1.0) {
                entityMob.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).applyModifier(new AttributeModifier(attModSP, "MS_TWEAK_2", speedMult, 1));
            }
            if (damageMult != 1.0) {
                entityMob.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).applyModifier(new AttributeModifier(attModAT, "MS_TWEAK_3", damageMult, 1));
            }
            if (knockbackMult != 1.0) {
                entityMob.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).applyModifier(new AttributeModifier(attModKB, "MS_TWEAK_4", knockbackMult, 1));
            }
            modHandler.getModificationData(DIM_MODIFIER).setBoolean("hasModifiers", true);
        }
        double creeperJocky = SiegeProps.CR_JOCKY.get(entityMob).doubleValue();
        if (creeperJocky > 0.0 && !modHandler.getModificationData(DIM_MODIFIER).getBoolean("checkMobBomb") && entityMob.getPassengers().size() == 0 && entityMob.getRidingEntity() == null && entityMob.world.loadedEntityList.size() < 512 && (creeperJocky >= 100.0 || entityMob.getRNG().nextDouble() * 100.0 < creeperJocky)) {
            EntityLiving passenger = new EntityCreeper(entityMob.world);
            IModifiedHandler passHandler = passenger.getCapability(CapabilityModifiedHandler.MODIFIED_HANDLER_CAPABILITY, null);
            if (passHandler != null) {
                passHandler.getModificationData(DIM_MODIFIER).setBoolean("checkMobBomb", true);
            }
            passenger.setLocationAndAngles(entityMob.posX, entityMob.posY, entityMob.posZ, entityMob.rotationYaw, 0.0f);
            passenger.onInitialSpawn(entityMob.world.getDifficultyForLocation(new BlockPos(entityMob)), null);
            entityMob.world.spawnEntity(passenger);
            passenger.startRiding(entityMob);
            for (EntityPlayer playersNear : entityMob.world.playerEntities) {
                if (playersNear instanceof EntityPlayerMP) {
                    ((EntityPlayerMP) playersNear).connection.sendPacket(new SPacketSetPassengers(passenger));
                }
            }
        }
        modHandler.getModificationData(DIM_MODIFIER).setBoolean("checkMobBomb", true);
    }

    @SubscribeEvent
    public void onEntityKilled(LivingDeathEvent event) {
        if (event.getEntity().world.isRemote || event.getEntity().isNonBoss()) return;
        curBossMod = Math.min(SiegeConfigGlobal.bossModCap, curBossMod + SiegeConfigGlobal.bossModifier);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote || worldDir != null) return;
        net.minecraft.server.MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (!server.isServerRunning()) return;
        if (MobSiege.proxy.isClient()) {
            worldDir = server.getFile("saves/" + server.getFolderName());
        } else {
            worldDir = server.getFile(server.getFolderName());
        }
        try {
            NBTTagCompound wmTag = net.minecraft.nbt.CompressedStreamTools.read(new File(worldDir, "MobSiege.dat"));
            curBossMod = wmTag == null ? 0.0f : Math.min(SiegeConfigGlobal.bossModCap, wmTag.getFloat("BossModifier"));
        } catch (Exception ex) {
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) return;
        net.minecraft.server.MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (!server.isServerRunning()) {
            curBossMod = 0.0f;
            worldDir = null;
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (event.getWorld().isRemote || worldDir == null) return;
        try {
            NBTTagCompound wmTag = new NBTTagCompound();
            wmTag.setFloat("BossModifier", curBossMod);
            net.minecraft.nbt.CompressedStreamTools.write(wmTag, new File(worldDir, "MobSiege.dat"));
        } catch (Exception ex) {
        }
    }
}
