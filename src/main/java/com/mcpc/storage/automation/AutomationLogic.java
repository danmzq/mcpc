package com.mcpc.storage.automation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcpc.storage.baritone.BaritoneBridge;
import com.mcpc.storage.chest.ChestRegistry;
import com.mcpc.storage.chest.ChestScanner;
import com.mcpc.storage.chest.InventoryAnalyzer;
import com.mcpc.storage.config.ConfigManager;
import com.mcpc.storage.task.TaskQueue;
import com.mcpc.storage.task.TransferTask;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class AutomationLogic {
    private final ChestRegistry registry;
    private final TaskQueue queue;
    private final BaritoneBridge baritone;
    private final ConfigManager config;
    private final ChestScanner scanner = new ChestScanner();
    private final InventoryAnalyzer analyzer = new InventoryAnalyzer();

    public AutomationLogic(ChestRegistry registry, TaskQueue queue, BaritoneBridge baritone, ConfigManager config) {
        this.registry = registry;
        this.queue = queue;
        this.baritone = baritone;
        this.config = config;
    }

    public Set<BlockPos> scan(ServerWorld world, BlockPos center, int radius) {
        return scanner.scan(world, center, radius);
    }

    public void planTasks(ServerWorld world) {
        Map<String, Set<String>> whitelist = registry.destinationWhitelist();
        for (var src : config.config().sources) {
            BlockPos srcPos = new BlockPos(src.pos[0], src.pos[1], src.pos[2]);
            BlockEntity be = world.getBlockEntity(srcPos);
            if (!(be instanceof Inventory inv)) continue;

            Map<String, Integer> items = analyzer.countItems(inv);
            for (Map.Entry<String, Integer> e : items.entrySet()) {
                String itemId = e.getKey();
                int count = e.getValue();
                String destinationId = chooseDestination(itemId, whitelist);
                if (destinationId != null && count > 0) {
                    queue.add(new TransferTask(src.id, destinationId, itemId, count));
                }
            }
        }
    }

    private String chooseDestination(String itemId, Map<String, Set<String>> whitelist) {
        for (var entry : whitelist.entrySet()) {
            if (entry.getValue().contains(itemId)) return entry.getKey();
        }
        return null;
    }

    public Optional<TransferTask> runOnce(ServerPlayerEntity player) {
        Optional<TransferTask> taskOpt = queue.poll();
        taskOpt.ifPresent(task -> {
            baritone.gotoPos(player, registry.sourcePos(task.sourceId()));
            // TODO: ambil item dari source inventory.
            baritone.gotoPos(player, registry.destinationPos(task.destinationId()));
            // TODO: deposit item ke destination inventory.
        });
        return taskOpt;
    }

    public void exportScanJson(ServerWorld world, Set<BlockPos> scanResult) {
        List<Map<String, Object>> payload = new ArrayList<>();
        for (BlockPos pos : scanResult) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof ChestBlockEntity chest)) continue;
            Map<String, Integer> items = analyzer.countItems((Inventory) chest);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("pos", List.of(pos.getX(), pos.getY(), pos.getZ()));
            row.put("items", items);
            row.put("unique_items", items.size());
            row.put("total_count", items.values().stream().mapToInt(Integer::intValue).sum());
            payload.add(row);
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.writeString(config.path().resolveSibling("mcpc_storage_scan.json"), gson.toJson(payload));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
