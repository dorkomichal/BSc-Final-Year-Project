package gameproblem;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import geneticClasses.*;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import ontology.Types;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
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
    StateObservation stateObs;
    int pointer = 0;
    boolean runga = false;

    public GameAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
        encodeActions(stateObs.getAvailableActions());
        this.stateObs = stateObs;
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
        if(optimisedActions == null && runga) {
            this.optimisedActions = new ArrayList<>();
           /* while(!stateObs.isGameOver()) {
                this.optimisedActions.addAll(runGA(stateObs));
            }*/
            this.optimisedActions.addAll(runGA(stateObs));
            this.runga = false;
        }
        Types.ACTIONS oneAction = optimisedActions.get(pointer);
        pointer++;
        if (pointer >= optimisedActions.size()) {
            pointer = 0;
            this.optimisedActions = runGA(stateObs);
        }
        return oneAction;
    }


    private List<Types.ACTIONS> runGA(StateObservation stateObs) {
        GameFitness gameFitness = new GameFitness();
        gameFitness.updateObservation(stateObs);
        FitnessCalculator.setFitnessFunction(gameFitness);
        Driver driver = Driver.getDriver();
        BinaryIndividualMapReduce.setChromosomeLength(30);
        driver.initializePopulation(50, IndividualType.String, stringEncodedActions.toArray(new String[stringEncodedActions.size()]));
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(10000);

        JavaRDD<IndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();
        JavaRDD<IndividualMapReduce> newGeneration;
        while (true) {
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation);

            IndividualMapReduce elite = mapper.getElite(populationWithFitness);
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite, SelectionMethod.tournament);
            System.out.println("Size of selected individuals: " + selectedIndividuals.count());
            newGeneration = reducer.reduceCrossover(selectedIndividuals, true, 2);

            //GlobalFile.setIndividualMapReduces(newGeneration.collect());
            //parallelizedPopulation = driver.paralleliseData(GlobalFile.getIndividualMapReduces());
            parallelizedPopulation = newGeneration;
            generationCounter++;

            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            //Important step for RWS selection is to reset max fitness of current generation
            //and assign new generation of the individuals to the population in order to calculate
            //aggregate fitness of the population necessary for RWS selection method
            if (GlobalFile.isSolutionFound() || generationCounter >= 20) {
                GlobalFile.setFittestIndividual(newGeneration.filter(ind -> ind.getFitness() >= GlobalFile.getCurrentMaxFitness() || ind.getFitness() >= GlobalFile.getMaxFitness()).collect().get(0));
                break; //if soulution is found or generation has converged to max and didn't change for some generations
            }
            GlobalFile.resetCurrentMax();
            GlobalFile.resetMaxNotChanged();
        }
        System.out.println(GlobalFile.getFittestIndividual().toString());
        return GameFitness.actionDecoder((String[]) GlobalFile.getFittestIndividual().getChromosome());
    }
}
