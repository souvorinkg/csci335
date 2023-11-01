package learning.decisiontree;

import core.Duple;
import learning.core.Classifier;
import learning.core.Histogram;
import learning.core.Updateable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.BiFunction;
import java.util.function.Function;

public class RandomForest<V,L, F, FV extends Comparable<FV>> implements Classifier<V,L> {
	private ArrayList<DecisionTree<V,L,F,FV>> treeRoots;
	private final int numTrees;
	private final Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures;
	private final BiFunction<V,F,FV> getFeatureValue;
	private final Function<FV,FV> successor;

	public RandomForest(int numTrees, Function<ArrayList<Duple<V, L>>, ArrayList<Duple<F,FV>>> allFeatures,
						BiFunction<V,F,FV> getFeatureValue, Function<FV,FV> successor) {
		this.numTrees = numTrees;
		this.allFeatures = allFeatures;
		this.getFeatureValue = getFeatureValue;
		this.successor = successor;
	}

	@Override
	public void train(ArrayList<Duple<V, L>> data) {
		this.treeRoots = new ArrayList<>();
		for (int i = 0; i < numTrees; i++) {
			DTTrainer<V,L, F, FV> trainer = new DTTrainer<>(DTTrainer.resample(data), allFeatures, true,
					getFeatureValue, successor);
			this.treeRoots.add(trainer.train());
		}
	}

	@Override
	public L classify(V v) {
		Histogram<L> Magi = new Histogram<>();
		for (DecisionTree<V,L,F,FV> tree : treeRoots) {
			Magi.bump(tree.classify(v));
		}
		return Magi.getPluralityWinner();
	}

	public <D extends Updateable<Duple<F,FV>>> void visualize(L label, D drawable) {
		for (DecisionTree<V,L,F,FV> treeRoot: treeRoots) {
			treeRoot.visualize(label, new ArrayList<>(), drawable);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (DecisionTree<V,L,F,FV> root: treeRoots) {
			sb.append(root.toString());
		}
		return sb.toString();
	}

	public HashSet<L> getLabels() {
		HashSet<L> result = new HashSet<>();
		for (DecisionTree<V,L,F,FV> root: treeRoots) {
			root.addAllLabels(result);
		}
		return result;
	}
}
