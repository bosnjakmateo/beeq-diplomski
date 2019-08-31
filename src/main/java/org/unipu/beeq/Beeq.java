package org.unipu.beeq;

import org.unipu.beeq.impl.InMemoryBeeqTaskStorage;
import org.unipu.beeq.interfaces.BeeqOnTaskCompletionHandler;
import org.unipu.beeq.interfaces.BeeqTaskProcessor;
import org.unipu.beeq.interfaces.BeeqTaskStorage;

import java.time.LocalDateTime;
import java.util.concurrent.*;

public class Beeq<T, R> {

    private BeeqTaskPool<T> taskPool;
    private BeeqTaskProcessor<T, R> taskProcessor;
    private BeeqOnTaskCompletionHandler<T, R> onTaskCompletionHandler;

    private WorkerThreadFactory workerThreadFactory = new WorkerThreadFactory("Beeq-Worker-");
    private ExecutorService workerExecutor;
    private Semaphore semaphore;

    boolean isWorking = true;


    public Beeq(BeeqTaskStorage<T> taskStorage, BeeqTaskProcessor<T, R> taskProcessor, BeeqOnTaskCompletionHandler<T, R> onTaskCompletionHandler, int workerCount) {
        this.taskProcessor = taskProcessor;
        this.onTaskCompletionHandler = onTaskCompletionHandler;
        this.taskPool = new BeeqTaskPool<>(taskStorage);
        this.workerExecutor = Executors.newFixedThreadPool(workerCount, workerThreadFactory);
        this.semaphore = new Semaphore(workerCount);

        initMainThread();
//        initPersistenceThread();
    }

    public Beeq(BeeqTaskProcessor<T, R> taskProcessor, BeeqOnTaskCompletionHandler<T, R> onTaskCompletionHandler, int workerCount) {
        this(new InMemoryBeeqTaskStorage<>(), taskProcessor, onTaskCompletionHandler, workerCount);
    }

    private void initMainThread() {
        Thread beeqMainThread = new Thread(() -> {
            while (isWorking) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (workerExecutor.isShutdown() || workerExecutor.isTerminated() || !isWorking) {
                    break;
                }

                BeeqTaskWrapper<T> task = taskPool.getNextTask();

                workerExecutor.execute(() -> {
                    try {
                        if (task.shouldBeDropped()) {
                            onTaskCompletionHandler.onTaskDropped(task, "Time of execution has passed");
                            semaphore.release();
                            return;
                        }

                        R r = taskProcessor.process(task);
                        onTaskCompletionHandler.onTaskCompleted(task, r);

                        semaphore.release();

                    } catch (Exception e) {
                        task.incrementRetryCount();

                        onTaskCompletionHandler.onTaskFailed(task, task.getRetryCount(), e);

                        BeeqRetryPolicy retryPolicy = taskPool.getRetryPolicy(task);

                        int retryDelay = retryPolicy.getNextDelayInSeconds(task.getRetryCount());

                        if (retryDelay == -1) {
                            onTaskCompletionHandler.onTaskDropped(task, "Maximum number of retries reached");
                        } else {
                            task.setTryAt(LocalDateTime.now().plusSeconds(retryDelay));
                            taskPool.addTask(task);
                        }

                        semaphore.release();
                    }
                });
            }
        });

        beeqMainThread.setName("Beeq-main-thread");
        beeqMainThread.start();
    }

    public void shutDown() {
        System.out.println("Shut down started...");

        isWorking = false;

        workerExecutor.shutdown();

        try {
            workerExecutor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        taskPool.saveTasksToDb();

        System.out.println("Shut down completed");
    }

    public void submitTask(BeeqTaskWrapper<T> task) {
        taskPool.addTask(task);
    }

    public void submitTask(BeeqTask<T> task) {
        taskPool.addTask(new BeeqTaskWrapper<>(task));
    }

    public void submitTask(T task) {
        taskPool.addTask(new BeeqTaskWrapper<>(new BeeqTask<>(task)));
    }

    public void createSegment(BeeqTaskPoolSegment<T> taskPoolSegment) {
        taskPool.createSegment(taskPoolSegment);
    }

    public void deleteSegment(BeeqTaskPoolSegment<T> taskPoolSegment) {
        taskPool.removeSegment(taskPoolSegment);
    }
}
