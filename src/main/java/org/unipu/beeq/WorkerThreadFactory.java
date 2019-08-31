package org.unipu.beeq;

import java.util.concurrent.ThreadFactory;

public class WorkerThreadFactory implements ThreadFactory {

    private String name;
    private int counter;

    public WorkerThreadFactory(String name){
        this.name = name;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        counter++;

        return new Thread(runnable, name + counter);
    }
}
