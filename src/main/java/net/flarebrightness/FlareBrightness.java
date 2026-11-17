package net.flarebrightness;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.flarebrightness.screen.FlareBrightnessScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * FlareBrightness - client-only brightness mod.
 * Level range: -500 (pitch dark) .. 0 (vanilla) .. 1500 (max).
 */
public final class FlareBrightness implements ClientModInitializer {
    public static final String MOD_ID = "flarebrightness";
    public static final int MIN_LEVEL = -500;
    public static final int MAX_LEVEL = 1500;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Active level. Plain int read each lightmap update: zero allocation, near-zero cost. */
    private static volatile int level = 0;

    private static KeyMapping openKey;

    @Override
    public void onInitializeClient() {
        load();

        // 66 = GLFW_KEY_B. Rebindable in Options > Controls.
        openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.flarebrightness.open", 66,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "main"))));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.consumeClick()) {
                if (client.screen == null) {
                    client.gui.setScreen(new FlareBrightnessScreen(null));
                }
            }
        });
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int newLevel) {
        level = Math.max(MIN_LEVEL, Math.min(MAX_LEVEL, newLevel));
    }

    /** Gamma value handed to the lightmap. 100 level units = 1.0 gamma (like 100% Bright). */
    public static double gammaForLevel() {
        return level / 100.0;
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("flarebrightness.json");
    }

    public static void load() {
        try {
            Path p = configPath();
            if (!Files.isRegularFile(p)) return;
            JsonObject o = GSON.fromJson(Files.readString(p, StandardCharsets.UTF_8), JsonObject.class);
            if (o != null && o.has("level") && o.get("level").isJsonPrimitive()) {
                setLevel(o.get("level").getAsInt());
            }
        } catch (Exception e) {
            System.err.println("[FlareBrightness] Could not read config, using defaults: " + e);
            level = 0;
        }
    }

    public static void save() {
        try {
            Path p = configPath();
            Files.createDirectories(p.getParent());
            JsonObject o = new JsonObject();
            o.addProperty("level", level);
            Files.writeString(p, GSON.toJson(o), StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.err.println("[FlareBrightness] Could not save config: " + e);
        }
    }
}
