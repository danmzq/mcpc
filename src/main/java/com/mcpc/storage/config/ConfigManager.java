package com.mcpc.storage.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path path;
    private StorageConfig config = new StorageConfig();

    public ConfigManager(String filename) {
        this.path = FabricLoader.getInstance().getConfigDir().resolve(filename);
    }

    public void load() {
        if (!Files.exists(path)) {
            save();
            return;
        }
        try {
            config = gson.fromJson(Files.readString(path), StorageConfig.class);
            if (config == null) config = new StorageConfig();
        } catch (IOException e) {
            throw new RuntimeException("Failed reading storage config", e);
        }
    }

    public void save() {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, gson.toJson(config));
        } catch (IOException e) {
            throw new RuntimeException("Failed saving storage config", e);
        }
    }

    public StorageConfig config() {
        return config;
    }

    public Path path() {
        return path;
    }
}
