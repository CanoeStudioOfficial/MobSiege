package org.canoestudios.mobsiege;

import net.minecraft.command.ServerCommandManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.canoestudios.mobsiege.command.CommandReload;
import org.canoestudios.mobsiege.core.proxies.CommonProxy;
import org.canoestudios.mobsiege.handlers.ConfigHandler;

import java.io.File;

@Mod(modid = MobSiege.MODID, name = MobSiege.NAME, version = MobSiege.VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class MobSiege
{
    public static final String MODID = "mobsiege";
    public static final String NAME = "Mob Siege";
    public static final String VERSION = Tags.VERSION;

    @Mod.Instance(MODID)
    public static MobSiege instance;

    @SidedProxy(clientSide = "org.canoestudios.mobsiege.core.proxies.ClientProxy", serverSide = "org.canoestudios.mobsiege.core.proxies.CommonProxy")
    public static CommonProxy proxy;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File confFile = new File("config/mobsiege/mobsiege.cfg");
        File oldCfg = new File("config/mobsiege.cfg");
        if (oldCfg.exists()) {
            org.canoestudios.mobsiege.config.JsonHelper.copyTo(oldCfg, confFile);
            oldCfg.delete();
        }
        ConfigHandler.config = new net.minecraftforge.common.config.Configuration(confFile);
        ConfigHandler.initConfigs();
        proxy.registerHandlers();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.registerRenderers();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    }

    @Mod.EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        ((ServerCommandManager) server.getCommandManager()).registerCommand(new CommandReload());
    }
}
