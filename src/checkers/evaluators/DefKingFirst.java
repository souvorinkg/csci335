package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.PlayerColor;

import java.util.function.ToIntFunction;

public class DefKingFirst implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        PlayerColor myColor = c.getCurrentPlayer();
        PlayerColor yourColor = c.getCurrentPlayer().opponent();
        int myScore = 0;
        int yourScore = 0;
        //myScore += c.numPiecesOf(myColor);
        //myScore +=  c.numKingsOf(myColor) *5;
        yourScore += c.numPiecesOf(yourColor);
        if (c.numKingsOf(yourColor) == 1 ) {
            yourScore += 1000000;
        }
        for (int i= 0; i < c.numKingsOf(yourColor); i ++) {
            yourScore += 100;
        }

        //System.out.println(myPieces-yourPieces);
        return -yourScore;
    }
}
