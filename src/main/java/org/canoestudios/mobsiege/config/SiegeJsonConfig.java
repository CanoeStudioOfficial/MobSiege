package org.canoestudios.mobsiege.config;

import com.google.gson.*;
import org.canoestudios.mobsiege.MobSiege;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class SiegeJsonConfig
{
    private SiegeJsonConfig parentSet;
    private final Map<net.minecraft.util.ResourceLocation, JsonObject> MAIN_CONFIG = new HashMap<>();
    private final JsonObject DEF_CONFIG = new JsonObject();
    private static final Gson GSON = new GsonBuilder().create();

    public SiegeJsonConfig setParentSet(SiegeJsonConfig set) {
        this.parentSet = set;
        return this;
    }

    public JsonObject getOrCreate(@Nullable net.minecraft.util.ResourceLocation idName) {
        return MAIN_CONFIG.computeIfAbsent(idName, id -> new JsonObject());
    }

    public JsonObject get(@Nullable net.minecraft.util.ResourceLocation idName) {
        JsonObject json = MAIN_CONFIG.get(idName);
        return json != null ? json : MAIN_CONFIG.get(null);
    }

    public void clear() {
        MAIN_CONFIG.clear();
    }

    public int size() {
        return MAIN_CONFIG.size();
    }

    public void readJson(JsonObject json) {
        clear();
        JsonObject jEnt = JsonHelper.getObject(json, "entities");
        for (Map.Entry<String, JsonElement> entry : jEnt.entrySet()) {
            if (!entry.getValue().isJsonObject()) continue;
            try {
                MAIN_CONFIG.put(new net.minecraft.util.ResourceLocation(entry.getKey()), deepJsonCopy(entry.getValue().getAsJsonObject()));
            } catch (Exception e) {
                MobSiege.LOGGER.error("Unable to parse entity ID: " + entry.getKey(), e);
            }
        }
        MAIN_CONFIG.put(null, deepJsonCopy(JsonHelper.getObject(json, "default")));
    }

    public JsonObject writeJson(JsonObject json) {
        JsonObject jEnt = new JsonObject();
        for (Map.Entry<net.minecraft.util.ResourceLocation, JsonObject> entry : MAIN_CONFIG.entrySet()) {
            JsonObject temp = new JsonObject();
            for (Map.Entry<String, JsonElement> sub : entry.getValue().entrySet()) {
                temp.add(sub.getKey(), deepJsonCopy(sub.getValue()));
            }
            if (entry.getKey() == null) {
                json.add("default", temp);
            } else {
                jEnt.add(entry.getKey().toString(), temp);
            }
        }
        json.add("entities", jEnt);
        return json;
    }

    public SiegeJsonConfig copyInto(SiegeJsonConfig dest) {
        dest.readJson(this.writeJson(new JsonObject()));
        return dest;
    }

    public void resetToDefault() {
        clear();
        org.canoestudios.mobsiege.config.props.SiegeProps.resetAllConfigs();
    }

    @SuppressWarnings("unchecked")
    private static <T extends JsonElement> T deepJsonCopy(T src) {
        try {
            return (T) GSON.fromJson(GSON.toJson(src), (Type) src.getClass());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
