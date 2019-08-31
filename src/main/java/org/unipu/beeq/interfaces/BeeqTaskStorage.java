package org.unipu.beeq.interfaces;

import org.unipu.beeq.BeeqTaskPoolSegment;
import org.unipu.beeq.BeeqTaskWrapper;

import java.util.List;

public interface BeeqTaskStorage<T> {
    void addTask(BeeqTaskWrapper<T> task, BeeqTaskPoolSegment<T> taskPoolSegment);

    BeeqTaskWrapper<T> getTask(BeeqTaskPoolSegment<T> taskPoolSegment);

    List<BeeqTaskWrapper<T>> getNextBatch();

    void addTaskPoolSegment(BeeqTaskPoolSegment<T> taskPoolSegment);

    List<BeeqTaskWrapper<T>> removeTaskPoolSegment(BeeqTaskPoolSegment<T> taskPoolSegment);

    void reorganizeTasks(BeeqTaskPoolSegment<T> taskPoolSegment);

    boolean isEmpty();

    List<BeeqTaskWrapper<T>> initTasks();

    List<BeeqTaskPoolSegment<T>> initSegments();

    void persistAllTasks();

    boolean isReadyForNextBatch();
}
