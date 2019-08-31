package org.unipu.beeq;

import org.unipu.beeq.interfaces.BeeqTaskDiscriminator;

import java.util.UUID;

public class BeeqTaskPoolSegment<T> {
    private UUID uuid;
    private String name;
    private BeeqTaskDiscriminator<T> taskDiscriminator;

    private int batchSize;

    private BeeqRetryPolicy retryPolicy;

    public BeeqTaskPoolSegment(String name, BeeqTaskDiscriminator<T> taskDiscriminator, int batchSize, BeeqRetryPolicy retryPolicy) {
        this.name = name;
        this.taskDiscriminator = taskDiscriminator;
        this.batchSize = batchSize;
        this.retryPolicy = retryPolicy;
        this.uuid = UUID.nameUUIDFromBytes(this.toString().getBytes());
    }

    public BeeqTaskPoolSegment(String name, BeeqTaskDiscriminator<T> taskDiscriminator, int batchSize, BeeqRetryPolicy retryPolicy, UUID uuid) {
        this.name = name;
        this.taskDiscriminator = taskDiscriminator;
        this.batchSize = batchSize;
        this.retryPolicy = retryPolicy;
        this.uuid = uuid;
    }

    public BeeqTaskDiscriminator<T> getTaskDiscriminator() {
        return taskDiscriminator;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public BeeqRetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }
}
