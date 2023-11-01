package learning.sentiment.learners;

import learning.core.Histogram;
import learning.decisiontree.RandomForest;
import learning.sentiment.core.SentimentAnalyzer;

public class SentimentForest40 extends RandomForest<Histogram<String>, String, String, Integer> {
    public SentimentForest40() {
        super(40, SentimentAnalyzer::allFeatures, Histogram::getCountFor, i -> i + 1);
    }
}
