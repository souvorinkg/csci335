package checkers.evaluators;

import checkers.core.Checkerboard;
import checkers.core.Move;
import checkers.core.PlayerColor;

import java.util.Set;
import java.util.function.ToIntFunction;

public class DefKingDefDoubleJump implements ToIntFunction<Checkerboard> {
    public int applyAsInt(Checkerboard c) {
        PlayerColor myColor = c.getCurrentPlayer();
        int myScore = 0;
        int yourScore = 0;
        //myScore += c.numPiecesOf(myColor);
        //myScore +=  c.numKingsOf(myColor) *5;
        yourScore += c.numPiecesOf(myColor.opponent());
        yourScore += c.numKingsOf(myColor.opponent())*100;
        //System.out.println(myPieces-yourPieces);
        if (isDoubleJumpPossible(c)) {
            yourScore += 10;
        }
        return -yourScore;
    }

    public boolean isDoubleJumpPossible(Checkerboard board) {
        PlayerColor opponentColor = board.getCurrentPlayer().opponent();

        // Get all possible moves for the opponent
        Set<Move> opponentMoves = board.getLegalMoves(opponentColor);

        // Check each move to see if there are additional jumps available
        for (Move move : opponentMoves) {
            Checkerboard simulatedBoard = board.duplicate(); // Create a copy of the board
            simulatedBoard.move(move); // Simulate the move

            // Check if there are any additional jumps available for the opponent
            Set<Move> additionalJumps = simulatedBoard.allCaptureMoves(opponentColor);
            if (!additionalJumps.isEmpty()) {
                return true; // Double jump is possible
            }
        }

        return false; // No double jump is possible
    }
}
