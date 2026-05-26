package com.mcpc.storage.baritone;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class BaritoneBridge {
    public void gotoPos(ServerPlayerEntity player, BlockPos pos) {
        // Integrasi real (opsional tergantung environment):
        // BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalBlock(pos));
        // Placeholder agar automation logic dapat dipanggil tanpa hard dependency runtime.
    }
}
