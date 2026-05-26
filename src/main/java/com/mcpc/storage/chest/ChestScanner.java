package com.mcpc.storage.chest;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChestScanner {
    public Set<BlockPos> scan(ServerWorld world, BlockPos center, int radius) {
        Set<BlockPos> uniqueChestRoots = new HashSet<>();

        int minChunkX = (center.getX() - radius) >> 4;
        int maxChunkX = (center.getX() + radius) >> 4;
        int minChunkZ = (center.getZ() - radius) >> 4;
        int maxChunkZ = (center.getZ() + radius) >> 4;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                if (!world.isChunkLoaded(chunkX, chunkZ)) {
                    continue;
                }

                WorldChunk chunk = world.getChunk(chunkX, chunkZ);
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (!(be instanceof ChestBlockEntity chest)) continue;

                    BlockPos pos = chest.getPos();
                    if (pos.getSquaredDistance(center) > (long) radius * radius) continue;

                    BlockPos root = findDoubleChestRoot(world, pos);
                    uniqueChestRoots.add(root);
                }
            }
        }

        return uniqueChestRoots;
    }

    private BlockPos findDoubleChestRoot(ServerWorld world, BlockPos pos) {
        List<BlockPos> candidates = new ArrayList<>();
        candidates.add(pos);
        for (BlockPos neighbor : List.of(pos.north(), pos.south(), pos.east(), pos.west())) {
            BlockEntity be = world.getBlockEntity(neighbor);
            if (be instanceof ChestBlockEntity) {
                candidates.add(neighbor);
            }
        }
        candidates.sort(Comparator.comparingInt(BlockPos::getX)
                .thenComparingInt(BlockPos::getY)
                .thenComparingInt(BlockPos::getZ));
        return candidates.get(0);
    }
}
