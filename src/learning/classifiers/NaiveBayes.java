package learning.classifiers;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;

import java.awt.*;
import java.io.Console;
import java.util.*;
import java.util.function.Function;

public class NaiveBayes<V,L,F> implements Classifier<V,L> {
    // Each entry represents P(Feature | Label)
    // We want to know P(Label | Features). That means calculating P(Feature | Label) * P(Label) / P(Feature)
    // P(Feature) is invariant to the label and is generally ignored.
    // But we could do what we did with Markov chains, and calculate 1 / sum(P(Feature|Label)) for P(Label) / P(Feature)

    // Each entry represents P(Feature | Label)
    private LinkedHashMap<L, Histogram<F>> featuresByLabel = new LinkedHashMap<>();

    // Each entry represents P(Label)
    private Histogram<L> priors = new Histogram<>();

    // Given a value, this function returns a list of features and counts of those features.
    private Function<V, ArrayList<Duple<F, Integer>>> allFeaturesFrom;

    public NaiveBayes(Function<V, ArrayList<Duple<F, Integer>>> allFeaturesFrom) {
        this.allFeaturesFrom = allFeaturesFrom;
    }


    // In the training process, we accumulate data to calculate P(Feature), P(Label), and P(Feature | Label).
    // For each data item:
    // * Increment the prior count for the item's label.
    // * For each feature in the item (determined by calling allFeaturesFrom on the item's value)
    //   * Increment the feature count for the item's label by the number of appearances of the feature.
    @Override
    public void train(ArrayList<Duple<V, L>> data) {
        for (Duple<V, L> item : data) {
            L label = item.getSecond();
            priors.bump(label);
            V value = item.getFirst();
            ArrayList<Duple<F, Integer>> features = allFeaturesFrom.apply(value);
            for (Duple<F, Integer> feature : features) {
                int count = feature.getSecond();
                F featureValue = feature.getFirst();
                Histogram<F> featureHistogram = featuresByLabel.computeIfAbsent(label, k -> new Histogram<>());
                featureHistogram.bumpBy(featureValue, count + 1); // Apply Laplace smoothing by adding 1
            }
        }
    }

    @Override
    public L classify(V value) {
        double bestPrior = Double.MIN_VALUE;
        L bestLabel = null;
        for (L label : priors) {
            int pLabel = priors.getCountFor(label);
            double pLabelGivenFeature = 1;

            ArrayList<Duple<F, Integer>> features = allFeaturesFrom.apply(value);
            for (Duple<F, Integer> feature : features) {
                F featureValue = feature.getFirst();
                Histogram<F> featureHistogram = featuresByLabel.get(label);
                if (featureHistogram != null) {
                    double featureCount = featureHistogram.getCountFor(featureValue);
                    pLabelGivenFeature *= ((featureCount + 1) / (featureHistogram.getTotalCounts() + featuresByLabel.size()));

                }
            }

            if (bestPrior < pLabelGivenFeature * pLabel) {
                bestPrior = pLabelGivenFeature * pLabel;
                bestLabel = label;
            }
        }
        return bestLabel;
    }
}
