package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.function.ToDoubleBiFunction;

// KnnTest.test() should pass once this is finished.
public class Knn<V, L> implements Classifier<V, L> {
    private ArrayList<Duple<V, L>> data = new ArrayList<>();
    private ToDoubleBiFunction<V, V> distance;
    private int k;

    public Knn(int k, ToDoubleBiFunction<V, V> distance) {
        this.k = k;
        this.distance = distance;
    }

    @Override
    public L classify(V value) {
        ArrayList<Duple<Double, L>> nearestNeighbors = new ArrayList<>();

        for (Duple<V, L> entry : data) {
            double d = distance.applyAsDouble(value, entry.getFirst());

            if (nearestNeighbors.size() < k) {
                nearestNeighbors.add(new Duple<>(d, entry.getSecond()));
            } else {
                int maxIdx = 0;
                for (int i = 1; i < k; i++) {
                    if (nearestNeighbors.get(i).getFirst() > nearestNeighbors.get(maxIdx).getFirst()) {
                        maxIdx = i;
                    }
                }

                if (d < nearestNeighbors.get(maxIdx).getFirst()) {
                    nearestNeighbors.set(maxIdx, new Duple<>(d, entry.getSecond()));
                }
            }
        }

        Histogram<L> labelHistogram = new Histogram<>();
        for (Duple<Double, L> neighbor : nearestNeighbors) {
            labelHistogram.bump(neighbor.getSecond());
        }
        return labelHistogram.getPluralityWinner();
    }


    @Override
    public void train(ArrayList<Duple<V, L>> training) {
        data.addAll(training);
    }
}
