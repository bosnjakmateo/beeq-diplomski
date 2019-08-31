package org.unipu.beeq.interfaces;

import org.unipu.beeq.BeeqTaskWrapper;

public interface BeeqOnTaskCompletionHandler<T, R> {
    public void onTaskCompleted(BeeqTaskWrapper<T> completedTask, R completionResult);
    public void onTaskFailed(BeeqTaskWrapper<T> failedTask, int retryNumber, Exception error);
    public void onTaskDropped(BeeqTaskWrapper<T> failedTask, String message);
}
