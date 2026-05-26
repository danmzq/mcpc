package com.mcpc.storage.task;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class TaskQueue {
    private final Queue<TransferTask> queue = new ArrayDeque<>();

    public void add(TransferTask task) { queue.offer(task); }
    public Optional<TransferTask> poll() { return Optional.ofNullable(queue.poll()); }
    public int size() { return queue.size(); }
}
