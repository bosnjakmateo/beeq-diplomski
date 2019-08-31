package org.unipu.beeq.impl;

import org.unipu.beeq.BeeqTaskWrapper;
import org.unipu.beeq.interfaces.BeeqTaskProcessor;
import org.unipu.beeq.models.NumberGenerator;

import java.util.Random;

public class NumberGeneratorProcessor<T, R> implements BeeqTaskProcessor<T, R> {

    @Override
    public R process(BeeqTaskWrapper<T> task) throws Exception {
        NumberGenerator ng = (NumberGenerator) task.getTaskDescriptor();
        Random random = new Random();

        Integer generatedNumber = random.nextInt(ng.getMax() - ng.getMin() + 1) + ng.getMin();

        if (generatedNumber % 2 != 0) {
            throw new Exception("Generated number can't be odd. Number: " + generatedNumber);
        }

        return (R) generatedNumber;
    }
}
