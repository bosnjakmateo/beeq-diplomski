package org.unipu.beeq.impl;

import org.unipu.beeq.BeeqTaskPoolSegment;
import org.unipu.beeq.BeeqTaskWrapper;
import org.unipu.beeq.interfaces.BeeqTaskStorage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class InMemoryBeeqTaskStorage<T> implements BeeqTaskStorage<T> {

    private Map<BeeqTaskPoolSegment<T>, List<BeeqTaskWrapper<T>>> tasks;
    private AtomicInteger taskCount = new AtomicInteger(0);

    public InMemoryBeeqTaskStorage() {
        this.tasks = new HashMap<>();
    }

    @Override
    public void addTask(BeeqTaskWrapper<T> task, BeeqTaskPoolSegment<T> taskPoolSegment) {
        tasks.computeIfAbsent(taskPoolSegment, v -> new LinkedList<>()).add(task);
        taskCount.incrementAndGet();
    }

    @Override
    public BeeqTaskWrapper<T> getTask(BeeqTaskPoolSegment<T> taskPoolSegment) {
        if (tasks.get(taskPoolSegment) == null || tasks.get(taskPoolSegment).isEmpty())
            return null;

        BeeqTaskWrapper<T> task = tasks.get(taskPoolSegment).get(0);

        if (task.getTryAt().isAfter(LocalDateTime.now()))
            return null;

        taskCount.decrementAndGet();
        tasks.get(taskPoolSegment).remove(task);

        return task;
    }

    @Override
    public void addTaskPoolSegment(BeeqTaskPoolSegment<T> taskPoolSegment) {
        reorganizeTasks(taskPoolSegment);
    }

    @Override
    public List<BeeqTaskWrapper<T>> removeTaskPoolSegment(BeeqTaskPoolSegment<T> taskPoolSegment) {
        return tasks.remove(taskPoolSegment);
    }

    @Override
    public void reorganizeTasks(BeeqTaskPoolSegment<T> taskPoolSegment) {
        tasks.forEach((key, value) -> value
                .forEach(task -> {
                    if (taskPoolSegment.getTaskDiscriminator().doesMatch(task.getTaskDescriptor())) {
                        value.remove(task);
                        this.addTask(task, taskPoolSegment);
                    }
                }));
    }

    @Override
    public boolean isEmpty() {
        return taskCount.intValue() <= 0;
    }

    @Override
    public List<BeeqTaskWrapper<T>> getNextBatch() {
        return null;
    }

    @Override
    public boolean isReadyForNextBatch() {
        return true;
    }

    @Override
    public List<BeeqTaskWrapper<T>> initTasks() {
        return null;
    }

    @Override
    public List<BeeqTaskPoolSegment<T>> initSegments() {
        return null;
    }

    @Override
    public void persistAllTasks() {
    }
}
