package globalparallelizationmodel;

import driver.Driver;
import driver.GlobalFile;
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
public class MapperFitnessSelection implements Serializable {
    /**
     * Singleton instance of the Mapper
     */
    private static final MapperFitnessSelection MAPPER_FITNESS_SELECTION = new MapperFitnessSelection();

    /**
     * Static method for getting singleton Mapper object
     * @return singleton Mapper object
     */
    public static MapperFitnessSelection getMapperFitnessSelection() {
        return MAPPER_FITNESS_SELECTION;
    }

    /**
     * Method for evaluating population using Spark map() operation to calculate fitness of each individual in the population.
     * It also finds fittest individual in the population and checks whether it's fitness is equal or better than goal fitness
     * set by user.
     * @param parallelizedPopulation population to be evaluated
     * @param fitnessCalculator instance of the fitness calculator with implemented fitness function for fitness evaluation
     * @return RDD of evaluated population
     */
    public JavaPairRDD<IndividualMapReduce, Long> mapCalculateFitness(JavaRDD<IndividualMapReduce> parallelizedPopulation, FitnessCalculator fitnessCalculator) {
        JavaPairRDD<IndividualMapReduce, Long> populationWithFitness = parallelizedPopulation.mapToPair(ind -> new Tuple2<IndividualMapReduce, Long>(ind, ind.calculateFitness(fitnessCalculator)));
        long currentMaxFitness = populationWithFitness.values().reduce(Math::max);
        GlobalFile.setCurrentMaxFitness(currentMaxFitness);
        long maxFitness = GlobalFile.getMaxFitness();
        JavaRDD<Long> terminate = populationWithFitness.values().filter(v -> (v >= maxFitness));
        if (!terminate.isEmpty()) {
            GlobalFile.setSolutionFound(true);
        }
        return populationWithFitness;
    }

    /**
     * Method for performing selection on the population using Global Parallelization Model. It performs selection specified by user.
     * Global Parallelization model requires whole population to be collected and returned to the driver so it can be passed as a parameter
     * to map() function that performs selection on the whole population. Selection is performed n times where n is number of the individuals
     * in the current population so we'll end up with n crossover pairs that are later processed to form new generation.
     * @param populationWithFitness RDD of the population with evaluated fitness
     * @param elite elite individual
     * @param method type of selection method
     * @param operations instance of the genetic operations class with all methods necessary for selection
     * @return RDD of CrossoverPairs ready for crossover
     */
    public JavaRDD<CrossoverPair> mapSelection(JavaPairRDD<IndividualMapReduce, Long> populationWithFitness, IndividualMapReduce elite, SelectionMethod method, GeneticOperationsMapReduce operations) {
        JavaRDD<IndividualMapReduce> keys = populationWithFitness.keys();

        JavaRDD<CrossoverPair> selectedIndividuals;
        if (method.equals(SelectionMethod.rouletteWheel)) {
            System.out.println("RWS");
            //collects population
            Iterator<IndividualMapReduce> populationIterator = keys.toLocalIterator();
            List<IndividualMapReduce> population = new ArrayList<>();
            while (populationIterator.hasNext()) {
                population.add(populationIterator.next());
            }
            long sumOfFitness = GlobalFile.getSumOfFitnesses(population);
            //performes RWS selection
            selectedIndividuals = populationWithFitness.map(ind -> rwsSelection(population, operations, sumOfFitness));
        } else {
            System.out.println("Tournament");
            Iterator<IndividualMapReduce> populationIterator = keys.toLocalIterator();
            List<IndividualMapReduce> population = new ArrayList<>();
            while (populationIterator.hasNext()) {
                population.add(populationIterator.next());
            }
            //tournament selection
            selectedIndividuals = populationWithFitness.map(ind -> tournamentSelection(population, operations));
        }
        //if elitism is used it replaces first crossover pair with the fittest individual from the current generation
        if (operations.isElitism() && elite != null) {
            List<CrossoverPair> eliteList = new ArrayList<>();
            CrossoverPair elitePair = new CrossoverPair();
            elitePair.setEliteIndividual(elite);
            eliteList.add(elitePair);
            JavaRDD<CrossoverPair> eliterdd = Driver.getDriver().paralleliseData(eliteList);
            JavaPairRDD<CrossoverPair, Long> zipped = selectedIndividuals.zipWithIndex();
            return zipped.filter(x -> x._2() != 0).keys().union(eliterdd);

        } else {
            return selectedIndividuals;
        }
    }

    /**
     * Performs Tournament selection optimised for map() phase and returns Crossover pair
     * @param population population on which selection should be performed
     * @param geneticOperations instance of the genetic operations class with all methods necessary for selection
     * @return crossover pair
     */
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

    /**
     * Method for obtaining elite individual from the population using Spark filter() operation.
     * @param populationWithFitness population with evaluated fitness
     * @return elite individual
     */
    public IndividualMapReduce getElite(JavaPairRDD<IndividualMapReduce, Long> populationWithFitness) {
        long currentMaxFitness = GlobalFile.getCurrentMaxFitness();
        JavaPairRDD<IndividualMapReduce, Long> eliteInd = populationWithFitness.filter(pair -> pair._2() >= currentMaxFitness);
        List<Tuple2<IndividualMapReduce, Long>> elites = eliteInd.take(1);
        if (elites.isEmpty()) {
            return null;
        } else {
            return elites.get(0)._1();
        }
    }

    /**
     * Method for performing Roulette Wheel Selection optimised for map() phase.
     * @param population population on which selection should be performed
     * @param geneticOperations instance of the genetic operations class with all methods necessary for selection
     * @param sumOfFitness sum of fitness values of the population
     * @return crossover pair
     */
    private CrossoverPair rwsSelection(List<IndividualMapReduce> population, GeneticOperationsMapReduce geneticOperations, double sumOfFitness) {
        population.stream().forEach(ind -> geneticOperations.rwsSelectionProbabilityCalculation(ind, sumOfFitness));
        IndividualMapReduce parent1 = geneticOperations.rwsSelection(population);
        IndividualMapReduce parent2 = geneticOperations.rwsSelection(population);
        CrossoverPair pair = new CrossoverPair();
        pair.setParent1(parent1);
        pair.setParent2(parent2);
        return pair;
    }

}
