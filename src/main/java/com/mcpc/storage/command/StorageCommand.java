package com.mcpc.storage.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mcpc.storage.automation.AutomationLogic;
import com.mcpc.storage.chest.ChestRegistry;
import com.mcpc.storage.config.ConfigManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class StorageCommand {
    public static void register(ChestRegistry registry, AutomationLogic automation, ConfigManager cfg) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("storage")
                        .then(CommandManager.literal("setsource")
                                .then(CommandManager.argument("id", StringArgumentType.word())
                                        .executes(ctx -> {
                                            return setSource(ctx.getSource(), registry, StringArgumentType.getString(ctx, "id"));
                                        })))
                        .then(CommandManager.literal("setdest")
                                .then(CommandManager.argument("id", StringArgumentType.word())
                                        .executes(ctx -> {
                                            return setDest(ctx.getSource(), registry, StringArgumentType.getString(ctx, "id"));
                                        })))
                        .then(CommandManager.literal("additem")
                                .then(CommandManager.argument("destId", StringArgumentType.word())
                                        .then(CommandManager.argument("item", StringArgumentType.word())
                                                .executes(ctx -> {
                                                    String destId = StringArgumentType.getString(ctx, "destId");
                                                    String item = StringArgumentType.getString(ctx, "item");
                                                    boolean ok = registry.addAllowedItem(destId, item);
                                                    ctx.getSource().sendFeedback(() -> Text.literal(ok ? "Item ditambahkan" : "Destination tidak ditemukan"), false);
                                                    return ok ? 1 : 0;
                                                })))))
                        .then(CommandManager.literal("scan")
                                .then(CommandManager.argument("radius", IntegerArgumentType.integer(1, 256))
                                        .executes(ctx -> {
                                            int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                            var player = ctx.getSource().getPlayerOrThrow();
                                            var result = automation.scan(player.getServerWorld(), player.getBlockPos(), radius);
                                            ctx.getSource().sendFeedback(() -> Text.literal("Scan chest: " + result.size()), false);
                                            return result.size();
                                        })))
                        .then(CommandManager.literal("exportscan")
                                .executes(ctx -> {
                                    var player = ctx.getSource().getPlayerOrThrow();
                                    var result = automation.scan(player.getServerWorld(), player.getBlockPos(), 64);
                                    automation.exportScanJson(player.getServerWorld(), result);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Export scan selesai"), false);
                                    return 1;
                                }))
                        .then(CommandManager.literal("runonce")
                                .executes(ctx -> {
                                    var player = ctx.getSource().getPlayerOrThrow();
                                    automation.planTasks(player.getServerWorld());
                                    var task = automation.runOnce(player);
                                    ctx.getSource().sendFeedback(() -> Text.literal(task.isPresent() ? "Task dijalankan" : "Task kosong"), false);
                                    return task.isPresent() ? 1 : 0;
                                }))
        ));
    }

    private static int setSource(ServerCommandSource src, ChestRegistry registry, String id) {
        BlockPos pos = src.getPlayerOrThrow().getBlockPos();
        registry.registerSource(id, pos);
        src.sendFeedback(() -> Text.literal("Source tersimpan: " + id), false);
        return 1;
    }

    private static int setDest(ServerCommandSource src, ChestRegistry registry, String id) {
        BlockPos pos = src.getPlayerOrThrow().getBlockPos();
        registry.registerDestination(id, pos);
        src.sendFeedback(() -> Text.literal("Destination tersimpan: " + id), false);
        return 1;
    }
}
