package robosim.ai;

import robosim.core.Action;
import robosim.core.Controller;
import robosim.core.Simulator;
import robosim.reinforcement.QTable;

public class qRoomba implements Controller {

    QTable qTable = new QTable(3, 4, 1, 10, 5, 0.9);
    int reward = 0;
    @Override
    public void control(Simulator sim) {
        int action;
        if (sim.findClosestProblem() < 30) {
            reward += 2;
            action = qTable.senseActLearn(1, reward);
        } else if (sim.wasHit()) {
            reward -= 100;
            action = qTable.senseActLearn(0, reward);
        } else {
            action = qTable.senseActLearn(2, reward);
        }

        switch (action) {
            case 0 -> Action.FORWARD.applyTo(sim);
            case 1 -> Action.LEFT.applyTo(sim);
            case 2 -> Action.RIGHT.applyTo(sim);
            case 3 -> Action.BACKWARD.applyTo(sim);
        }
    }

}