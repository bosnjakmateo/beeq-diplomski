package org.unipu.beeq;

import java.util.ArrayList;
import java.util.List;

public class BeeqRetryPolicy {
    private List<Integer> retryPolicyList = new ArrayList<>();

    public BeeqRetryPolicy() {
        //TODO default retry policy
    }

    public BeeqRetryPolicy(List<Integer> delaysInSeconds) {
        if(delaysInSeconds == null) throw new NullPointerException();

        this.retryPolicyList = delaysInSeconds;
    }

    /**
     *
     * @param retryNumber
     * @return delay in seconds, -1 means that the task should not be retried
     */
    public int getNextDelayInSeconds(int retryNumber){
        if(retryNumber > retryPolicyList.size() - 1) return -1;
        return retryPolicyList.get(retryNumber-1);
    }
}
