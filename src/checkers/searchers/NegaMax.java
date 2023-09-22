package checkers.searchers;

import checkers.core.Checkerboard;
import checkers.core.CheckersSearcher;
import checkers.core.Move;
import checkers.core.PlayerColor;
import core.Duple;

import java.util.Optional;
import java.util.function.ToIntFunction;

public class NegaMax extends CheckersSearcher {
    private int numNodes = 0;
    private int depth = 4;

    private PlayerColor lastColor;
    private PlayerColor thisColor;

    public NegaMax(ToIntFunction<Checkerboard> e) {
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
        for (Checkerboard alternative: board.getNextBoards()) {
            numNodes += 1;
            int negation = -1;
            int scoreFor = negamax(alternative, depth, negation);
            if (best.isEmpty() || best.get().getFirst() < scoreFor) {
                best = Optional.of(new Duple<>(scoreFor, alternative.getLastMove()));
            }
        }
        return best;
    }


    private int negamax(Checkerboard node, int depth, int negation) {

        lastColor = thisColor;
        thisColor = node.getCurrentPlayer();
        if (thisColor == lastColor) {
            negation = -negation;
        }

        if ((depth == 0) || (node.gameOver())) {
            return (negation * getEvaluator().applyAsInt(node));
        }
        int alpha = Integer.MIN_VALUE;



        for (Checkerboard alternative : node.getNextBoards()) {
            alpha = Math.max(alpha, -negamax(alternative, depth - 1, -negation));
        }
        return alpha;
    }
}
