package stubborn.beaconui.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import static stubborn.beaconui.ModernBeaconGUI.LOGGER;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BeaconUIConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("beaconui.json");
    private static BeaconUIConfig INSTANCE;
    // Default values
    public static boolean DEFAULT_PATCH_QUICK_MOVE = true;
    // Config
    public boolean patchQuickMove = DEFAULT_PATCH_QUICK_MOVE ;

    public static BeaconUIConfig getInstance() {
        if (INSTANCE == null) {
            load();
        }
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                INSTANCE = GSON.fromJson(json, BeaconUIConfig.class);

                if (INSTANCE == null) {
                    INSTANCE = new BeaconUIConfig();
                }

                INSTANCE.save();

            } catch (IOException | JsonParseException e) {
                LOGGER.error("Errors when trying to load config: ", e);
                INSTANCE = new BeaconUIConfig();
            }
        } else {
            INSTANCE = new BeaconUIConfig();
            INSTANCE.save();
        }
    }

    public void save() {

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            LOGGER.error("Errors when trying to save config: ", e);
        }
    }
}

