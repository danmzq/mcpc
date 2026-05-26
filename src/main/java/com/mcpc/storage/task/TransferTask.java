package com.mcpc.storage.task;

public record TransferTask(String sourceId, String destinationId, String itemId, int count) {}
