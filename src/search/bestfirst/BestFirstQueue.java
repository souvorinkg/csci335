package search.bestfirst;

import search.SearchNode;
import search.SearchQueue;

import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.ToIntFunction;

public class BestFirstQueue<T> implements SearchQueue<T> {
    private final PriorityQueue<SearchNode<T>> queue;
    private final HashSet<T> visited = new HashSet<>();

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
