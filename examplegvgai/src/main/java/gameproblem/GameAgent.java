package gameproblem;

import core.VGDLFactory;
import core.VGDLRegistry;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import geneticClasses.*;
import main.GARunner;
import main.MapReduceBinaryGAMain;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import ontology.Types;
import tools.ElapsedCpuTimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Dorko on 01/02/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GameAgent extends AbstractPlayer {

    List<String> stringEncodedActions;
    List<Types.ACTIONS> optimisedActions;
    int pointer = 0;
    static boolean gameLost;

    public GameAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        encodeActions(stateObs.getAvailableActions());
        gameLost = false;
    }



    private void encodeActions(List<Types.ACTIONS> actions) {
        this.stringEncodedActions = new ArrayList<>();
        for (Types.ACTIONS action: actions) {
            switch (action) {
                case ACTION_NIL: stringEncodedActions.add("n");
                    break;
                case ACTION_UP: stringEncodedActions.add("u");
                    break;
                case ACTION_DOWN: stringEncodedActions.add("d");
                    break;
                case ACTION_RIGHT: stringEncodedActions.add("r");
                    break;
                case ACTION_LEFT: stringEncodedActions.add("l");
                    break;
                case ACTION_ESCAPE: stringEncodedActions.add("e");
                    break;
                case ACTION_USE: stringEncodedActions.add("s");
            }
        }
    }

    @Override
    public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        if(optimisedActions == null) {
            this.optimisedActions = new ArrayList<>();
            this.optimisedActions.addAll(runGA(stateObs));
        }
        Types.ACTIONS oneAction = optimisedActions.get(pointer);
        pointer++;
        if (pointer >= optimisedActions.size()) {
            pointer = 0;
            this.optimisedActions = runGA(stateObs);
        }
        MapReduceBinaryGAMain.addActionSequence(optimisedActions);
        StateObservation stateCopy = stateObs.copy();
        stateCopy.advance(oneAction);
        if(stateCopy.isGameOver() && stateCopy.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
            gameLost = true;
        }
        return oneAction;
    }


    private List<Types.ACTIONS> runGA(StateObservation stateObs) {
        StateObservation copy = stateObs.copy();
        copy.setVGDLCopies(VGDLFactory.getNewCopy(), VGDLRegistry.getCopy());
        GameFitness gameFitness = new GameFitness(copy);
        String[] source = stringEncodedActions.toArray(new String[stringEncodedActions.size()]);
        int chromosomeLength = 10;
        int populationSize = 100;
        int maxFitness = 1000;
        int maxGeneration = 100;
        SelectionMethod method = SelectionMethod.tournament;
        boolean multipoint = false;
        int numberOfMultipoints = 2;
        GARunner gaRunner = GARunner.getGARunner(gameFitness, source, chromosomeLength, populationSize, maxFitness, maxGeneration, method, multipoint, numberOfMultipoints);
        String[] solution = (String[]) gaRunner.runGA();
        return GameFitness.actionDecoder(solution);
    }
}
