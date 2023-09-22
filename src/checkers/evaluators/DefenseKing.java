package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class DefenseKing implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        PlayerColor myColor = c.getCurrentPlayer();
        int myScore = 0;
        int yourScore = 0;
        //myScore += c.numPiecesOf(myColor);
        //myScore +=  c.numKingsOf(myColor) *5;
        yourScore += c.numPiecesOf(myColor.opponent());
        yourScore += c.numKingsOf(myColor.opponent())*100;
        //System.out.println(myPieces-yourPieces);
        return 12-yourScore;
    }
}
