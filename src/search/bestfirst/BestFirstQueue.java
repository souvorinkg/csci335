package search.bestfirst;

import core.Duple;
import search.SearchNode;
import search.SearchQueue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;

public class BestFirstQueue<T> implements SearchQueue<T> {
    private final PriorityQueue<SearchNode<T>> queue;
    private final HashSet<T> visited = new HashSet<>();
    // Each object in the priority queue is an estimate paired with a SearchNode.

    // For each object encountered, this is the lowest total length estimate
    // encountered so far.
    private HashMap<T,Integer> lowestEstimateFor;

    // Use this heuristic to get the estimated distance to the goal node.
    private ToIntFunction<T> heuristic;

    public BestFirstQueue(ToIntFunction<T> heuristic) {
        this.queue = new PriorityQueue<>(new BestFSComparator<>(heuristic));
    }




    @Override
    public void enqueue(SearchNode<T> node) {
        if (!visited.contains(node.getValue())) {
            queue.add(node);
            visited.add(node.getValue());
        }
    }

    @Override
    public Optional<SearchNode<T>> dequeue() {
        if (queue.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(queue.poll());
        }
    }
}
