package org.unipu.beeq.interfaces;

import org.unipu.beeq.BeeqTaskWrapper;

public interface BeeqTaskProcessor<T, R> {
    public R process(BeeqTaskWrapper<T> task) throws Exception;
}
