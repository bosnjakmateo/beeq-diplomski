package org.unipu.beeq.impl;

import org.unipu.beeq.BeeqTaskWrapper;
import org.unipu.beeq.interfaces.BeeqOnTaskCompletionHandler;

public class OnNumberGeneratorCompletionHandler<T, R> implements BeeqOnTaskCompletionHandler<T, R> {
    @Override
    public void onTaskCompleted(BeeqTaskWrapper<T> completedTask, R completionResult) {
        System.out.println(Thread.currentThread().getName() + " Generated number " + completionResult);
    }

    @Override
    public void onTaskFailed(BeeqTaskWrapper<T> failedTask, int retryNumber, Exception error) {
        System.out.println(Thread.currentThread().getName() + " Task " + failedTask.getTaskDescriptor() +
                " failed " + retryNumber + " time(s), with error " + error.getMessage());
    }

    @Override
    public void onTaskDropped(BeeqTaskWrapper<T> failedTask, String message) {
        System.out.println(Thread.currentThread().getName() + " Task" + failedTask.getTaskDescriptor() +
                " was dropped. Reason: " + message);
    }
}
