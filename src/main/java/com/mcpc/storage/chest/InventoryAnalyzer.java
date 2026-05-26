package com.mcpc.storage.chest;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.HashMap;
import java.util.Map;

public class InventoryAnalyzer {
    public Map<String, Integer> countItems(Inventory inventory) {
        Map<String, Integer> out = new HashMap<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) continue;
            Identifier id = Registries.ITEM.getId(stack.getItem());
            out.merge(id.toString(), stack.getCount(), Integer::sum);
        }
        return out;
    }
}
