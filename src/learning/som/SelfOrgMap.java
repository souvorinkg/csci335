package learning.som;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;

public class SelfOrgMap<V> {
    private V[][] map;
    private ToDoubleBiFunction<V, V> distance;
    private WeightedAverager<V> averager;

    public SelfOrgMap(int side, Supplier<V> makeDefault, ToDoubleBiFunction<V, V> distance, WeightedAverager<V> averager) {
        this.distance = distance;
        this.averager = averager;
        map = (V[][])new Object[side][side];
        for (int i = 0; i < side; i++) {
            for (int j = 0; j < side; j++) {
                map[i][j] = makeDefault.get();
            }
        }
    }

    // TODO: Return a SOMPoint corresponding to the map square which has the
    //  smallest distance compared to example.
    // NOTE: The unit tests assume that the map is traversed in row-major order,
    //  that is, the y-coordinate is updated in the outer loop, and the x-coordinate
    //  is updated in the inner loop.
    public SOMPoint bestFor(V example) {
        double small = 100;
        double dist;
        SOMPoint close = new SOMPoint(0, 0);

        // Iterate through all nodes in the SOM
        for (int i = 0; i < numMapNodes(); i++) {
            //System.out.println(numMapNodes() + " nodes");
            //System.out.println("Old small " + small + " for " + i);
            int x = i / getMapHeight();
            int y = i % getMapHeight();

            V currentPoint = getNode(x, y);
            //System.out.println(currentPoint + "currentPoint");
            //System.out.println(example + "currentExample");
            dist = distance.applyAsDouble(currentPoint, example);

            // If the distance is smaller than the current smallest, update small and close
            if (dist < small) {
                //System.out.println(dist + " is smaller than " + small);
                small = dist; // this line is not updating small
                //ystem.out.println("New small: " + small + " for " + i);
                close = new SOMPoint(x, y);
                //System.out.println(close);
            }
        }

        return close;
    }

    // TODO: Train this SOM with example.
    //  1. Find the best matching node.
    //  2. Update the best matching node with the average of itself and example,
    //     using a learning rate of 0.9.
    //  3. Update each neighbor of the best matching node that is in the map,
    //     using a learning rate of 0.4.
    public void train(V example) {
        SOMPoint bestSOM = bestFor(example);
        V best = getNode(bestSOM.y(), bestSOM.x());
        System.out.println("Old best is " + best);
        System.out.println("Old example is " + example);

        V newBest = averager.weightedAverage(example, best, .9);
        System.out.println("New weighted best is " + newBest);
        map[bestSOM.y()][bestSOM.x()] = newBest; // Update best in the map
        bestSOM = new SOMPoint(bestSOM.x(), bestSOM.y());
        SOMPoint[] neighbors = bestSOM.neighbors();

        for (SOMPoint neighborSOM : neighbors) {
            int neighborX = neighborSOM.x();
            int neighborY = neighborSOM.y();

            if (inMap(neighborSOM)) {
                V neighbor = getNode(neighborX, neighborY);
                V newNeighbor = averager.weightedAverage(example, neighbor, .4);
                map[neighborX][neighborY] = newNeighbor; // Update neighbor in the map
            }
        }
    }


    public V getNode(int x, int y) {
        return map[x][y];
    }

    public int getMapWidth() {
        return map.length;
    }

    public int getMapHeight() {
        return map[0].length;
    }

    public int numMapNodes() {
        return getMapHeight() * getMapWidth();
    }

    public boolean inMap(SOMPoint point) {
        return point.x() >= 0 && point.x() < getMapWidth() && point.y() >= 0 && point.y() < getMapHeight();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SelfOrgMap that) {
            if (this.getMapHeight() == that.getMapHeight() && this.getMapWidth() == that.getMapWidth()) {
                for (int x = 0; x < getMapWidth(); x++) {
                    for (int y = 0; y < getMapHeight(); y++) {
                        if (!map[x][y].equals(that.map[x][y])) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int x = 0; x < getMapWidth(); x++) {
            for (int y = 0; y < getMapHeight(); y++) {
                result.append(String.format("(%d, %d):\n", x, y));
                result.append(getNode(x, y));
            }
        }
        return result.toString();
    }
}
