package org.canoestudios.mobsiege.config;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class SiegeConfig
{
    private static final Map<String, SiegeJsonConfig> DIM_MAP = new HashMap<>();
    private static final SiegeJsonConfig DIM_DEFAULT = new SiegeJsonConfig();

    public static SiegeJsonConfig getConfig(@Nullable String dim) {
        return dim == null ? DIM_DEFAULT : DIM_MAP.getOrDefault(dim, DIM_DEFAULT);
    }

    public static void loadDimConfig(String dim, com.google.gson.JsonObject json) {
        DIM_MAP.computeIfAbsent(dim, key -> createFromDefault()).readJson(json);
    }

    private static SiegeJsonConfig createFromDefault() {
        return DIM_DEFAULT.copyInto(new SiegeJsonConfig());
    }
}
