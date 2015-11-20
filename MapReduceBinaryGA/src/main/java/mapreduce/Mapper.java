package mapreduce;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.CrossoverPair;
import geneticClasses.GeneticAlgorithmMapReduce;
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

    public JavaPairRDD<BinaryIndividualMapReduce, Integer> mapCalculateFitness(JavaRDD<BinaryIndividualMapReduce> parallelizedPopulation) {
        JavaPairRDD<BinaryIndividualMapReduce, Integer> populationWithFitness = parallelizedPopulation.mapToPair(bi -> new Tuple2<BinaryIndividualMapReduce, Integer>(bi, bi.calculateFitness()));
        JavaRDD<Integer> terminate = populationWithFitness.values().filter(v -> (v >= GlobalFile.getMaxFitness()));
        if (!terminate.isEmpty()) {
            GlobalFile.setSolutionFound(true);
        }
        return populationWithFitness;
    }

    public JavaRDD<CrossoverPair> mapSelection(JavaPairRDD<BinaryIndividualMapReduce, Integer> populationWithFitness, BinaryIndividualMapReduce elite) {
        List<BinaryIndividualMapReduce> keys = populationWithFitness.keys().collect();
        JavaRDD<CrossoverPair> selectedIndividuals = populationWithFitness.map(bi -> tournamentSelection(keys));
        if (GeneticAlgorithmMapReduce.isElitism() && elite != null) {
            List<CrossoverPair> first = new ArrayList<>();
            List<CrossoverPair> eliteList = new ArrayList<>();
            CrossoverPair elitePair = new CrossoverPair();
            elitePair.setEliteIndividual(elite);
            first.add(selectedIndividuals.first());
            eliteList.add(elitePair);
            JavaRDD<CrossoverPair> eliterdd = Driver.getDriver().getJsc().parallelize(eliteList);
            JavaRDD<CrossoverPair> replace = Driver.getDriver().getJsc().parallelize(first);

            return selectedIndividuals.subtract(replace).union(eliterdd);
        } else {
            return  selectedIndividuals;
        }
    }

    private CrossoverPair tournamentSelection(List<BinaryIndividualMapReduce> population) {
            int[] randoms = new Random().ints(0, population.size()).distinct().limit(4).toArray();
            CrossoverPair crossoverPair = new CrossoverPair();
            BinaryIndividualMapReduce firstParent = GeneticAlgorithmMapReduce.tournamentSelection(population.get(randoms[0]), population.get(randoms[1]));
            firstParent.setCrossoverPair(crossoverPair);
            crossoverPair.setParent1(firstParent);
            BinaryIndividualMapReduce secondParent = GeneticAlgorithmMapReduce.tournamentSelection(population.get(randoms[2]), population.get(randoms[3]));
            secondParent.setCrossoverPair(crossoverPair);
            crossoverPair.setParent2(secondParent);
            return crossoverPair;
        }

    public BinaryIndividualMapReduce getElite(JavaPairRDD<BinaryIndividualMapReduce, Integer> populationWithFitness) {
        JavaRDD<BinaryIndividualMapReduce> eliteInd = populationWithFitness.keys().filter(bi -> (bi.getFitness() == GlobalFile.getCurrentMaxFitness()));
        return eliteInd.collect().get(0);
    }



}
