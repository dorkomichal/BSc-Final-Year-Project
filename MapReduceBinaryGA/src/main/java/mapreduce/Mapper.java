package mapreduce;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.GeneticAlgorithmMapReduce;
import org.apache.commons.beanutils.converters.IntegerArrayConverter;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.io.Serializable;
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

    public JavaRDD<BinaryIndividualMapReduce> mapSelection(JavaPairRDD<BinaryIndividualMapReduce, Integer> populationWithFitness) {
        List<BinaryIndividualMapReduce> keys = populationWithFitness.keys().collect();
        JavaRDD<BinaryIndividualMapReduce> selectedIndividuals = populationWithFitness.map(bi -> tournamentSelection(keys));
        return selectedIndividuals;
    }

    private BinaryIndividualMapReduce tournamentSelection(List<BinaryIndividualMapReduce> population) {
        int[] randoms = new Random().ints(0, population.size()).distinct().limit(2).toArray();
        return GeneticAlgorithmMapReduce.tournamentSelection(population.get(randoms[0]), population.get(randoms[1]));
    }
}
