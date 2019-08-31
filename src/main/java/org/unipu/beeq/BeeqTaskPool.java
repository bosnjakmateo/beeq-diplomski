package org.unipu.beeq;

import org.unipu.beeq.interfaces.BeeqTaskStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BeeqTaskPool<T> {
    private BeeqTaskStorage<T> taskStorage;
    private List<BeeqTaskPoolSegment<T>> taskPoolSegmentList;
    private BeeqCircularList<T> circularList;

    private BeeqTaskPoolSegment<T> defaultTaskPoolSegment;

    private static final Object LOCK = new Object();


    public BeeqTaskPool(BeeqTaskStorage<T> taskStorage) {
        this.taskStorage = taskStorage;
        this.taskPoolSegmentList = new ArrayList<>();
        this.circularList = new BeeqCircularList<>();

        createDefaultSegment();

        // Segments first, because tasks need segments to be organized
//        this.initSegments();
//        this.initTasks();
    }

    BeeqTaskWrapper<T> getNextTask() {
        synchronized (LOCK) {
            if (taskStorage.isEmpty()) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            BeeqTaskWrapper<T> taskWrapper = taskStorage.getTask(circularList.getNextPoolSegment());

            if (taskWrapper == null) {
                taskWrapper = getNextTask();
            }

            return taskWrapper;
        }
    }

    void addTask(BeeqTaskWrapper<T> task) {
        BeeqTaskPoolSegment<T> segmentPool = findTaskPoolSegment(task);

        synchronized (LOCK) {
            taskStorage.addTask(task, segmentPool);
            LOCK.notifyAll();
        }
    }

    private BeeqTaskPoolSegment<T> findTaskPoolSegment(BeeqTaskWrapper<T> task) {
        Optional<BeeqTaskPoolSegment<T>> segmentPool = taskPoolSegmentList.stream()
                .filter(segment -> segment.getTaskDiscriminator().doesMatch(task.getTaskDescriptor()))
                .findFirst();

        return segmentPool.orElseGet(() -> defaultTaskPoolSegment);
    }

    void createSegment(BeeqTaskPoolSegment<T> taskPoolSegment) {
        if (defaultTaskPoolSegment.getBatchSize() > taskPoolSegment.getBatchSize()) {
            defaultTaskPoolSegment.setBatchSize(taskPoolSegment.getBatchSize());
        }

        // We want the default task pool segment to be last
        taskPoolSegmentList.add(0, taskPoolSegment);
        circularList.addTaskPoolSegment(taskPoolSegment);
        taskStorage.addTaskPoolSegment(taskPoolSegment);
    }

    void removeSegment(BeeqTaskPoolSegment<T> taskPoolSegment) {
        circularList.removeTaskPoolSegment(taskPoolSegment);
        taskPoolSegmentList.remove(taskPoolSegment);
        taskStorage.removeTaskPoolSegment(taskPoolSegment).forEach(this::addTask);
    }

    BeeqRetryPolicy getRetryPolicy(BeeqTaskWrapper<T> task) {
        Optional<BeeqTaskPoolSegment<T>> taskPoolSegment = taskPoolSegmentList.stream()
                .filter(segment -> segment.getTaskDiscriminator().doesMatch(task.getTaskDescriptor()))
                .findFirst();

        if (taskPoolSegment.isPresent()) {
            return taskPoolSegment.get().getRetryPolicy();
        } else {
            return defaultTaskPoolSegment.getRetryPolicy();
        }
    }

    void saveTasksToDb() {
        taskStorage.persistAllTasks();
    }

    private void createDefaultSegment() {
        this.defaultTaskPoolSegment = new BeeqTaskPoolSegment<>(
                "Default task pool segment",
                (task) -> true,
                Integer.MAX_VALUE,
                new BeeqRetryPolicy()
        );

        taskPoolSegmentList.add(defaultTaskPoolSegment);
        circularList.addTaskPoolSegment(defaultTaskPoolSegment);
    }

    private void getNextBatch() {
        List<BeeqTaskWrapper<T>> taskWrapperList = taskStorage.getNextBatch();
        taskWrapperList.forEach(this::addTask);
    }

    private void initTasks() {
        taskStorage.initTasks().forEach(this::addTask);
    }

    private void initSegments() {
        taskStorage.initSegments().forEach(this::createSegment);
    }
}
