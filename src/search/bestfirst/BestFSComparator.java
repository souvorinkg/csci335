package search.bestfirst;

import search.SearchNode;

import java.util.Comparator;
import java.util.function.ToIntFunction;

public class BestFSComparator <T> implements Comparator<SearchNode<T>> {
    private final ToIntFunction<T> heuristic;
    public BestFSComparator(ToIntFunction<T> heuristic){
        this.heuristic = heuristic;
    }
    @Override
    public int compare(SearchNode<T> o1, SearchNode<T> o2) {
        return Integer.compare(heuristic.applyAsInt(o1.getValue()), heuristic.applyAsInt(o2.getValue()));
    }
}
