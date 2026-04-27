package org.canoestudios.mobsiege.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class DynamicConfigGenerator
{
    public static JsonObject generateDefaultConfig()
    {
        JsonObject root = new JsonObject();
        root.add("default", generateGlobalDefault());
        JsonObject entities = new JsonObject();
        entities.add("minecraft:zombie", generateZombieDefaults());
        entities.add("minecraft:zombie_villager", generateZombieDefaults());
        entities.add("minecraft:husk", generateZombieDefaults());
        entities.add("minecraft:skeleton", generateSkeletonDefaults());
        entities.add("minecraft:wither_skeleton", generateWitherSkeletonDefaults());
        entities.add("minecraft:stray", generateSkeletonDefaults());
        entities.add("minecraft:creeper", generateCreeperDefaults());
        entities.add("minecraft:spider", generateSpiderDefaults());
        entities.add("minecraft:cave_spider", generateSpiderDefaults());
        entities.add("minecraft:enderman", generateEndermanDefaults());
        entities.add("minecraft:witch", generateWitchDefaults());
        entities.add("minecraft:blaze", generateBlazeDefaults());
        entities.add("minecraft:evoker", generateEvokerDefaults());
        entities.add("minecraft:vindicator", generateVindicatorDefaults());
        entities.add("minecraft:illager", generateVindicatorDefaults());
        root.add("entities", entities);
        return root;
    }

    private static JsonObject generateGlobalDefault()
    {
        JsonObject def = new JsonObject();
        def.addProperty("Awareness Radius", 64);
        def.addProperty("Xray Distance", 64);
        def.addProperty("Attack Everything", false);
        def.addProperty("Attack Pets", true);
        def.addProperty("Attack Villagers", true);
        def.addProperty("Neutral Mob", false);
        def.addProperty("Strafe Evade", false);
        def.addProperty("Animal Retaliate", true);
        def.addProperty("Spider Webbing", 25.0);
        def.addProperty("Ender-Tele-Target", true);
        def.addProperty("Infectious", false);
        def.addProperty("Wither Skeletons", 0.1);
        def.addProperty("Debug Targets", false);

        JsonObject digging = new JsonObject();
        digging.addProperty("Digging", false);
        digging.add("Digging Blacklist", new JsonArray());
        digging.addProperty("Invert Blacklist", false);
        digging.addProperty("Requires Tools", true);
        digging.addProperty("Griefing", false);
        digging.add("Grief Targets", createGriefTargetsList());
        digging.addProperty("Demolition", 0.0);
        def.add("Digging", digging);

        JsonObject building = new JsonObject();
        building.addProperty("Pillaring", false);
        building.addProperty("Pillar Block", "minecraft:cobblestone:0");
        def.add("Building", building);

        JsonObject creeper = new JsonObject();
        creeper.addProperty("Jockey", 0.0);
        creeper.addProperty("Powered", 0.1);
        creeper.addProperty("Walking Fuse", true);
        creeper.addProperty("Napalm", true);
        creeper.addProperty("Breaching", true);
        creeper.addProperty("Cena", 0.01);
        def.add("Creepers", creeper);

        JsonObject ranged = new JsonObject();
        ranged.addProperty("Ranged Atk Dist", 48);
        ranged.addProperty("Ranged Atk Error", 0);
        JsonArray arrowTips = new JsonArray();
        arrowTips.add("minecraft:slowness:60:0");
        ranged.add("Arrow Tips", arrowTips);
        JsonArray thrownPots = new JsonArray();
        thrownPots.add("minecraft:harming:1:0");
        thrownPots.add("minecraft:slowness:200:0");
        ranged.add("Thrown Potions", thrownPots);
        def.add("Ranged", ranged);

        JsonObject attributes = new JsonObject();
        attributes.addProperty("Health", 1.0);
        attributes.addProperty("Damage", 1.0);
        attributes.addProperty("Speed", 1.0);
        attributes.addProperty("Knockback", 1.0);
        attributes.addProperty("Boss Mod Health", false);
        attributes.addProperty("Boss Mod Damage", false);
        attributes.addProperty("Boss Mod Speed", false);
        attributes.addProperty("Boss Mod Knockback", false);
        def.add("Attributes", attributes);

        return def;
    }

    private static JsonObject generateZombieDefaults()
    {
        JsonObject zombie = new JsonObject();
        zombie.addProperty("Infectious", true);

        JsonObject digging = new JsonObject();
        digging.addProperty("Digging", true);
        digging.add("Digging Blacklist", new JsonArray());
        digging.addProperty("Invert Blacklist", false);
        digging.addProperty("Requires Tools", false);
        digging.addProperty("Griefing", true);
        digging.add("Grief Targets", createGriefTargetsList());
        digging.addProperty("Demolition", 0.05);
        zombie.add("Digging", digging);

        JsonObject building = new JsonObject();
        building.addProperty("Pillaring", true);
        building.addProperty("Pillar Block", "minecraft:cobblestone:0");
        zombie.add("Building", building);

        JsonObject attributes = new JsonObject();
        attributes.addProperty("Health", 1.5);
        attributes.addProperty("Damage", 1.2);
        zombie.add("Attributes", attributes);

        return zombie;
    }

    private static JsonObject generateSkeletonDefaults()
    {
        JsonObject skeleton = new JsonObject();
        JsonObject ranged = new JsonObject();
        ranged.addProperty("Ranged Atk Dist", 48);
        ranged.addProperty("Ranged Atk Error", 2);
        skeleton.add("Ranged", ranged);
        return skeleton;
    }

    private static JsonObject generateWitherSkeletonDefaults()
    {
        JsonObject ws = new JsonObject();
        JsonObject ranged = new JsonObject();
        ranged.addProperty("Ranged Atk Dist", 48);
        ranged.addProperty("Ranged Atk Error", 1);
        ws.add("Ranged", ranged);

        JsonObject digging = new JsonObject();
        digging.addProperty("Digging", true);
        digging.addProperty("Requires Tools", true);
        ws.add("Digging", digging);

        JsonObject attributes = new JsonObject();
        attributes.addProperty("Health", 1.3);
        attributes.addProperty("Damage", 1.3);
        ws.add("Attributes", attributes);
        return ws;
    }

    private static JsonObject generateCreeperDefaults()
    {
        JsonObject creeper = new JsonObject();
        JsonObject cr = new JsonObject();
        cr.addProperty("Powered", 0.15);
        cr.addProperty("Napalm", true);
        cr.addProperty("Breaching", true);
        cr.addProperty("Cena", 0.02);
        cr.addProperty("Jockey", 0.0);
        cr.addProperty("Walking Fuse", true);
        creeper.add("Creepers", cr);
        return creeper;
    }

    private static JsonObject generateSpiderDefaults()
    {
        JsonObject spider = new JsonObject();
        spider.addProperty("Spider Webbing", 30.0);
        JsonObject building = new JsonObject();
        building.addProperty("Pillaring", true);
        building.addProperty("Pillar Block", "minecraft:cobblestone:0");
        spider.add("Building", building);
        return spider;
    }

    private static JsonObject generateEndermanDefaults()
    {
        JsonObject enderman = new JsonObject();
        enderman.addProperty("Ender-Tele-Target", true);
        JsonObject digging = new JsonObject();
        digging.addProperty("Digging", true);
        digging.addProperty("Requires Tools", false);
        enderman.add("Digging", digging);
        return enderman;
    }

    private static JsonObject generateWitchDefaults()
    {
        JsonObject witch = new JsonObject();
        JsonObject ranged = new JsonObject();
        JsonArray thrownPots = new JsonArray();
        thrownPots.add("minecraft:harming:1:0");
        thrownPots.add("minecraft:slowness:200:0");
        thrownPots.add("minecraft:poison:200:0");
        thrownPots.add("minecraft:weakness:200:1");
        ranged.add("Thrown Potions", thrownPots);
        witch.add("Ranged", ranged);
        return witch;
    }

    private static JsonObject generateBlazeDefaults()
    {
        JsonObject blaze = new JsonObject();
        JsonObject ranged = new JsonObject();
        ranged.addProperty("Ranged Atk Dist", 48);
        ranged.addProperty("Ranged Atk Error", 3);
        blaze.add("Ranged", ranged);
        return blaze;
    }

    private static JsonObject generateEvokerDefaults()
    {
        JsonObject evoker = new JsonObject();
        JsonObject ranged = new JsonObject();
        ranged.addProperty("Ranged Atk Dist", 32);
        evoker.add("Ranged", ranged);
        return evoker;
    }

    private static JsonObject generateVindicatorDefaults()
    {
        JsonObject vindicator = new JsonObject();
        vindicator.addProperty("Strafe Evade", true);
        JsonObject attributes = new JsonObject();
        attributes.addProperty("Speed", 1.2);
        vindicator.add("Attributes", attributes);
        return vindicator;
    }

    private static JsonArray createGriefTargetsList()
    {
        JsonArray arr = new JsonArray();
        arr.add("minecraft:chest");
        arr.add("minecraft:furnace");
        arr.add("minecraft:crafting_table");
        arr.add("minecraft:glass");
        arr.add("minecraft:glass_pane");
        arr.add("minecraft:stained_glass");
        arr.add("minecraft:stained_glass_pane");
        arr.add("minecraft:wooden_door");
        arr.add("minecraft:bed");
        arr.add("minecraft:bookshelf");
        arr.add("minecraft:fence");
        arr.add("minecraft:fence_gate");
        arr.add("minecraft:ladder");
        arr.add("minecraft:brewing_stand");
        arr.add("minecraft:enchanting_table");
        arr.add("minecraft:cake");
        arr.add("minecraft:carrots");
        arr.add("minecraft:potatoes");
        arr.add("minecraft:farmland");
        arr.add("minecraft:sapling");
        arr.add("minecraft:pumpkin");
        arr.add("minecraft:melon_block");
        arr.add("minecraft:pumpkin_stem");
        arr.add("minecraft:melon_stem");
        arr.add("minecraft:planks");
        return arr;
    }

    public static JsonObject mergeWithExisting(JsonObject defaults, JsonObject existing)
    {
        if (existing == null || existing.entrySet().isEmpty()) return defaults;
        JsonObject merged = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
            String key = entry.getKey();
            if (existing.has(key)) {
                if (entry.getValue().isJsonObject() && existing.get(key).isJsonObject()) {
                    merged.add(key, mergeWithExisting(entry.getValue().getAsJsonObject(), existing.getAsJsonObject(key)));
                } else {
                    merged.add(key, existing.get(key));
                }
            } else {
                merged.add(key, entry.getValue());
            }
        }
        for (Map.Entry<String, JsonElement> entry : existing.entrySet()) {
            String key = entry.getKey();
            if (!defaults.has(key)) {
                merged.add(key, entry.getValue());
            }
        }
        return merged;
    }
}
