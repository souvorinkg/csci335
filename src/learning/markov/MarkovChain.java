package learning.markov;

import learning.core.Histogram;

import javax.swing.text.html.Option;
import java.util.*;

public class MarkovChain<L,S> {
    private LinkedHashMap<L, HashMap<Optional<S>, Histogram<S>>> label2symbol2symbol = new LinkedHashMap<>();

    public Set<L> allLabels() {return label2symbol2symbol.keySet();}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (L language: label2symbol2symbol.keySet()) {
            sb.append(language);
            sb.append('\n');
            for (Map.Entry<Optional<S>, Histogram<S>> entry: label2symbol2symbol.get(language).entrySet()) {
                sb.append("    ");
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue().toString());
                sb.append('\n');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    // Increase the count for the transition from prev to next.
    // Should pass SimpleMarkovTest.testCreateChains().
    public void count(Optional<S> prev, L label, S next) {
        HashMap<Optional<S>, Histogram<S>> symbol2symbol = label2symbol2symbol.computeIfAbsent(label, k -> new HashMap<>());

        Histogram<S> histogram = symbol2symbol.get(prev);
        if (histogram == null) {
            histogram = new Histogram<>();
            symbol2symbol.put(prev, histogram);
        }

        histogram.bump(next);
    }


    // HINT: Be sure to add 1 to both the numerator and denominator when finding the probability of a
    // transition. This helps avoid sending the probability to zero.
    public double probability(ArrayList<S> sequence, L label) {
        double logProbabilitySum;
        logProbabilitySum = 0.0;

        HashMap<Optional<S>, Histogram<S>> symbol2symbol = label2symbol2symbol.get(label);
        if (symbol2symbol != null) {
            Optional<S> prev = Optional.empty();

            for (S currentSymbol : sequence) {
                Histogram<S> histogram = symbol2symbol.get(prev);

                if (histogram != null) {
                    double prob = (histogram.getCountFor(currentSymbol) + 1.0) / (histogram.getTotalCounts() + 1.0
                    );
                    double logProb = Math.log(prob);
                    logProbabilitySum += logProb;
                }

                prev = Optional.of(currentSymbol);
            }
        }

        return Math.exp(logProbabilitySum); //reconvert
    }

    // Return a map from each label to P(label | sequence).
    // Should pass MajorMarkovTest.testSentenceDistributions()
    public LinkedHashMap<L, Double> labelDistribution(ArrayList<S> sequence) {
        LinkedHashMap<L, Double> distribution = new LinkedHashMap<>();
        double totalProbability = 0.0;

        for (L label : allLabels()) {
            double labelProb = probability(sequence, label);
            distribution.put(label, labelProb);
            totalProbability += labelProb;
        }

        for (Map.Entry<L, Double> entry : distribution.entrySet()) {
            distribution.put(entry.getKey(), entry.getValue() / totalProbability);
        }

        return distribution;
    }

    // Calls labelDistribution(). Returns the label with the highest probability.
    // Should pass MajorMarkovTest.bestChainTest()
    public L bestMatchingChain(ArrayList<S> sequence) {
        LinkedHashMap<L, Double> allLang = labelDistribution(sequence);
        L bestChoice = null;
        double maxProbability = 0.0;

        for (Map.Entry<L, Double> entry : allLang.entrySet()) {
            if (entry.getValue() > maxProbability) {
                maxProbability = entry.getValue();
                bestChoice = entry.getKey();
            }
        }

        return bestChoice;
    }
}
