package org.canoestudios.mobsiege.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import org.canoestudios.mobsiege.MobSiege;
import org.canoestudios.mobsiege.ai.SiegeAIPillarUp;
import org.canoestudios.mobsiege.config.JsonHelper;
import org.canoestudios.mobsiege.config.SiegeConfig;
import org.canoestudios.mobsiege.config.SiegeConfigGlobal;
import org.canoestudios.mobsiege.config.SiegeJsonConfig;
import org.canoestudios.mobsiege.config.props.SiegeProps;

import java.io.File;
import java.util.Arrays;

public class ConfigHandler
{
    public static Configuration config;
    private static final String CAT_MAIN = "General";
    private static final String CAT_ADVANCED = "Other";

    public static void initConfigs() {
        if (config == null) {
            MobSiege.LOGGER.error("Config attempted to be loaded before it was initialised!");
            return;
        }
        config.load();
        SiegeConfigGlobal.hardDay = config.getInt("Hardcore Day Cycle", CAT_MAIN, 8, 0, Integer.MAX_VALUE, "The interval in which 'hard' days will occur where mob spawning is increased and lighting is ignored (0 = off, default = 8/full moon)");
        SiegeConfigGlobal.TargetCap = config.getInt("Pathing Cap", CAT_MAIN, 16, 0, 128, "Maximum number of attackers per target");
        SiegeConfigGlobal.AllowSleep = config.getBoolean("Allow Sleep", CAT_MAIN, false, "Prevents players skipping the night through sleep");
        SiegeConfigGlobal.ResistanceCoolDown = config.getInt("Resistance Cooldown", CAT_MAIN, 200, 0, Integer.MAX_VALUE, "Temporary invulnerability in ticks when respawning and teleporting");
        SiegeConfigGlobal.AIExempt.clear();
        for (String s : config.getStringList("AI Blacklist", CAT_MAIN, new String[]{"minecraft:villager_golem"}, "Mobs that are exempt from AI modifications")) {
            SiegeConfigGlobal.AIExempt.add(new ResourceLocation(s));
        }
        SiegeConfigGlobal.altChunkCache = config.getBoolean("Alt Chunk Caching", CAT_ADVANCED, true, "Can fix some issues with long distance navigation pathing through unloaded chunks");
        SiegeConfigGlobal.bossModifier = config.getFloat("Boss Kill Modifier", CAT_ADVANCED, 0.1f, 0.0f, Float.MAX_VALUE, "The factor by which mob health and damage multipliers will be increased when bosses are killed");
        SiegeConfigGlobal.bossModCap = config.getFloat("Boss Modifier Cap", CAT_ADVANCED, 3.0f, 0.0f, Float.MAX_VALUE, "The upper limit of the Boss Kill Modifier");

        SiegeJsonConfig cfgSet = SiegeConfig.getConfig(null);
        File cfgFile = new File("config/mobsiege/entity_ai.json");
        JsonObject aiJson;
        if (cfgFile.exists()) {
            aiJson = JsonHelper.readFromFile(cfgFile);
            cfgSet.readJson(aiJson);
        } else {
            cfgSet.resetToDefault();
            aiJson = cfgSet.writeJson(new JsonObject());
        }
        JsonHelper.writeToFile(cfgFile, aiJson);
        config.save();
        SiegeAIPillarUp.updateBlock = true;
    }
}
