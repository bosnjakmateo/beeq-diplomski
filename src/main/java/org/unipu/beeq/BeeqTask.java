package org.unipu.beeq;

import java.time.LocalDateTime;

public class BeeqTask<T> {

    private T taskDescriptor;

    private LocalDateTime executeAt;

    private LocalDateTime executeBefore;

    public BeeqTask(){}

    public BeeqTask(T taskDescriptor) {
        this(taskDescriptor, LocalDateTime.now());
    }

    public BeeqTask(T taskDescriptor, LocalDateTime executeAt) {
        this.taskDescriptor = taskDescriptor;
        this.executeAt = executeAt;
        this.executeBefore = LocalDateTime.now().plusYears(1000);
    }

    public BeeqTask(T taskDescriptor, LocalDateTime executeAt, LocalDateTime executeBefore) {
        this.taskDescriptor = taskDescriptor;
        this.executeAt = executeAt;
        this.executeBefore = executeBefore;
    }

    public T getTaskDescriptor(){
        return taskDescriptor;
    }

    public LocalDateTime getExecuteAt() {
        return executeAt;
    }

    public LocalDateTime getExecuteBefore() {
        return executeBefore;
    }

    public void setTaskDescriptor(T taskDescriptor) {
        this.taskDescriptor = taskDescriptor;
    }

    public void setExecuteAt(LocalDateTime executeAt) {
        this.executeAt = executeAt;
    }

    public void setExecuteBefore(LocalDateTime executeBefore) {
        this.executeBefore = executeBefore;
    }
}
