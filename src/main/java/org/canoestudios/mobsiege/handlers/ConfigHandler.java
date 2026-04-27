package org.canoestudios.mobsiege.handlers;

import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.ai.SiegeAIPillarUp;
import org.canoestudios.mobsiege.config.DynamicConfigGenerator;
import org.canoestudios.mobsiege.config.JsonHelper;
import org.canoestudios.mobsiege.config.SiegeConfig;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.SiegeJsonConfig;

import java.io.File;

public class ConfigHandler
{
    public static Configuration config;
    private static final String CAT_MAIN = "General";
    private static final String CAT_ADVANCED = "Other";
    private static final String CONFIG_VERSION = "2";

    public static void initConfigs()
    {
        if (config == null)
        {
            MobSiege.LOGGER.error("Config attempted to be loaded before it was initialised!");
            return;
        }
        config.load();
        SiegeConfigGlobal.hardDay = config.getInt("Hardcore Day Cycle", CAT_MAIN, 8, 0, Integer.MAX_VALUE, "The interval in which 'hard' days will occur where mob spawning is increased and lighting is ignored (0 = off, default = 8/full moon)");
        SiegeConfigGlobal.TargetCap = config.getInt("Pathing Cap", CAT_MAIN, 16, 0, 128, "Maximum number of attackers per target");
        SiegeConfigGlobal.AllowSleep = config.getBoolean("Allow Sleep", CAT_MAIN, false, "Prevents players skipping the night through sleep");
        SiegeConfigGlobal.ResistanceCoolDown = config.getInt("Resistance Cooldown", CAT_MAIN, 200, 0, Integer.MAX_VALUE, "Temporary invulnerability in ticks when respawning and teleporting");
        SiegeConfigGlobal.AIExempt.clear();
        for (String s : config.getStringList("AI Blacklist", CAT_MAIN, new String[]{"minecraft:villager_golem"}, "Mobs that are exempt from AI modifications"))
        {
            SiegeConfigGlobal.AIExempt.add(new ResourceLocation(s));
        }
        SiegeConfigGlobal.altChunkCache = config.getBoolean("Alt Chunk Caching", CAT_ADVANCED, true, "Can fix some issues with long distance navigation pathing through unloaded chunks");
        SiegeConfigGlobal.bossModifier = config.getFloat("Boss Kill Modifier", CAT_ADVANCED, 0.1f, 0.0f, Float.MAX_VALUE, "The factor by which mob health and damage multipliers will be increased when bosses are killed");
        SiegeConfigGlobal.bossModCap = config.getFloat("Boss Modifier Cap", CAT_ADVANCED, 3.0f, 0.0f, Float.MAX_VALUE, "The upper limit of the Boss Kill Modifier");
        String cfgVersion = config.getString("Config Version", CAT_ADVANCED, "0", "Internal version tracking for config migration. Do not change manually.");

        loadDynamicEntityConfig(cfgVersion);

        config.get(CAT_ADVANCED, "Config Version", CONFIG_VERSION).set(CONFIG_VERSION);
        config.save();
        SiegeAIPillarUp.updateBlock = true;
    }

    private static void loadDynamicEntityConfig(String currentVersion)
    {
        File cfgDir = new File("config/mobsiege");
        if (!cfgDir.exists()) cfgDir.mkdirs();
        File cfgFile = new File(cfgDir, "entity_ai.json");

        JsonObject dynamicDefaults = DynamicConfigGenerator.generateDefaultConfig();
        SiegeJsonConfig cfgSet = SiegeConfig.getConfig(null);

        if (cfgFile.exists())
        {
            JsonObject existingJson = JsonHelper.readFromFile(cfgFile);
            if (existingJson.entrySet().isEmpty() || needsMigration(currentVersion))
            {
                MobSiege.LOGGER.info("Migrating entity AI config to version " + CONFIG_VERSION + "...");
                JsonObject merged = DynamicConfigGenerator.mergeWithExisting(dynamicDefaults, existingJson);
                cfgSet.readJson(merged);
                JsonHelper.writeToFile(cfgFile, merged);
            }
            else
            {
                cfgSet.readJson(existingJson);
            }
        }
        else
        {
            MobSiege.LOGGER.info("Generating default entity AI config with dynamic defaults...");
            cfgSet.readJson(dynamicDefaults);
            JsonHelper.writeToFile(cfgFile, dynamicDefaults);
        }
    }

    private static boolean needsMigration(String currentVersion)
    {
        if ("0".equals(currentVersion)) return true;
        try
        {
            int cur = Integer.parseInt(currentVersion);
            return cur < Integer.parseInt(CONFIG_VERSION);
        }
        catch (NumberFormatException e)
        {
            return true;
        }
    }
}
