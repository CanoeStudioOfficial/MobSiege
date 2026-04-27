package org.canoestudios.mobsiege.config;

import com.google.gson.*;
import org.canoestudios.mobsiege.MobSiege;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.function.Supplier;

public class JsonHelper
{
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonArray getArray(@Nonnull JsonObject json, @Nonnull String key) {
        return getArray(json, key, JsonArray::new);
    }

    public static JsonArray getArray(@Nonnull JsonObject json, @Nonnull String key, Supplier<JsonArray> def) {
        JsonElement je = json.get(key);
        return je instanceof JsonArray ? je.getAsJsonArray() : def.get();
    }

    public static JsonObject getObject(@Nonnull JsonObject json, @Nonnull String key) {
        return getObject(json, key, JsonObject::new);
    }

    public static JsonObject getObject(@Nonnull JsonObject json, @Nonnull String key, Supplier<JsonObject> def) {
        JsonElement je = json.get(key);
        return je instanceof JsonObject ? je.getAsJsonObject() : def.get();
    }

    public static String getString(@Nonnull JsonObject json, @Nonnull String key) {
        return getString(json, key, "");
    }

    public static String getString(@Nonnull JsonObject json, @Nonnull String key, String def) {
        JsonElement je = json.get(key);
        return je instanceof JsonPrimitive && je.getAsJsonPrimitive().isString() ? je.getAsString() : def;
    }

    public static Number getNumber(@Nonnull JsonObject json, @Nonnull String key) {
        return getNumber(json, key, 0);
    }

    public static Number getNumber(@Nonnull JsonObject json, @Nonnull String key, Number def) {
        JsonElement je = json.get(key);
        try {
            return je instanceof JsonPrimitive ? je.getAsNumber() : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean getBoolean(@Nonnull JsonObject json, @Nonnull String key) {
        return getBoolean(json, key, false);
    }

    public static boolean getBoolean(@Nonnull JsonObject json, @Nonnull String key, boolean def) {
        JsonElement je = json.get(key);
        try {
            return je instanceof JsonPrimitive ? je.getAsBoolean() : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static JsonElement getElement(@Nonnull JsonObject json, @Nonnull String key) {
        return getElement(json, key, () -> JsonNull.INSTANCE);
    }

    public static JsonElement getElement(@Nonnull JsonObject json, @Nonnull String key, @Nonnull Supplier<JsonElement> def) {
        JsonElement je = json.get(key);
        return je != null ? je : def.get();
    }

    public static JsonObject readFromFile(@Nonnull File file) {
        return readFromFile(file, JsonObject.class, JsonObject::new);
    }

    public static <T extends JsonElement> T readFromFile(@Nonnull File file, Class<T> cls, @Nonnull Supplier<T> def) {
        if (!file.exists()) {
            return def.get();
        }
        T json;
        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            json = GSON.fromJson(isr, cls);
        } catch (Exception e) {
            MobSiege.LOGGER.warn("Error loading JSON from file:", e);
            json = def.get();
        }
        return json;
    }

    public static void writeToFile(@Nonnull File file, JsonElement json) {
        File tmp = new File(file.getAbsolutePath() + ".tmp");
        try {
            if (tmp.exists()) {
                if (!tmp.delete()) {
                    throw new IOException("Unable to delete old temp file!");
                }
            } else if (tmp.getParentFile() != null && !tmp.getParentFile().exists() && !tmp.getParentFile().mkdirs()) {
                throw new IOException("Unable to create parent directory!");
            }
        } catch (IOException e) {
            MobSiege.LOGGER.warn("Error writing JSON (Directory Setup): " + e);
            return;
        }
        try (FileOutputStream fos = new FileOutputStream(tmp);
             OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            GSON.toJson(json, osw);
            osw.flush();
        } catch (IOException e) {
            MobSiege.LOGGER.warn("Error writing JSON (File Write): " + e);
            return;
        }
        try (FileInputStream fis = new FileInputStream(tmp);
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {
            GSON.fromJson(isr, json.getClass());
        } catch (IOException e) {
            MobSiege.LOGGER.warn("Error writing JSON (Validation): " + e);
            return;
        }
        try {
            if (file.exists() && !file.delete()) {
                throw new IOException("Failed to delete old file!");
            }
            if (!tmp.renameTo(file)) {
                throw new IOException("Failed to rename temp file!");
            }
        } catch (IOException e) {
            MobSiege.LOGGER.warn("Error writing JSON (Temp Copy): " + e);
        }
    }

    public static void copyTo(File fileIn, File fileOut) {
        if (!fileIn.exists()) return;
        if (fileOut.getParentFile() != null) {
            fileOut.getParentFile().mkdirs();
        }
        try {
            Files.copy(fileIn.toPath(), fileOut.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            MobSiege.LOGGER.warn("Error copying file (" + fileIn + ") to (" + fileOut + "):", e);
        }
    }

    public static void createBackup(File file, String prefix) {
        String name = prefix + "_" + file.getName();
        String ext = getExtension(file);
        String par = file.getParent();
        File bkup = new File(par, name + ext);
        for (int i = 0; bkup.exists(); bkup = new File(par, name + "_" + i++ + ext)) {
        }
        MobSiege.LOGGER.warn("Creating backup at: " + bkup.getAbsolutePath());
        copyTo(file, bkup);
    }

    public static String getExtension(File file) {
        String fullName = file.getAbsolutePath();
        int idx = fullName.lastIndexOf(46);
        return idx >= 0 ? fullName.substring(idx) : "";
    }
}
