package com.mcpc.storage.chest;

import com.mcpc.storage.config.ConfigManager;
import com.mcpc.storage.config.StorageConfig;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ChestRegistry {
    private final ConfigManager configManager;

    public ChestRegistry(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public boolean registerSource(String id, BlockPos pos) {
        StorageConfig cfg = configManager.config();
        cfg.sources.removeIf(s -> s.id.equals(id));
        StorageConfig.SourceChest s = new StorageConfig.SourceChest();
        s.id = id;
        s.pos = new int[]{pos.getX(), pos.getY(), pos.getZ()};
        cfg.sources.add(s);
        configManager.save();
        return true;
    }

    public boolean registerDestination(String id, BlockPos pos) {
        StorageConfig cfg = configManager.config();
        cfg.destinations.removeIf(d -> d.id.equals(id));
        StorageConfig.DestinationChest d = new StorageConfig.DestinationChest();
        d.id = id;
        d.pos = new int[]{pos.getX(), pos.getY(), pos.getZ()};
        cfg.destinations.add(d);
        configManager.save();
        return true;
    }

    public boolean addAllowedItem(String destId, String itemId) {
        Optional<StorageConfig.DestinationChest> target = configManager.config().destinations.stream()
                .filter(d -> d.id.equals(destId)).findFirst();
        if (target.isEmpty()) return false;
        if (!target.get().items.contains(itemId)) {
            target.get().items.add(itemId);
            configManager.save();
        }
        return true;
    }

    public Map<String, Set<String>> destinationWhitelist() {
        Map<String, Set<String>> map = new HashMap<>();
        for (StorageConfig.DestinationChest d : configManager.config().destinations) {
            map.put(d.id, new HashSet<>(d.items));
        }
        return map;
    }

    public BlockPos sourcePos(String id) {
        for (StorageConfig.SourceChest source : configManager.config().sources) {
            if (source.id.equals(id)) return new BlockPos(source.pos[0], source.pos[1], source.pos[2]);
        }
        return null;
    }

    public BlockPos destinationPos(String id) {
        for (StorageConfig.DestinationChest d : configManager.config().destinations) {
            if (d.id.equals(id)) return new BlockPos(d.pos[0], d.pos[1], d.pos[2]);
        }
        return null;
    }
}
