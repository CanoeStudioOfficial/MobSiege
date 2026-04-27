package org.canoestudios.mobsiege.handlers.entities;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;

import java.util.List;
import java.util.Random;

public class PlayerHandler
{
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntity().world.isRemote || !(event.getEntity() instanceof EntityPlayer) || !(event.getEntity().world instanceof WorldServer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntity();
        int day = (int) (player.world.getWorldTime() / 24000L);
        boolean hard = SiegeConfigGlobal.hardDay != 0 && day != 0 && day % SiegeConfigGlobal.hardDay == 0;
        Random rand = player.getRNG();
        if (hard && rand.nextInt(10) == 0 && player.world.getDifficulty() != EnumDifficulty.PEACEFUL && player.world.getGameRules().getBoolean("doMobSpawning") && player.world.loadedEntityList.size() < 512) {
            int x = MathHelper.floor(player.posX) + rand.nextInt(48) - 24;
            int y = MathHelper.floor(player.posY) + rand.nextInt(48) - 24;
            int z = MathHelper.floor(player.posZ) + rand.nextInt(48) - 24;
            BlockPos spawnPos = new BlockPos(x, y, z);
            if (player.world.getClosestPlayer(x, y, z, 8.0, false) == null && net.minecraft.world.WorldEntitySpawner.canCreatureTypeSpawnAtLocation(EntityLiving.SpawnPlacementType.ON_GROUND, player.world, spawnPos)) {
                Biome.SpawnListEntry spawnlistentry = ((WorldServer) player.world).getSpawnListEntryForTypeAt(net.minecraft.entity.EnumCreatureType.MONSTER, spawnPos);
                if (spawnlistentry != null) {
                    try {
                        EntityLiving entityliving = spawnlistentry.entityClass.getConstructor(net.minecraft.world.World.class).newInstance(player.world);
                        entityliving.setLocationAndAngles(x, y, z, rand.nextFloat() * 360.0f, 0.0f);
                        if (entityliving.getCanSpawnHere()) {
                            player.world.spawnEntity(entityliving);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRespawn(PlayerEvent.PlayerLoggedInEvent event) {
        if (SiegeConfigGlobal.ResistanceCoolDown > 0) {
            event.player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, SiegeConfigGlobal.ResistanceCoolDown, 5));
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (SiegeConfigGlobal.ResistanceCoolDown > 0) {
            event.player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, SiegeConfigGlobal.ResistanceCoolDown, 5));
        }
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (SiegeConfigGlobal.ResistanceCoolDown > 0) {
            event.player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, SiegeConfigGlobal.ResistanceCoolDown, 5));
        }
    }

    @SubscribeEvent
    public void onPlayerSleepInBed(PlayerSleepInBedEvent event) {
        if (SiegeConfigGlobal.AllowSleep || event.getEntityPlayer().world.isRemote) return;
        if (event.getEntityPlayer().isPlayerSleeping() || !event.getEntityPlayer().isEntityAlive()) return;
        if ((!event.getEntityPlayer().world.provider.canRespawnHere() || event.getEntityPlayer().world.isDaytime()) && (Math.abs(event.getEntityPlayer().posX - event.getPos().getX()) > 3.0 || Math.abs(event.getEntityPlayer().posY - event.getPos().getY()) > 2.0 || Math.abs(event.getEntityPlayer().posZ - event.getPos().getZ()) > 3.0)) {
            return;
        }
        double xOff = 8.0;
        double yOff = 5.0;
        List<?> list = event.getEntityPlayer().world.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB(event.getPos().getX() - xOff, event.getPos().getY() - yOff, event.getPos().getZ() - xOff, event.getPos().getX() + xOff, event.getPos().getY() + yOff, event.getPos().getZ() + xOff));
        if (!list.isEmpty()) return;
        event.setResult(EntityPlayer.SleepResult.OTHER_PROBLEM);
        if (event.getEntityPlayer().isRiding()) {
            event.getEntityPlayer().dismountRidingEntity();
        }
        event.getEntityPlayer().setSpawnChunk(event.getPos(), false, event.getEntityPlayer().dimension);
        event.getEntityPlayer().sendMessage(new TextComponentString("Spawnpoint set"));
    }
}
