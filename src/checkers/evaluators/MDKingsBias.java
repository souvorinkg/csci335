package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class MDKingsBias implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        PlayerColor myColor = c.getCurrentPlayer();
        int myScore = 1;
        int yourScore = 0;
        myScore += c.numPiecesOf(myColor);
        myScore +=  c.numKingsOf(myColor);
        yourScore += c.numPiecesOf(myColor.opponent());
        yourScore += c.numKingsOf(myColor.opponent());
        //System.out.println(myPieces-yourPieces);
        return myScore - yourScore;
    }
}
