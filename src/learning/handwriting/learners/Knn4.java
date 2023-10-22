package learning.handwriting.learners;

import learning.classifiers.Knn;
import learning.handwriting.core.Drawing;

public class Knn4 extends Knn<Drawing,String> {
    public Knn4() {
        super(4, (d1, d2) -> (double)d1.distance(d2));
    }
}
