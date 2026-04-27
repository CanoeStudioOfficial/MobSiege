package org.canoestudios.mobsiege.config;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.canoestudios.mobsiege.MobSiege;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConfigCategory
{
    public final String catKey;
    private final ConfigCategory catParent;
    private List<String> parCache;

    public ConfigCategory(@Nullable String catKey) {
        this(catKey, null);
    }

    public ConfigCategory(@Nullable String catKey, @Nullable ConfigCategory catParent) {
        this.catKey = catKey;
        this.catParent = catParent;
    }

    public JsonObject findProperty(Entity entity, String key) {
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entity.getClass());
        return ee == null || ee.getRegistryName() == null ? findProperty(null, null, key) : findProperty(ee.getRegistryName(), "dim_" + entity.world.provider.getDimension(), key);
    }

    @Nullable
    public JsonObject findProperty(@Nullable ResourceLocation idName, @Nullable String dim, String key) {
        JsonObject[] opts = {
            SiegeConfig.getConfig(null).get(null),
            SiegeConfig.getConfig(null).get(idName),
            SiegeConfig.getConfig(dim).get(null),
            SiegeConfig.getConfig(dim).get(idName)
        };

        if (parCache == null) {
            if (catKey == null) {
                parCache = Collections.emptyList();
            } else {
                parCache = new ArrayList<>();
                for (ConfigCategory cat = this; cat != null; cat = cat.catParent) {
                    parCache.add(cat.catKey);
                }
            }
        }

        for (int i = opts.length - 1; i >= 0; i--) {
            if (opts[i] != null) {
                JsonObject subCat = opts[i];
                boolean found = true;
                for (int j = parCache.size() - 1; j >= 0; j--) {
                    String s = parCache.get(j);
                    if (!subCat.has(s)) {
                        found = false;
                        break;
                    }
                    subCat = JsonHelper.getObject(subCat, s);
                }
                if (found && subCat.has(key)) {
                    return subCat;
                }
            }
        }

        StringBuilder sb = new StringBuilder("Failed to find key: root");
        for (int k = parCache.size() - 1; k >= 0; k--) {
            sb.append(" > ").append(parCache.get(k));
        }
        sb.append(" > ").append(key);
        MobSiege.LOGGER.warn(sb.toString());
        return null;
    }

    public JsonObject getOrCreate(@Nullable ResourceLocation idName, @Nullable String dim) {
        if (parCache == null) {
            if (catKey == null) {
                parCache = Collections.emptyList();
            } else {
                parCache = new ArrayList<>();
                for (ConfigCategory cat = this; cat != null; cat = cat.catParent) {
                    parCache.add(cat.catKey);
                }
            }
        }

        JsonObject subCat = SiegeConfig.getConfig(dim).getOrCreate(idName);
        for (int j = parCache.size() - 1; j >= 0; j--) {
            String s = parCache.get(j);
            if (!subCat.has(s)) {
                JsonObject json = new JsonObject();
                subCat.add(s, json);
                subCat = json;
            } else {
                subCat = JsonHelper.getObject(subCat, s);
            }
        }
        return subCat;
    }
}
