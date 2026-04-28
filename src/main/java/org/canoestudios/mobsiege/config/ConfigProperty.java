package org.canoestudios.mobsiege.config;

import com.google.gson.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigProperty<T>
{
    private static final ConfigCategory DEFAULT_ROOT = new ConfigCategory(null);

    private final String key;
    private ConfigCategory cat;
    private final Function<JsonObject, T> funcGet;
    private final BiConsumer<JsonObject, T> funcSet;
    private final T def;

    public ConfigProperty(String key, T def, Function<JsonObject, T> funcGet, BiConsumer<JsonObject, T> funcSet) {
        this(key, (ConfigCategory) null, def, funcGet, funcSet);
    }

    public ConfigProperty(String key, ConfigCategory cat, T def, Function<JsonObject, T> funcGet, BiConsumer<JsonObject, T> funcSet) {
        this.cat = cat;
        this.key = key;
        this.def = def;
        this.funcGet = funcGet;
        this.funcSet = funcSet;
    }

    public ConfigProperty<T> setCategory(@Nullable ConfigCategory cat) {
        this.cat = cat;
        return this;
    }

    public String getKeyName() {
        return key;
    }

    private ConfigCategory getEffectiveCategory() {
        return cat != null ? cat : DEFAULT_ROOT;
    }

    public T get(@Nonnull Entity entity) {
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entity.getClass());
        return ee == null || ee.getRegistryName() == null ? def : get(ee.getRegistryName(), "dim_" + entity.world.provider.getDimension());
    }

    @Deprecated
    public T get(@Nullable ResourceLocation idName) {
        return get(idName, null);
    }

    public T get(@Nullable ResourceLocation idName, @Nullable String dim) {
        JsonObject conf = getEffectiveCategory().findProperty(idName, dim, key);
        return conf == null ? def : funcGet.apply(conf);
    }

    public void set(@Nonnull Entity entity, T value) {
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entity.getClass());
        if (ee != null && ee.getRegistryName() != null) {
            set(ee.getRegistryName(), "dim_" + entity.world.provider.getDimension(), value);
        }
    }

    @Deprecated
    public void set(@Nullable ResourceLocation idName, T value) {
        set(idName, null, value);
    }

    public void set(@Nullable ResourceLocation idName, @Nullable String dim, T value) {
        JsonObject conf = getEffectiveCategory().getOrCreate(idName, dim);
        if (conf != null) {
            funcSet.accept(conf, value);
        }
    }

    public void setDef(T value) {
        set(null, null, value);
    }

    public void resetDef() {
        setDef(def);
    }
}
