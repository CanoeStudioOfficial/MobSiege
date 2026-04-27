package org.canoestudios.mobsiege.config.props;

import com.google.gson.*;
import org.canoestudios.mobsiege.config.ConfigProperty;
import org.canoestudios.mobsiege.config.JsonHelper;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.function.Function;

public class ConfigPropList<T> extends ConfigProperty<List<T>>
{
    public ConfigPropList(String key, List<T> def, Function<JsonElement, T> funcGet, Function<T, JsonElement> funcSet) {
        super(key, def, json -> readList(key, json, funcGet), (json, value) -> writeList(key, json, value, funcSet));
    }

    private static <T> List<T> readList(String key, JsonObject json, Function<JsonElement, T> funcGet) {
        JsonArray ary = JsonHelper.getArray(json, key);
        if (ary.size() <= 0) {
            return Collections.emptyList();
        }
        List<T> list = new ArrayList<>();
        for (JsonElement e : ary) {
            if (e == null) continue;
            T value = funcGet.apply(e);
            if (value != null) list.add(value);
        }
        return list;
    }

    private static <T> void writeList(String key, JsonObject json, List<T> list, Function<T, JsonElement> funcSet) {
        JsonArray ary = new JsonArray();
        for (T val : list) {
            if (val == null) continue;
            JsonElement e = funcSet.apply(val);
            if (e != null) ary.add(e);
        }
        json.add(key, ary);
    }

    public static ConfigPropList<String> asString(String key, List<String> def) {
        return new ConfigPropList<>(key, def,
                json -> json.isJsonPrimitive() && json.getAsJsonPrimitive().isString() ? json.getAsString() : "",
                JsonPrimitive::new);
    }

    public static ConfigPropList<Number> asNumber(String key, List<Number> def) {
        return new ConfigPropList<>(key, def,
                json -> json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber() ? json.getAsNumber() : 0,
                JsonPrimitive::new);
    }

    public static ConfigPropList<ResourceLocation> asResource(String key, List<ResourceLocation> def) {
        return new ConfigPropList<>(key, def,
                json -> {
                    if (!json.isJsonPrimitive() || !json.getAsJsonPrimitive().isString()) return null;
                    try {
                        return new ResourceLocation(json.getAsString());
                    } catch (Exception ignored) {
                        return null;
                    }
                },
                res -> new JsonPrimitive(res.toString()));
    }
}
