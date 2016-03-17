package mapreduce;

import geneticClasses.*;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Michal Dorko on 09/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Mapper implements Serializable {

    private static final Mapper mapper = new Mapper();

    public static Mapper getMapper() {
        return mapper;
    }

    public JavaPairRDD<IndividualMapReduce, Long> mapCalculateFitness(JavaRDD<IndividualMapReduce> parallelizedPopulation, FitnessCalculator fitnessCalculator) {
        JavaPairRDD<IndividualMapReduce, Long> populationWithFitness = parallelizedPopulation.mapToPair(ind -> new Tuple2<IndividualMapReduce, Long>(ind, ind.calculateFitness(fitnessCalculator)));
        long currentMaxFitness = populationWithFitness.values().reduce(Math::max);
        GlobalFile.submitMaxFitness(currentMaxFitness);
        long maxFitness = GlobalFile.getMaxFitness();
        JavaRDD<Long> terminate = populationWithFitness.values().filter(v -> (v >= maxFitness));
        if (!terminate.isEmpty()) {
            GlobalFile.setSolutionFound(true);
        }
        return populationWithFitness;
    }

    public JavaRDD<CrossoverPair> mapSelection(JavaPairRDD<IndividualMapReduce, Long> populationWithFitness, IndividualMapReduce elite, SelectionMethod method, GeneticOperationsMapReduce operations) {
        JavaRDD<IndividualMapReduce> keys = populationWithFitness.keys();

        JavaRDD<CrossoverPair> selectedIndividuals;
        if(method.equals(SelectionMethod.rouletteWheel)) {
            System.out.println("RWS");
            Iterator<IndividualMapReduce> populationIterator = keys.toLocalIterator();
            List<IndividualMapReduce> population = new ArrayList<>();
            while(populationIterator.hasNext()) {
                population.add(populationIterator.next());
            }
            long sumOfFitnesses = GlobalFile.getSumOfFitnesses(population);
            JavaRDD<IndividualMapReduce> populationWithProbability = keys.map(bi -> operations.rwsSelectionProbabilityCalculation(bi, sumOfFitnesses));
            selectedIndividuals = populationWithFitness.map(ind -> rwsSelection(population, operations));
        } else {
            System.out.println("Tournament");
            Iterator<IndividualMapReduce> populationIterator = keys.toLocalIterator();
            List<IndividualMapReduce> population = new ArrayList<>();
            while(populationIterator.hasNext()) {
                population.add(populationIterator.next());
            }
            selectedIndividuals = populationWithFitness.map(ind -> tournamentSelection(population, operations));
        }
        if (operations.isElitism() && elite != null) {
            List<CrossoverPair> eliteList = new ArrayList<>();
            CrossoverPair elitePair = new CrossoverPair();
            elitePair.setEliteIndividual(elite);
            eliteList.add(elitePair);
            JavaRDD<CrossoverPair> eliterdd = Driver.getDriver().paralleliseData(eliteList);
            JavaPairRDD<CrossoverPair, Long> zipped = selectedIndividuals.zipWithIndex();
            return zipped.filter(x -> x._2()!= 0).keys().union(eliterdd);

        } else {
            return  selectedIndividuals;
        }
    }

    private CrossoverPair tournamentSelection(List<IndividualMapReduce> population, GeneticOperationsMapReduce geneticOperations) {
            int[] randoms = new Random().ints(0, population.size()).distinct().limit(4).toArray();
            CrossoverPair crossoverPair = new CrossoverPair();
            IndividualMapReduce firstParent = geneticOperations.tournamentSelection(population.get(randoms[0]), population.get(randoms[1]));
            firstParent.setCrossoverPair(crossoverPair);
            crossoverPair.setParent1(firstParent);
            IndividualMapReduce secondParent = geneticOperations.tournamentSelection(population.get(randoms[2]), population.get(randoms[3]));
            secondParent.setCrossoverPair(crossoverPair);
            crossoverPair.setParent2(secondParent);
            return crossoverPair;
        }

    public IndividualMapReduce getElite(JavaPairRDD<IndividualMapReduce, Long> populationWithFitness) {
        long currentMaxFitness = GlobalFile.getCurrentMaxFitness();
        JavaPairRDD<IndividualMapReduce, Long> eliteInd = populationWithFitness.filter(pair -> pair._2() >= currentMaxFitness);
        /*
         * Strange behaviour observed for next block of code. Sometimes the first if statement
         * returned false (RDD is not empty) however during the execution of else statement
         * runtime error occurred that collection was in fact empty. Therefore I collect RDD
         * first and check on driver size of the list
         */
        List<Tuple2<IndividualMapReduce,Long>> elites = eliteInd.take(1);
        if(elites.isEmpty()) {
           return null;
        } else {
            return elites.get(0)._1();
        }
    }

    private CrossoverPair rwsSelection(List<IndividualMapReduce> population, GeneticOperationsMapReduce geneticOperations) {
        IndividualMapReduce parent1 = geneticOperations.rwsSelection(population);
        IndividualMapReduce parent2 = geneticOperations.rwsSelection(population);
        CrossoverPair pair = new CrossoverPair();
        pair.setParent1(parent1);
        pair.setParent2(parent2);
        return pair;
    }

}
