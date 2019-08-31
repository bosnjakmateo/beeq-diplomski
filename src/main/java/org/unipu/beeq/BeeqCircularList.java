package org.unipu.beeq;

import java.util.ArrayList;
import java.util.List;

public class BeeqCircularList<T> {
    private List<BeeqTaskPoolSegment<T>> taskPoolSegmentList;
    private BeeqTaskPoolSegment<T> currentTaskPoolSegment;
    private int index = 0;
    private int batchSize;
    private int batchIndex = 0;

    public BeeqCircularList() {
        this.taskPoolSegmentList = new ArrayList<>();
    }

    public void addTaskPoolSegment(BeeqTaskPoolSegment<T> taskPoolSegment){
        // We want the default task pool segment to be last
        taskPoolSegmentList.add(0, taskPoolSegment);
        this.setCurrentData();
    }

    public void removeTaskPoolSegment(BeeqTaskPoolSegment<T> taskPoolSegment){
        taskPoolSegmentList.remove(taskPoolSegment);
        this.setCurrentData();
    }

    public BeeqTaskPoolSegment<T> getNextPoolSegment(){
        if(batchIndex == batchSize){
            index = (index + 1) % taskPoolSegmentList.size();
            setCurrentData();
            batchIndex = 1;
            return currentTaskPoolSegment;
        }

        batchIndex++;

        return currentTaskPoolSegment;
    }

    private void setCurrentData(){
        currentTaskPoolSegment = taskPoolSegmentList.get(index);
        batchSize = currentTaskPoolSegment.getBatchSize();
    }
}
