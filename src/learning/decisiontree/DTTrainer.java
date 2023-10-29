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
	private ArrayList<Duple<V,L>> baseData;
	private boolean restrictFeatures;
	private Function<ArrayList<Duple<V,L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private BiFunction<V,F,FV> getFeatureValue;
	private Function<FV,FV> successor;
	
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

	// TODO: Call allFeatures.apply() to get the feature list. Then shuffle the list, retaining
	//  only targetNumber features. Should pass DTTest.testReduced().
	public static <V,L, F, FV  extends Comparable<FV>> ArrayList<Duple<F,FV>>
	reducedFeatures(ArrayList<Duple<V,L>> data, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
					int targetNumber) {
		ArrayList<Duple<F, FV>> fl = allFeatures.apply(data);
		Collections.shuffle(fl);
		ArrayList<Duple<F, FV>> fl2 = new ArrayList<>(fl.subList(0, targetNumber));
		return fl2;
    }
	
	public DecisionTree<V,L,F,FV> train() {
		return train(baseData);
	}

	public static <V,L> int numLabels(ArrayList<Duple<V,L>> data) {
		return data.stream().map(Duple::getSecond).collect(Collectors.toUnmodifiableSet()).size();
	}
	
	private DecisionTree<V,L,F,FV> train(ArrayList<Duple<V,L>> data) {
		// TODO: Implement the decision tree learning algorithm
		if (numLabels(data) == 1) {
			// TODO: Return a leaf node consisting of the only label in data
			return new DTLeaf<>(data.get(0).getSecond());
		} else {
			ArrayList<Duple<F, FV>> features = allFeatures.apply(data);
			if (!restrictFeatures) {

			} else {
				int target = (int) Math.floor(Math.sqrt(allFeatures.apply(data).size()));
                features = reducedFeatures(data, allFeatures, target);
			}
			double high = Double.NEGATIVE_INFINITY;
			Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> lacerate = null;
			Duple<F, FV> featured = null;
			for (Duple<F, FV> feature : features) {
				Duple<ArrayList<Duple<V, L>>, ArrayList<Duple<V, L>>> split = splitOn(data, feature.getFirst(), feature.getSecond(), getFeatureValue);
//				if (split.getFirst().isEmpty() || split.getSecond().isEmpty()) {
//					if (split.getFirst().isEmpty()) {
//						return new DTLeaf<>(mostPopularLabelFrom(split.getSecond()));
//					} else {
//						return new DTLeaf<>(mostPopularLabelFrom(split.getFirst()));
//					}
//				}
				double gain1 = gain(data, split.getFirst(), split.getSecond());
				if (gain1 > high) {
					high = gain1;
					lacerate = split;
					featured = feature;
				}
			}
            DecisionTree<V, L, F, FV> right = train(lacerate.getFirst());
			DecisionTree<V, L, F, FV> left = train(lacerate.getSecond());
			return new DTInterior<>(featured.getFirst(), featured.getSecond(), right, left, getFeatureValue, successor);

			// TODO: Return an interior node.
			//  If restrictFeatures is false, call allFeatures.apply() to get a complete list
			//  of features and values, all of which you should cosider when splitting.
			//  If restrictFeatures is true, call reducedFeatures() to get sqrt(# features)
			//  of possible features/values as candidates for the split. In either case,
			//  for each feature/value combination, use the splitOn() function to break the
			//  data into two parts. Then use gain() on each split to figure out which
			//  feature/value combination has the highest gain. Use that combination, as
			//  well as recursively created left and right nodes, to create the new
			//  interior node.
			//  Note: It is possible for the split to fail; that is, you can have a split
			//  in which one branch has zero elements. In this case, return a leaf node
			//  containing the most popular label in the branch that has elements.
		}
	}

	public static <V,L> L mostPopularLabelFrom(ArrayList<Duple<V, L>> data) {
		Histogram<L> h = new Histogram<>();
		for (Duple<V,L> datum: data) {
			h.bump(datum.getSecond());
		}
		return h.getPluralityWinner();
	}

	// TODO: Generates a new data set by sampling randomly with replacement. It should return
	//    an `ArrayList` that is the same length as `data`, where each element is selected randomly
	//    from `data`. Should pass `DTTest.testResample()`.
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

	public static <V,L, F, FV  extends Comparable<FV>> Duple<ArrayList<Duple<V,L>>,ArrayList<Duple<V,L>>> splitOn
			(ArrayList<Duple<V,L>> data, F feature, FV featureValue, BiFunction<V,F,FV> getFeatureValue) {
		ArrayList<Duple<V, L>> firstData = new ArrayList<Duple<V, L>>();
		ArrayList<Duple<V, L>> secondData = new ArrayList<Duple<V, L>>();

		for (Duple<V,L> d : data) {
			FV newFV = getFeatureValue.apply(d.getFirst(), feature);
			int comp = newFV.compareTo(featureValue);
			if (comp <= 0) {
				firstData.add(d);
			} else {
				secondData.add(d);
			}
		}

		if (firstData.isEmpty() || secondData.isEmpty()) {
			// Handle case where one branch is empty
			// In this case, return a Duple with both branches containing all data
			return new Duple<>(data, data);
		}

		return new Duple<>(firstData, secondData);
	}
}
