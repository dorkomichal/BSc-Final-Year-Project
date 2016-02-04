package gameproblem;

import core.game.StateObservation;
import geneticClasses.FitnessFunction;
import geneticClasses.IndividualMapReduce;
import ontology.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Dorko on 04/02/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GameFitness implements FitnessFunction {

    private StateObservation observation;
    private final int winner = 10000;
    private final int looser = -10000;

    public void updateObservation(StateObservation obs) {
        this.observation = obs;
    }

    @Override
    public int calculateFitness(Object[] chromosome, IndividualMapReduce individualMapReduce) {
        List<Types.ACTIONS> decodedActions = actionDecoder((String[])chromosome);
        StateObservation stateCopy = observation.copy();
        int score = 0;
        for (Types.ACTIONS action: decodedActions) {
            stateCopy.advance(action);
            if(stateCopy.isGameOver()) {
                Types.WINNER win = stateCopy.getGameWinner();
                if (win == Types.WINNER.PLAYER_LOSES) {
                    return looser;
                } else if(win == Types.WINNER.PLAYER_WINS) {
                    return winner;
                }
            }
            score += stateCopy.getGameScore();
        }
        return score;
    }

    public static List<Types.ACTIONS> actionDecoder(String[] chromosome) {
        ArrayList<Types.ACTIONS> decoded = new ArrayList<>(chromosome.length);
        for (String s : chromosome) {
            switch (s) {
                case "n": decoded.add(Types.ACTIONS.ACTION_NIL);
                    break;
                case "u": decoded.add(Types.ACTIONS.ACTION_UP);
                    break;
                case "d": decoded.add(Types.ACTIONS.ACTION_DOWN);
                    break;
                case "l": decoded.add(Types.ACTIONS.ACTION_LEFT);
                    break;
                case "r": decoded.add(Types.ACTIONS.ACTION_RIGHT);
                    break;
                case "e": decoded.add(Types.ACTIONS.ACTION_ESCAPE);
                    break;
                case "s": decoded.add(Types.ACTIONS.ACTION_USE);
                    break;
            }
        }
        return decoded;
    }
}
