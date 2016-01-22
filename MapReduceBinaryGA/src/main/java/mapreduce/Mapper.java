package mapreduce;

import geneticClasses.*;
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
        List<IndividualMapReduce> keys = populationWithFitness.keys().collect();
        JavaRDD<CrossoverPair> selectedIndividuals;
        if(method.equals(SelectionMethod.rouletteWheel)) {
            System.out.println("RWS");
            System.out.println("Sum of fitness: " + GlobalFile.getSumOfFitnesses());
            GeneticOperationsMapReduce.rwsSelectionProbabilityCalculation(keys, GlobalFile.getSumOfFitnesses());
            selectedIndividuals = populationWithFitness.map(ind -> rwsSelection(keys));
        } else {
            System.out.println("Tournament");
            selectedIndividuals = populationWithFitness.map(ind -> tournamentSelection(keys));
        }
        if (GeneticOperationsMapReduce.isElitism() && elite != null) {
            List<CrossoverPair> first = new ArrayList<>();
            List<CrossoverPair> eliteList = new ArrayList<>();
            CrossoverPair elitePair = new CrossoverPair();
            elitePair.setEliteIndividual(elite);
            first.add(selectedIndividuals.first());
            eliteList.add(elitePair);
            JavaRDD<CrossoverPair> eliterdd = Driver.getDriver().paralleliseData(eliteList);
            JavaRDD<CrossoverPair> replace = Driver.getDriver().paralleliseData(first);

            return selectedIndividuals.subtract(replace).union(eliterdd);
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
        JavaRDD<IndividualMapReduce> eliteInd = populationWithFitness.keys().filter(bi -> (bi.getFitness() == GlobalFile.getCurrentMaxFitness()));
        return eliteInd.collect().get(0);
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
