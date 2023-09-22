package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class MaterialDifference implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        PlayerColor myColor = c.getCurrentPlayer();
        int myPieces = c.numPiecesOf(myColor);
        int yourPieces = c.numPiecesOf(myColor.opponent());
        //System.out.println(myPieces-yourPieces);
        return myPieces - yourPieces;
    }
}
