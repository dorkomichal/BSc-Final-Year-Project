package main;

import core.ArcadeMachine;
import geneticClasses.*;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import sat.Satisfiability;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Michal Dorko on 11/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class MapReduceBinaryGAMain {

    public static void main(String[] args) {
       /* FitnessFunction function = new FitnessFunction() {
            @Override
            public int calculateFitness(Object[] chromosome, IndividualMapReduce individual) {
                String stringChromosome = getStringFromByteArray((Byte[])chromosome);
                int threshold = 1000;
                int a = Integer.parseInt(stringChromosome.substring(0, 16), 2);
                int b = Integer.parseInt(stringChromosome.substring(16, stringChromosome.length()), 2);
                double calculation = Math.sqrt(a*5) + b;
                if (calculation >= threshold) {
                    return 0;
                } else {
                    return (int) Math.round(calculation);
                }
            }
        };
        Satisfiability sat = new Satisfiability(86, 20);
        FitnessCalculator.setFitnessFunction(sat);
        Driver driver = Driver.getDriver();
        BinaryIndividualMapReduce.setChromosomeLength(20);
        driver.initializePopulation(10, IndividualType.Binary, null);
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(86);

        JavaRDD<IndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();

        while (true) {
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation);
            if (GlobalFile.isSolutionFound()) {
                break; //if soulution is found or generation has converged to max and didn't change for some generations
            }
            GlobalFile.resetMaxNotChanged();
            IndividualMapReduce elite = mapper.getElite(populationWithFitness);
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite, SelectionMethod.tournament);
            JavaRDD<IndividualMapReduce> newGeneration = reducer.reduceCrossover(selectedIndividuals, true, 2);
            GlobalFile.setIndividualMapReduces(newGeneration.collect());
            parallelizedPopulation = driver.paralleliseData(GlobalFile.getIndividualMapReduces());
            generationCounter++;

            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            //Important step for RWS selection is to reset max fitness of current generation
            //and assign new generation of the individuals to the population in order to calculate
            //aggregate fitness of the population necessary for RWS selection method
            GlobalFile.resetCurrentMax();
            GlobalFile.assignNewGenerationToPopulation();
        }


        System.out.println("Solution Found: ");
        System.out.println("Problem: \n");
        for (String[] s: sat.getExpressionString()) {
            System.out.println(Arrays.toString(s));
        }
        String solution = GlobalFile.getNewGeneration().getFittestIndividual().toString();
        for (int i = 0; i < solution.length(); i++) {
            System.out.println("P" + i + " = " + solution.substring(i,i+1));
        }
        */

        String gamesPath = "examples/gridphysics/";
        String games[] = new String[]{};
        String generateLevelPath = "examples/generatedLevels/";

        //Training Set 1 (2015; CIG 2014)
        games = new String[]{"aliens", "boulderdash", "butterflies", "chase", "frogs",
                "missilecommand", "portals", "sokoban", "survivezombies", "zelda"};

        //Training Set 2 (2015; Validation CIG 2014)
        //games = new String[]{"camelRace", "digdug", "firestorms", "infection", "firecaster",
        //      "overload", "pacman", "seaquest", "whackamole", "eggomania"};

        //Training Set 3 (2015)
        //games = new String[]{"bait", "boloadventures", "brainman", "chipschallenge",  "modality",
        //                              "painter", "realportals", "realsokoban", "thecitadel", "zenpuzzle"};

        //Training Set 4 (Validation GECCO 2015, Test CIG 2014)
        //games = new String[]{"roguelike", "surround", "catapults", "plants", "plaqueattack",
        //        "jaws", "labyrinth", "boulderchase", "escape", "lemmings"};


        //Training Set 5 (Validation CIG 2015, Test GECCO 2015)
        //games = new String[]{ "solarfox", "defender", "enemycitadel", "crossfire", "lasers",
        //                               "sheriff", "chopper", "superman", "waitforbreakfast", "cakybaky"};

        //Training Set 6 (Validation CEEC 2015)
        //games = new String[]{"lasers2", "hungrybirds" ,"cookmepasta", "factorymanager", "raceBet2",
        //        "intersection", "blacksmoke", "iceandfire", "gymkhana", "tercio"};

        boolean visuals = false;
        String recordActionsFile = "./actions.txt"; //where to record the actions executed. null if not to save.
        int seed = new Random().nextInt();

        //Game and level to play
        int gameIdx = 0;
        int levelIdx = 0; //level names from 0 to 4 (game_lvlN.txt).
        String game = gamesPath + games[gameIdx] + ".txt";
        String level1 = gamesPath + games[gameIdx] + "_lvl" + levelIdx +".txt";

        String recordLevelFile = generateLevelPath +"geneticLevelGenerator/" + games[gameIdx] + "_lvl0.txt";

        String gameAgent = "gameproblem.GameAgent";

        ArcadeMachine.runOneGame(game, level1, visuals, gameAgent, recordActionsFile, seed);
    }

    public static String getStringFromByteArray(Byte[] chromosome) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i]);
        }
        return stringBuilder.toString();
    }
}
