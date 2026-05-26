package com.mcpc.storage;

import com.mcpc.storage.automation.AutomationLogic;
import com.mcpc.storage.baritone.BaritoneBridge;
import com.mcpc.storage.chest.ChestRegistry;
import com.mcpc.storage.command.StorageCommand;
import com.mcpc.storage.config.ConfigManager;
import com.mcpc.storage.task.TaskQueue;
import net.fabricmc.api.ModInitializer;

public class StorageAutomationMod implements ModInitializer {
    public static final String MOD_ID = "mcpc_storage";

    private static ConfigManager configManager;
    private static ChestRegistry chestRegistry;
    private static TaskQueue taskQueue;
    private static BaritoneBridge baritoneBridge;
    private static AutomationLogic automationLogic;

    @Override
    public void onInitialize() {
        configManager = new ConfigManager(MOD_ID + ".json");
        configManager.load();

        chestRegistry = new ChestRegistry(configManager);
        taskQueue = new TaskQueue();
        baritoneBridge = new BaritoneBridge();
        automationLogic = new AutomationLogic(chestRegistry, taskQueue, baritoneBridge, configManager);

        StorageCommand.register(chestRegistry, automationLogic, configManager);
    }

    public static AutomationLogic automationLogic() {
        return automationLogic;
    }
}
