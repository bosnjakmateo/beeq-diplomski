package org.unipu.beeq;

import java.time.LocalDateTime;
import java.util.UUID;

public class BeeqTaskWrapper<T> {
    private UUID uuid;
    private BeeqTask<T> beeqTask;
    private LocalDateTime tryAt;
    private int retryCount;

    public BeeqTaskWrapper(){}

    public BeeqTaskWrapper(BeeqTask<T> beeqTask) {
        this.beeqTask = beeqTask;
        this.tryAt = beeqTask.getExecuteAt();
        this.retryCount = 0;
        this.uuid = UUID.nameUUIDFromBytes(beeqTask.toString().getBytes());
    }

    public BeeqTaskWrapper(BeeqTask<T> beeqTask, int retryCount) {
        this.beeqTask = beeqTask;
        this.tryAt = beeqTask.getExecuteAt();
        this.retryCount = retryCount;
        this.uuid = UUID.nameUUIDFromBytes(beeqTask.toString().getBytes());
    }

    public BeeqTaskWrapper(BeeqTask<T> beeqTask, int retryCount, UUID uuid) {
        this.beeqTask = beeqTask;
        this.tryAt = beeqTask.getExecuteAt();
        this.retryCount = retryCount;
        this.uuid = uuid;
    }

    public boolean shouldBeDropped(){
        return LocalDateTime.now().isAfter(beeqTask.getExecuteBefore());
    }

    public boolean isScheduledForNow() {
        return beeqTask.getExecuteAt().isBefore(LocalDateTime.now());
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void incrementRetryCount() {
        retryCount++;
    }

    public LocalDateTime getTryAt() {
        return tryAt;
    }

    public void setTryAt(LocalDateTime tryAt) {
        this.tryAt = tryAt;
    }

    public BeeqTask<T> getBeeqTask() {
        return beeqTask;
    }

    public void setBeeqTask(BeeqTask<T> beeqTask) {
        this.beeqTask = beeqTask;
    }

    public T getTaskDescriptor(){
        return beeqTask.getTaskDescriptor();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
