package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.*;
import java.util.function.ToIntFunction;

public class AlphaBeta extends CheckersSearcher {
    private int numNodes = 0;
    private int depth = 4;

    private PlayerColor lastColor;
    private PlayerColor thisColor;


    public AlphaBeta(ToIntFunction<Checkerboard> e) {
        super(e);
    }

    public void setDepthLimit(int depth) {
        this.depth = depth;
    }

    @Override
    public int numNodesExpanded() {
        return numNodes;
    }

    @Override
    public Optional<Duple<Integer, Move>> selectMove(Checkerboard board) {
        Optional<Duple<Integer, Move>> best = Optional.empty();
        thisColor = board.getCurrentPlayer();
        int beta = Integer.MAX_VALUE;
        int alpha = Integer.MIN_VALUE;
        int negation = 1;
        for (Checkerboard alternative: board.getNextBoards()) {
            numNodes += 1;
            int scoreFor = alphabeta(alternative, depth, alpha, beta, negation);
            if (best.isEmpty() || best.get().getFirst() < scoreFor) {
                best = Optional.of(new Duple<>(scoreFor, alternative.getLastMove()));
            }
        }
        return best;
    }


    private int alphabeta(Checkerboard node, int depth, int alpha, int beta, int negation) {

        lastColor = thisColor;
        thisColor = node.getCurrentPlayer();
        if (thisColor == lastColor) {
            negation = -negation;
        }

        if ((depth == 0) || (node.gameOver())) {
            return (negation * getEvaluator().applyAsInt(node));
        }
        List<Checkerboard> childNodes = orderMoves(node.getNextBoards());


        int value = Integer.MIN_VALUE;
        for (Checkerboard child : childNodes) {
            value = Math.max(value, -alphabeta(child, depth -1, -beta, -alpha, -negation));
            alpha = Math.max(alpha, value);
            if (alpha >= beta) {
                break;
            }
        }
        return value;
    }
    private List<Checkerboard> orderMoves(List<Checkerboard> moves) {
        moves.sort(new MoveComparator());
        return moves;
    }

    class MoveComparator implements Comparator<Checkerboard> {
        @Override
        public int compare(Checkerboard board1, Checkerboard board2) {
            int value1 = getValue(board1);
            int value2 = getValue(board2);
            return Integer.compare(value2, value1);
        }

        private int getValue(Checkerboard board) {
            return getEvaluator().applyAsInt(board);
        }
    }
}
