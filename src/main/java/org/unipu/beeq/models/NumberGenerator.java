package org.unipu.beeq.models;

public class NumberGenerator {

    private int minNumber;
    private int maxNumber;

    public NumberGenerator(int minNumber, int maxNumber) {
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
    }

    public int getMax() {
        return maxNumber;
    }

    public int getMin() {
        return minNumber;
    }

    @Override
    public String toString() {
        return "NumberGenerator{" +
                "minNumber=" + minNumber +
                ", maxNumber=" + maxNumber +
                '}';
    }
}






