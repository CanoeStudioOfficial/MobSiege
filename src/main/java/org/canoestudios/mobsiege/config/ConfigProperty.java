package org.canoestudios.mobsiege.config;

import com.google.gson.*;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConfigProperty<T>
{
    private final String key;
    private final ConfigCategory cat;
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

    public ConfigProperty<T> setCategory(ConfigCategory cat) {
        return this;
    }

    public String getKeyName() {
        return key;
    }

    public T get(Entity entity) {
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entity.getClass());
        return ee == null || ee.getRegistryName() == null ? def : get(ee.getRegistryName(), "dim_" + entity.world.provider.getDimension());
    }

    public T get(@Nullable ResourceLocation idName) {
        return get(idName, null);
    }

    public T get(@Nullable ResourceLocation idName, @Nullable String dim) {
        JsonObject conf = SiegeConfig.getConfig(dim).get(idName);
        return conf == null ? def : funcGet.apply(conf);
    }

    public void set(Entity entity, T value) {
        net.minecraftforge.fml.common.registry.EntityEntry ee = EntityRegistry.getEntry(entity.getClass());
        if (ee != null && ee.getRegistryName() != null) {
            set(ee.getRegistryName(), "dim_" + entity.world.provider.getDimension(), value);
        }
    }

    public void set(@Nullable ResourceLocation idName, T value) {
        set(idName, null, value);
    }

    public void set(@Nullable ResourceLocation idName, @Nullable String dim, T value) {
        SiegeJsonConfig dimConfig = SiegeConfig.getConfig(dim);
        JsonObject conf = dimConfig.getOrCreate(idName);
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
