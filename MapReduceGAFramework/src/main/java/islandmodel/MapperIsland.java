package islandmodel;

import geneticClasses.*;
import mapreduce.GlobalFile;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
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
public class MapperIsland implements Serializable {

    private static MapperIsland mapperIsland;

    public static MapperIsland getMapperIsland() {
        if (mapperIsland == null) {
            mapperIsland = new MapperIsland();
            return mapperIsland;
        } else {
            return mapperIsland;
        }
    }

    public JavaPairRDD<Island, Long> mapCalculateFitness(JavaRDD<Island> parallelizedIslandPop, FitnessCalculator fitnessCalculator) {
        JavaPairRDD<Island, Long> populationWithFitness = parallelizedIslandPop.mapToPair(isl -> evaluateFitness(isl, fitnessCalculator));
        System.out.println(populationWithFitness.values().collect().toString());
        long currentMaxFitness = populationWithFitness.values().reduce(Math::max);
        System.out.println("After Reduce: " + currentMaxFitness);
        GlobalFile.submitMaxFitness(currentMaxFitness);
        long maxFitness = GlobalFile.getMaxFitness();
        JavaRDD<Long> terminate = populationWithFitness.values().filter(v -> (v >= maxFitness));
        if (!terminate.isEmpty()) {
            GlobalFile.setSolutionFound(true);
        }
        return populationWithFitness;
    }

    private Tuple2<Island, Long> evaluateFitness(Island isl, FitnessCalculator fitnessCalculator) {
        long maxFitness = Long.MIN_VALUE;
        Population population = isl.getPopulation();
        IndividualMapReduce[] individuals = population.getIndividualMapReduces();
        for(IndividualMapReduce ind : individuals) {
            long fitness = ind.calculateFitness(fitnessCalculator);
            if (fitness >= maxFitness) {
                maxFitness = fitness;
            }
        }
        isl.getPopulation().setIndividualMapReduces(individuals);
        return new Tuple2<>(isl, maxFitness);
    }

    public JavaRDD<Island> mapSelection(JavaPairRDD<Island,Long> populationWithFitness, SelectionMethod method, GeneticOperationsMapReduce operations) {
        JavaRDD<Island> islands = populationWithFitness.keys();
        JavaRDD<Island> islandsWithPairs;
        if(method.equals(SelectionMethod.tournament)) {
            islandsWithPairs = islands.map(island -> tournamentSelection(island, operations));
        } else {
            islandsWithPairs = islands.map(island -> rwsSelection(island, operations));
        }
        return islandsWithPairs;
    }

    private Island tournamentSelection(Island isl, GeneticOperationsMapReduce operationsMapReduce) {
        CrossoverPair[] pairs = new CrossoverPair[isl.getSizeOfIsland()];
        IndividualMapReduce[] ind = isl.getPopulation().getIndividualMapReduces();
        int head = 0;
        if (operationsMapReduce.isElitism()) {
            pairs[0] = getElite(isl);
            head = 1;
        }
        for (int i = head; i < pairs.length; i++) {
            int[] randoms = new Random().ints(0, ind.length).distinct().limit(4).toArray();
            IndividualMapReduce parent1 = operationsMapReduce.tournamentSelection(ind[randoms[0]], ind[randoms[1]]);
            IndividualMapReduce parent2 = operationsMapReduce.tournamentSelection(ind[randoms[2]], ind[randoms[3]]);
            CrossoverPair pair = new CrossoverPair();
            pair.setParent1(parent1);
            pair.setParent2(parent2);
            pairs[i] = pair;
        }
      isl.setCrossoverPairs(pairs);
      return isl;
    }

    private Island rwsSelection(Island isl, GeneticOperationsMapReduce geneticOperationsMapReduce) {
        CrossoverPair[] pairs = new CrossoverPair[isl.getSizeOfIsland()];
        IndividualMapReduce[] ind = isl.getPopulation().getIndividualMapReduces();
        isl.getPopulation().calculateSumOfFitnesses();
        double sumOfFitnesses = isl.getPopulation().getSumOfFitnesses();
        for (IndividualMapReduce individual: ind) {
            geneticOperationsMapReduce.rwsSelectionProbabilityCalculation(individual, sumOfFitnesses);
        }
        int head = 0;
        if (geneticOperationsMapReduce.isElitism()) {
            pairs[0] = getElite(isl);
            head = 1;
        }
        for (int i = head; i < pairs.length; i++) {
            IndividualMapReduce parent1 = geneticOperationsMapReduce.rwsSelection(Arrays.asList(ind));
            IndividualMapReduce parent2 = geneticOperationsMapReduce.rwsSelection(Arrays.asList(ind));
            CrossoverPair pair = new CrossoverPair();
            pair.setParent1(parent1);
            pair.setParent2(parent2);
            pairs[i] = pair;
        }
        isl.setCrossoverPairs(pairs);
        return isl;
    }

    private CrossoverPair getElite(Island isl) {
        IndividualMapReduce elite = isl.getPopulation().getFittestIndividual();
        CrossoverPair elitePair = new CrossoverPair();
        elitePair.setEliteIndividual(elite);
        return elitePair;
    }
}
