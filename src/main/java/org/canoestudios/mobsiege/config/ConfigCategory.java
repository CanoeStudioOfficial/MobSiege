package org.canoestudios.mobsiege.config;

import javax.annotation.Nullable;

public class ConfigCategory
{
    public final String catKey;
    private final ConfigCategory catParent;
    private java.util.List<String> parCache;

    public ConfigCategory(@Nullable String catKey) {
        this(catKey, null);
    }

    public ConfigCategory(@Nullable String catKey, @Nullable ConfigCategory catParent) {
        this.catKey = catKey;
        this.catParent = catParent;
    }

    public java.util.List<String> getParentCache() {
        if (parCache == null) {
            if (catKey == null) {
                parCache = java.util.Collections.emptyList();
            } else {
                parCache = new java.util.ArrayList<>();
                for (ConfigCategory cat = this; cat != null; cat = cat.catParent) {
                    parCache.add(cat.catKey);
                }
            }
        }
        return parCache;
    }
}
