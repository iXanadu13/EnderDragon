package pers.xanadu.enderdragon.util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class AliasSample {
    private final int[] alias;
    private final double[] probability;
    public AliasSample(List<Double> probabilities) {
        if (probabilities == null) throw new NullPointerException();
        if (probabilities.size() == 0)
            throw new IllegalArgumentException("Probability vector can't be empty.");
        this.probability = new double[probabilities.size()];
        this.alias = new int[probabilities.size()];
        final double average = 1.0 / probabilities.size();
        probabilities = new ArrayList<>(probabilities);//make a copy of list, avoid modifying the param
        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();
        for (int i = 0; i < probabilities.size(); ++i) {
            if (probabilities.get(i) >= average) large.add(i);
            else small.add(i);
        }
        while (!small.isEmpty() && !large.isEmpty()) {
            int less = small.removeLast();
            int more = large.removeLast();
            probability[less] = probabilities.get(less) * probabilities.size();
            alias[less] = more;
            probabilities.set(more, (probabilities.get(more) + probabilities.get(less)) - average);
            if (probabilities.get(more) >= 1.0 / probabilities.size()) large.add(more);
            else small.add(more);
        }
        while (!small.isEmpty()) probability[small.removeLast()] = 1.0;
        while (!large.isEmpty()) probability[large.removeLast()] = 1.0;
    }

    public int next() {
        int column = ThreadLocalRandom.current().nextInt(probability.length);
        boolean coinToss = ThreadLocalRandom.current().nextDouble() < probability[column];
        return coinToss ? column : alias[column];
    }

}
