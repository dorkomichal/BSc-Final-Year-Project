package mapreduce;

import geneticClasses.*;
import org.apache.log4j.net.SyslogAppender;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
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

    public JavaPairRDD<IndividualMapReduce, Integer> mapCalculateFitness(JavaRDD<IndividualMapReduce> parallelizedPopulation) {
        JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness = parallelizedPopulation.mapToPair(ind -> new Tuple2<IndividualMapReduce, Integer>(ind, ind.calculateFitness()));
        JavaRDD<Integer> terminate = populationWithFitness.values().filter(v -> (v >= GlobalFile.getMaxFitness()));
        if (!terminate.isEmpty()) {
            GlobalFile.setSolutionFound(true);
        } else {
            GlobalFile.incrementMaxNotChanged();
        }
        return populationWithFitness;
    }

    public JavaRDD<CrossoverPair> mapSelection(JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness, IndividualMapReduce elite, SelectionMethod method) {
        JavaRDD<IndividualMapReduce> keys = populationWithFitness.keys();
        JavaRDD<CrossoverPair> selectedIndividuals;
        if(method.equals(SelectionMethod.rouletteWheel)) {
            System.out.println("RWS");
            System.out.println("Sum of fitness: " + GlobalFile.getSumOfFitnesses());
            JavaRDD<IndividualMapReduce> populationWithProbability = keys.map(bi -> GeneticOperationsMapReduce.rwsSelectionProbabilityCalculation(bi, GlobalFile.getSumOfFitnesses()));
            List<IndividualMapReduce> population = populationWithProbability.collect();
            selectedIndividuals = populationWithFitness.map(ind -> rwsSelection(population));
        } else {
            System.out.println("Tournament");
            List<IndividualMapReduce> population = keys.collect();
            selectedIndividuals = populationWithFitness.map(ind -> tournamentSelection(population));
        }
        if (GeneticOperationsMapReduce.isElitism() && elite != null) {
            List<CrossoverPair> eliteList = new ArrayList<>();
            CrossoverPair elitePair = new CrossoverPair();
            elitePair.setEliteIndividual(elite);
            eliteList.add(elitePair);
            JavaRDD<CrossoverPair> eliterdd = Driver.getDriver().paralleliseData(eliteList);
            return selectedIndividuals.union(eliterdd);
        } else {
            return  selectedIndividuals;
        }
    }

    private CrossoverPair tournamentSelection(List<IndividualMapReduce> population) {
            int[] randoms = new Random().ints(0, population.size()).distinct().limit(4).toArray();
            CrossoverPair crossoverPair = new CrossoverPair();
            IndividualMapReduce firstParent = GeneticOperationsMapReduce.tournamentSelection(population.get(randoms[0]), population.get(randoms[1]));
            firstParent.setCrossoverPair(crossoverPair);
            crossoverPair.setParent1(firstParent);
            IndividualMapReduce secondParent = GeneticOperationsMapReduce.tournamentSelection(population.get(randoms[2]), population.get(randoms[3]));
            secondParent.setCrossoverPair(crossoverPair);
            crossoverPair.setParent2(secondParent);
            return crossoverPair;
        }

    public IndividualMapReduce getElite(JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness) {
        int currentMaxFitness = GlobalFile.getCurrentMaxFitness();
        JavaRDD<IndividualMapReduce> eliteInd = populationWithFitness.keys().filter(bi -> bi.getFitness() >= currentMaxFitness);
        /*
         * Strange behaviour observed for next block of code. Sometimes the first if statement
         * returned false (RDD is not empty) however during the execution of else statement
         * runtime error occurred that collection was in fact empty. Therefore I collect RDD
         * first and check on driver size of the list
         */
        List<IndividualMapReduce> elites = eliteInd.take(1);
        if(elites.isEmpty()) {
           return null;
        } else {
            return elites.get(0);
        }
    }

    private CrossoverPair rwsSelection(List<IndividualMapReduce> population) {
        IndividualMapReduce parent1 = GeneticOperationsMapReduce.rwsSelection(population);
        IndividualMapReduce parent2 = GeneticOperationsMapReduce.rwsSelection(population);
        CrossoverPair pair = new CrossoverPair();
        pair.setParent1(parent1);
        pair.setParent2(parent2);
        return pair;
    }

}
