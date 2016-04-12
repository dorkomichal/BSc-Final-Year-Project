package islandmodel;

import driver.GlobalFile;
import geneticClasses.*;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Michal Dorko on 18/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class MapperIslandFitnessSelection implements Serializable {
    /**
     * Singleton instance
     */
    private static MapperIslandFitnessSelection mapperIslandFitnessSelection;
    private final static Logger LOGGER = LoggerFactory.getLogger(MapperIslandFitnessSelection.class);

    /**
     * Static method for creating and/or getting singleton of this class
     * @return singleton object of MapperIsland
     */
    public static MapperIslandFitnessSelection getMapperIslandFitnessSelection() {
        if (mapperIslandFitnessSelection == null) {
            mapperIslandFitnessSelection = new MapperIslandFitnessSelection();
            return mapperIslandFitnessSelection;
        } else {
            return mapperIslandFitnessSelection;
        }
    }

    /**
     * Calculates the fitness of the individuals on the island using Spark map() function and creates pair RDD of island with it's overall
     * fitness. This method also finds max fitness within this generation amongst all islands and checks whether
     * goal fitness set by user is met and flags this termination condition. Uses one map(), one reduce() and one filter() function from
     * Spark RDD operations.
     * @param parallelizedIslandPop RDD of Islands
     * @param fitnessCalculator instance of the class with implemented fitness evaluation function
     * @return pair RDD of island and it's overall fintess
     */
    public JavaPairRDD<Island, Long> mapCalculateFitness(JavaRDD<Island> parallelizedIslandPop, FitnessCalculator fitnessCalculator) {
        JavaPairRDD<Island, Long> populationWithFitness = parallelizedIslandPop.mapToPair(isl -> evaluateFitness(isl, fitnessCalculator));
        long currentMaxFitness = populationWithFitness.values().reduce(Math::max);
        GlobalFile.setCurrentMaxFitness(currentMaxFitness);
        long goalFitness = GlobalFile.getMaxFitness();
        if (currentMaxFitness >= goalFitness) {
            GlobalFile.setSolutionFound(true);
        }
        return populationWithFitness;
    }

    /**
     * Evaluates fitness on the island using provided fitness calculator and sums
     * fitness of all individuals on the island.
     * @param isl island to be evaluated
     * @param fitnessCalculator instance of the class with implemented fitness evaluation function
     * @return tuple of island and it's overall fitness
     */
    private Tuple2<Island, Long> evaluateFitness(Island isl, FitnessCalculator fitnessCalculator) {
        long maxFitness = Long.MIN_VALUE;
        Population population = isl.getPopulation();
        IndividualMapReduce[] individuals = population.getIndividualMapReduces();
        for (IndividualMapReduce ind : individuals) {
            long fitness = ind.calculateFitness(fitnessCalculator);
            if (fitness >= maxFitness) {
                maxFitness = fitness;
            }
        }
        Population evaluatedPopulation = new Population(isl.getSizeOfIsland());
        evaluatedPopulation.setIndividualMapReduces(individuals);
        Island evaluatedIsland = new Island(evaluatedPopulation, isl.getSizeOfIsland());
        return new Tuple2<>(evaluatedIsland, maxFitness);
    }

    /**
     * Method for performing selection method using Spark map() function on provided RDD of islands.
     * @param populationWithFitness island with evaluated population
     * @param method selection method
     * @param operations instance of available genetic operations for selection
     * @return island with selected individuals
     */
    public JavaRDD<Island> mapSelection(JavaPairRDD<Island, Long> populationWithFitness, SelectionMethod method, GeneticOperationsMapReduce operations) {
        JavaRDD<Island> islands = populationWithFitness.keys();
        JavaRDD<Island> islandsWithPairs;
        if (method.equals(SelectionMethod.tournament)) {
            islandsWithPairs = islands.map(island -> tournamentSelection(island, operations));
        } else {
            islandsWithPairs = islands.map(island -> rwsSelection(island, operations));
        }
        return islandsWithPairs;
    }

    /**
     * Tournament selection method optimised for island model and map phase
     * @param isl island on which selection will be performed
     * @param operationsMapReduce instance of available genetic operations for selection
     * @return island with pairs ready for crossover
     */
    private Island tournamentSelection(Island isl, GeneticOperationsMapReduce operationsMapReduce) {
        CrossoverPair[] pairs = new CrossoverPair[isl.getSizeOfIsland()];
        IndividualMapReduce[] ind = isl.getPopulation().getIndividualMapReduces();
        int head = 0;
        //if we have elitism implemented get fittest individual and set it as elite
        if (operationsMapReduce.isElitism()) {
            pairs[0] = getElite(isl);
            head = 1;
        }
        /* loop through the population and assign crossover pairs by performing Tournament selection
         * implemented in genetic operations class
         */
        for (int i = head; i < pairs.length; i++) {
            int[] randoms = new Random().ints(0, ind.length).distinct().limit(4).toArray();
            IndividualMapReduce parent1 = operationsMapReduce.tournamentSelection(ind[randoms[0]], ind[randoms[1]]);
            IndividualMapReduce parent2 = operationsMapReduce.tournamentSelection(ind[randoms[2]], ind[randoms[3]]);
            CrossoverPair pair = new CrossoverPair();
            pair.setParent1(parent1);
            pair.setParent2(parent2);
            pairs[i] = pair;
        }
        Population pairedPopulation = new Population(isl.getSizeOfIsland());
        pairedPopulation.setIndividualMapReduces(ind);
        Island pairedIsland = new Island(pairedPopulation, isl.getSizeOfIsland());
        pairedIsland.setCrossoverPairs(pairs);
        return pairedIsland;
    }

    /**
     * Method for performing Roulette Wheel Selection optimised for Island model and map() phase
     * @param isl island on which selection will be performed
     * @param geneticOperationsMapReduce instance of available genetic operations for selection
     * @return island with pairs ready for crossover
     */
    private Island rwsSelection(Island isl, GeneticOperationsMapReduce geneticOperationsMapReduce) {
        CrossoverPair[] pairs = new CrossoverPair[isl.getSizeOfIsland()];
        IndividualMapReduce[] ind = isl.getPopulation().getIndividualMapReduces();
        //calculate sum of fitness values and calculate probability of the selection of each individual
        isl.getPopulation().calculateSumOfFitness();
        double sumOfFitness = isl.getPopulation().getSumOfFitness();
        for (IndividualMapReduce individual : ind) {
            geneticOperationsMapReduce.rwsSelectionProbabilityCalculation(individual, sumOfFitness);
        }
        int head = 0;
        //if we have elitism implemented get fittest individual and set it as elite
        if (geneticOperationsMapReduce.isElitism()) {
            pairs[0] = getElite(isl);
            head = 1;
        }
        /* loop through the population and assign crossover pairs by performing Tournament selection
         * implemented in genetic operations class
         */
        for (int i = head; i < pairs.length; i++) {
            IndividualMapReduce parent1 = geneticOperationsMapReduce.rwsSelection(Arrays.asList(ind));
            IndividualMapReduce parent2 = geneticOperationsMapReduce.rwsSelection(Arrays.asList(ind));
            CrossoverPair pair = new CrossoverPair();
            pair.setParent1(parent1);
            pair.setParent2(parent2);
            pairs[i] = pair;
        }
        Population pairedPopulation = new Population(isl.getSizeOfIsland());
        pairedPopulation.setIndividualMapReduces(ind);
        Island pairedIsland = new Island(pairedPopulation, isl.getSizeOfIsland());
        pairedIsland.setCrossoverPairs(pairs);
        return pairedIsland;
    }

    /**
     * Gets fittest individual on the island and sets it as elite individual
     * @param isl island
     * @return crossover pair with elite individual
     */
    private CrossoverPair getElite(Island isl) {
        IndividualMapReduce elite = isl.getPopulation().getFittestIndividual();
        CrossoverPair elitePair = new CrossoverPair();
        elitePair.setEliteIndividual(elite);
        return elitePair;
    }
}
