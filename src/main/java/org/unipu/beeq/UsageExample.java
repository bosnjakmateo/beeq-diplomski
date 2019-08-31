package org.unipu.beeq;

import org.unipu.beeq.impl.InMemoryBeeqTaskStorage;
import org.unipu.beeq.impl.NumberGeneratorProcessor;
import org.unipu.beeq.impl.OnNumberGeneratorCompletionHandler;
import org.unipu.beeq.models.NumberGenerator;

import java.util.Collections;

public class UsageExample {
    public static void main(String[] args) {
        NumberGeneratorProcessor<NumberGenerator, Integer> processor = new NumberGeneratorProcessor<>();
        InMemoryBeeqTaskStorage<NumberGenerator> storage = new InMemoryBeeqTaskStorage<>();
        OnNumberGeneratorCompletionHandler<NumberGenerator, Integer> completionHandler =
                new OnNumberGeneratorCompletionHandler<>();

        Beeq<NumberGenerator, Integer> beeq = new Beeq<>(storage, processor, completionHandler, 2);

        BeeqTaskPoolSegment<NumberGenerator> smallNumber = getSmallPoolSegment();
        BeeqTaskPoolSegment<NumberGenerator> largeNumbers = getLargePoolSegment();

        beeq.createSegment(smallNumber);
        beeq.createSegment(largeNumbers);

        NumberGenerator numberGeneratorSmallOne = new NumberGenerator(1, 100);
        NumberGenerator numberGeneratorSmallTwo = new NumberGenerator(1, 100);
        NumberGenerator numberGeneratorLarge = new NumberGenerator(101, 1000);
        NumberGenerator numberGeneratorExtraLarge = new NumberGenerator(1001, 10000);

        beeq.submitTask(numberGeneratorSmallOne);
        beeq.submitTask(numberGeneratorSmallTwo);
        beeq.submitTask(numberGeneratorLarge);
        beeq.submitTask(numberGeneratorExtraLarge);
    }

    private static BeeqTaskPoolSegment<NumberGenerator> getSmallPoolSegment() {
        return new BeeqTaskPoolSegment<>(
                "Small numbers segment",
                (task) -> task.getMax() <= 100,
                1,
                new BeeqRetryPolicy(Collections.singletonList(1)));
    }

    private static BeeqTaskPoolSegment<NumberGenerator> getLargePoolSegment() {
        return new BeeqTaskPoolSegment<>(
                "Large numbers segment",
                (task) -> task.getMax() > 100 && task.getMax() < 1000,
                1,
                new BeeqRetryPolicy(Collections.singletonList(1)));
    }
}
