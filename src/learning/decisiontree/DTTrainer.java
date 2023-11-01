package learning.decisiontree;

import core.Duple;
import learning.core.Histogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DTTrainer<V,L, F, FV extends Comparable<FV>> {
	private final ArrayList<Duple<V,L>> baseData;
	private final boolean restrictFeatures;
	private final Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private final BiFunction<V,F,FV> getFeatureValue;
	private final Function<FV,FV> successor;
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 boolean restrictFeatures, BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		baseData = data;
		this.restrictFeatures = restrictFeatures;
		this.allFeatures = allFeatures;
		this.getFeatureValue = getFeatureValue;
		this.successor = successor;
	}
	
	public DTTrainer(ArrayList<Duple<V, L>> data, Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures,
					 BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		this(data, allFeatures, false, getFeatureValue, successor);
	}

	public static <V,L, F, FV  extends Comparable<FV>> ArrayList<Duple<F,FV>>
	reducedFeatures(ArrayList<Duple<V,L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					int targetNumber) {
		ArrayList<Duple<F, FV>> fl = allFeatures.apply(data);
		Collections.shuffle(fl);
        return new ArrayList<>(fl.subList(0, targetNumber));
    }
	
	public DecisionTree<V,L,F,FV> train() {
		return train(baseData);
	}

	public static <V,L> int numLabels(ArrayList<Duple<V,L>> data) {
		return data.stream().map(Duple::getSecond).collect(Collectors.toUnmodifiableSet()).size();
	}

	private DecisionTree<V, L, F, FV> train(ArrayList<Duple<V, L>> data) {
		if (data.isEmpty() || numLabels(data) <= 1 || allFeatures.apply(data).isEmpty()) {
			return new DTLeaf<>(mostPopularLabelFrom(data));
		} else {
			ArrayList<Duple<F, FV>> features = allFeatures.apply(data);
			if (restrictFeatures) {
				int target = (int) Math.floor(Math.sqrt(features.size()));
				features = reducedFeatures(data, allFeatures, target);
			}
			double high = Double.NEGATIVE_INFINITY;
			Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> lacerate = new Duple<>(data, data);
			Duple<F, FV> featured = features.get(0);
			boolean unity = true;
			for (Duple<F, FV> feature : features) {
				Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> split = splitOn(data, feature.getFirst(), feature.getSecond(), getFeatureValue);
				if (!split.getFirst().isEmpty() && !split.getSecond().isEmpty()) {
					unity = false;
				}
				double gain1 = gain(data, split.getFirst(), split.getSecond());
				if (gain1 > high) {
					high = gain1;
					lacerate = split;
					featured = feature;
				}
			}
			if (unity) {
				return new DTLeaf<>(mostPopularLabelFrom(data));
			}
			DecisionTree<V, L, F, FV> right = train(lacerate.getFirst());
			DecisionTree<V, L, F, FV> left = train(lacerate.getSecond());
			return new DTInterior<>(featured.getFirst(), featured.getSecond(), right, left, getFeatureValue, successor);
		}
	}

	public static <V,L> L mostPopularLabelFrom(ArrayList<Duple<V, L>> data) {
		Histogram<L> h = new Histogram<>();
		for (Duple<V,L> datum: data) {
			h.bump(datum.getSecond());
		}
		return h.getPluralityWinner();
	}

	public static <V,L> ArrayList<Duple<V,L>> resample(ArrayList<Duple<V,L>> data) {
		ArrayList<Duple<V, L>> redata = new ArrayList<>();
		for (int i = 0; i<data.size(); i++) {
			Random rand = new Random();
			int n = rand.nextInt(data.size());
			redata.add(data.get(n));
		}
		return redata;
	}

	public static <V,L> double getGini(ArrayList<Duple<V,L>> data) {
        Histogram<L> h = new Histogram<>();
        for (Duple<V, L> d : data) {
            h.bump(d.getSecond());
        }
        double gini = 1;
        for (L l : h) {
            double p_i = h.getPortionFor(l);
            double p_i2 = p_i * p_i;
            gini -= p_i2;
        }
        return gini;
    }

	public static <V,L> double gain(ArrayList<Duple<V,L>> parent, ArrayList<Duple<V,L>> child1,
									ArrayList<Duple<V,L>> child2) {
		return getGini(parent) - (getGini(child1) + getGini(child2));
	}

	public static <V, L, F, FV extends Comparable<FV>> Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> splitOn
			(ArrayList<Duple<V, L>> data, F feature, FV featureValue, BiFunction<V, F, FV> getFeatureValue) {
		ArrayList<Duple<V, L>> firstData = new ArrayList<>();
		ArrayList<Duple<V, L>> secondData = new ArrayList<>();
		boolean split = false;
		for (Duple<V, L> d : data) {
			FV newFV = getFeatureValue.apply(d.getFirst(), feature);
			int comp = newFV.compareTo(featureValue);
			if (comp <= 0) {
				firstData.add(d);
				split = true;
			} else {
				secondData.add(d);
			}
		}
		if (!split) {
			firstData = data;
			secondData.clear();
		}
		return new Duple<>(firstData, secondData);
	}

}
