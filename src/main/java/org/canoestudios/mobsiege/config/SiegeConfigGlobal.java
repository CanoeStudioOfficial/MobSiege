package org.canoestudios.mobsiege.config;

import net.minecraft.util.ResourceLocation;
import java.util.ArrayList;
import java.util.List;

public class SiegeConfigGlobal
{
    public static int TargetCap = 16;
    public static boolean AllowSleep = false;
    public static int ResistanceCoolDown = 200;
    public static int hardDay = 8;
    public static List<ResourceLocation> AIExempt = new ArrayList<>();
    public static float bossModifier = 0.1f;
    public static float bossModCap = 3.0f;
    public static boolean altChunkCache = true;
}
