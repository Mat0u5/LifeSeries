package net.mat0u5.lifeseries.utils.other;
import java.util.Random;

public class WeightedRandomizer {
    private Random random;

    public WeightedRandomizer() {
        this.random = new Random();
    }

    public WeightedRandomizer(long seed) {
        this.random = new Random(seed);
    }

    public int getWeightedRandom(int minValue, int maxValue, int biasLevel, int maxBiasLevel, double biasStrength) {
        if (minValue > maxValue) {
            throw new IllegalArgumentException("minValue cannot be greater than maxValue");
        }
        if (biasLevel < 1 || biasLevel > maxBiasLevel) {
            throw new IllegalArgumentException("biasLevel must be between 1 and " + maxBiasLevel);
        }
        if (biasStrength <= 0) {
            throw new IllegalArgumentException("biasStrength must be positive");
        }

        int range = maxValue - minValue + 1;
        double[] weights = new double[range];

        double normalizedBias = (double)(biasLevel - 1) / (maxBiasLevel - 1);
        double targetCenter = normalizedBias * (range - 1);

        for (int i = 0; i < range; i++) {
            double distance = Math.abs(i - targetCenter);
            weights[i] = Math.exp(-distance * (biasStrength/10)) + 0.05;
        }

        int selectedIndex = weightedRandomSelect(weights);
        return minValue + selectedIndex;
    }

    public int getWeightedRandom(int minValue, int maxValue, int biasLevel, int maxBiasLevel) {
        return getWeightedRandom(minValue, maxValue, biasLevel, maxBiasLevel, 1.0);
    }

    private int weightedRandomSelect(double[] weights) {
        double totalWeight = 0;
        for (double weight : weights) {
            totalWeight += weight;
        }

        double randomValue = random.nextDouble() * totalWeight;
        double currentWeight = 0;

        for (int i = 0; i < weights.length; i++) {
            currentWeight += weights[i];
            if (randomValue <= currentWeight) {
                return i;
            }
        }

        return weights.length - 1; // Fallback
    }
}