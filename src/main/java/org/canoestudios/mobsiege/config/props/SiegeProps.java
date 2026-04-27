package org.canoestudios.mobsiege.config.props;

import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import org.canoestudios.mobsiege.config.ConfigCategory;
import org.canoestudios.mobsiege.config.ConfigProperty;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiegeProps
{
    public static final ConfigCategory CAT_ROOT = new ConfigCategory(null);
    public static final ConfigCategory CAT_DIGGING = new ConfigCategory("Digging");
    public static final ConfigCategory CAT_BUILDING = new ConfigCategory("Building");
    public static final ConfigCategory CAT_CREEPER = new ConfigCategory("Creepers");
    public static final ConfigCategory CAT_RANGED = new ConfigCategory("Ranged");
    public static final ConfigCategory CAT_ATTRIBUTE = new ConfigCategory("Attributes");

    private static final List<String> POT_DEF = Arrays.asList(
            "minecraft:harming:1:0",
            "minecraft:slowness:200:0",
            "minecraft:blindness:200:0",
            "minecraft:poison:200:0",
            "minecraft:weakness:200:1",
            "minecraft:mining_fatigue:200:2"
    );

    private static final List<String> GRF_DEF = Arrays.asList(
            "minecraft:chest", "minecraft:furnace", "minecraft:crafting_table",
            "minecraft:melon_stem", "minecraft:pumpkin_stem", "minecraft:fence_gate",
            "minecraft:melon_block", "minecraft:pumpkin", "minecraft:glass",
            "minecraft:glass_pane", "minecraft:stained_glass", "minecraft:stained_glass_pane",
            "minecraft:carrots", "minecraft:potatoes", "minecraft:brewing_stand",
            "minecraft:enchanting_table", "minecraft:cake", "minecraft:ladder",
            "minecraft:wooden_door", "minecraft:farmland", "minecraft:bookshelf",
            "minecraft:sapling", "minecraft:bed", "minecraft:fence", "minecraft:planks"
    );

    private static final List<ConfigProperty> ALL_CFGS = new ArrayList<>();

    public static final ConfigPropNumber AWARENESS = addConfig(new ConfigPropNumber("Awareness Radius", 64));
    public static final ConfigPropNumber XRAY_VIEW = addConfig(new ConfigPropNumber("Xray Distance", 64));
    public static final ConfigPropBoolean ATK_ALL = addConfig(new ConfigPropBoolean("Attack Everything", false));
    public static final ConfigPropBoolean ATK_PETS = addConfig(new ConfigPropBoolean("Attack Pets", true));
    public static final ConfigPropBoolean ATK_VILLAGER = addConfig(new ConfigPropBoolean("Attack Villagers", true));
    public static final ConfigPropBoolean PASSIVE = addConfig(new ConfigPropBoolean("Neutral Mob", false));
    public static final ConfigPropBoolean STRAFE = addConfig(new ConfigPropBoolean("Strafe Evade", false));
    public static final ConfigPropBoolean ANIMAL_RET = addConfig(new ConfigPropBoolean("Animal Retaliate", true));
    public static final ConfigPropNumber RANGE_DIST = addConfig(new ConfigPropNumber("Ranged Atk Dist", 48), CAT_RANGED);
    public static final ConfigPropNumber RANGE_ERR = addConfig(new ConfigPropNumber("Ranged Atk Error", 0), CAT_RANGED);
    public static final ConfigPropList<String> ARROW_POT = addConfig(ConfigPropList.asString("Arrow Tips", Collections.singletonList("minecraft:slowness:60:0")), CAT_RANGED);
    public static final ConfigPropList<String> POTION_TH = addConfig(ConfigPropList.asString("Thrown Potions", POT_DEF), CAT_RANGED);
    public static final ConfigPropBoolean PILLAR = addConfig(new ConfigPropBoolean("Pillaring", false), CAT_BUILDING);
    public static final ConfigPropString PILLAR_BLOCK = addConfig(new ConfigPropString("Pillar Block", "minecraft:cobblestone:0"), CAT_BUILDING);
    public static final ConfigPropBoolean DIGGING = addConfig(new ConfigPropBoolean("Digging", false), CAT_DIGGING);
    public static final ConfigPropList<String> DIG_BL = addConfig(ConfigPropList.asString("Digging Blacklist", Collections.emptyList()), CAT_DIGGING);
    public static final ConfigPropBoolean DIG_BL_INV = addConfig(new ConfigPropBoolean("Invert Blacklist", false), CAT_DIGGING);
    public static final ConfigPropBoolean DIG_TOOLS = addConfig(new ConfigPropBoolean("Requires Tools", true), CAT_DIGGING);
    public static final ConfigPropBoolean GRIEF = addConfig(new ConfigPropBoolean("Griefing", false), CAT_DIGGING);
    public static final ConfigPropList<String> GRIEF_BLOCKS = addConfig(ConfigPropList.asString("Grief Targets", GRF_DEF), CAT_DIGGING);
    public static final ConfigPropNumber DEMOLITION = addConfig(new ConfigPropNumber("Demolition", 0.1), CAT_DIGGING);
    public static final ConfigPropNumber SP_WEB = addConfig(new ConfigPropNumber("Spider Webbing", 25.0));
    public static final ConfigPropBoolean EN_TELE = addConfig(new ConfigPropBoolean("Ender-Tele-Target", true));
    public static final ConfigPropBoolean INFECTIONS = addConfig(new ConfigPropBoolean("Infectious", false));
    public static final ConfigPropNumber WITHER_SKEL = addConfig(new ConfigPropNumber("Wither Skeletons", 0.1));
    public static final ConfigPropNumber CR_JOCKY = addConfig(new ConfigPropNumber("Jockey", 0.0), CAT_CREEPER);
    public static final ConfigPropNumber CR_POWERED = addConfig(new ConfigPropNumber("Powered", 0.1), CAT_CREEPER);
    public static final ConfigPropBoolean CR_FUSE = addConfig(new ConfigPropBoolean("Walking Fuse", true), CAT_CREEPER);
    public static final ConfigPropBoolean CR_FIRE = addConfig(new ConfigPropBoolean("Napalm", true), CAT_CREEPER);
    public static final ConfigPropBoolean CR_BREACH = addConfig(new ConfigPropBoolean("Breaching", true), CAT_CREEPER);
    public static final ConfigPropNumber CR_CENA = addConfig(new ConfigPropNumber("Cena", 0.01), CAT_CREEPER);
    public static final ConfigPropNumber MOD_HEALTH = addConfig(new ConfigPropNumber("Health", 1.0), CAT_ATTRIBUTE);
    public static final ConfigPropNumber MOD_DAMAGE = addConfig(new ConfigPropNumber("Damage", 1.0), CAT_ATTRIBUTE);
    public static final ConfigPropNumber MOD_SPEED = addConfig(new ConfigPropNumber("Speed", 1.0), CAT_ATTRIBUTE);
    public static final ConfigPropNumber MOD_KNOCKBACK = addConfig(new ConfigPropNumber("Knockback", 1.0), CAT_ATTRIBUTE);
    public static final ConfigPropBoolean BOSS_HEALTH = addConfig(new ConfigPropBoolean("Boss Mod Health", false), CAT_ATTRIBUTE);
    public static final ConfigPropBoolean BOSS_DAMAGE = addConfig(new ConfigPropBoolean("Boss Mod Damage", false), CAT_ATTRIBUTE);
    public static final ConfigPropBoolean BOSS_SPEED = addConfig(new ConfigPropBoolean("Boss Mod Speed", false), CAT_ATTRIBUTE);
    public static final ConfigPropBoolean BOSS_KNOCKBACK = addConfig(new ConfigPropBoolean("Boss Mod Knockback", false), CAT_ATTRIBUTE);
    public static final ConfigPropBoolean DEBUG_TARGET = addConfig(new ConfigPropBoolean("Debug Targets", false));

    public static <T extends ConfigProperty> T addConfig(T cfg) {
        ALL_CFGS.add(cfg);
        return cfg;
    }

    public static <T extends ConfigProperty> T addConfig(T cfg, ConfigCategory cat) {
        ALL_CFGS.add(cfg);
        return cfg;
    }

    public static void resetAllConfigs() {
        ALL_CFGS.forEach(ConfigProperty::resetDef);
    }
}
