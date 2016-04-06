package globalparallelizationmodel;

import geneticClasses.CrossoverPair;
import geneticClasses.GeneticOperationsMapReduce;
import geneticClasses.IndividualMapReduce;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 09/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class ParallelCrossover implements Serializable {
    /**
     * Singleton instance of the ParallelCrossover
     */
    private static ParallelCrossover parallelCrossover;

    /**
     * Creates and/or returns singleton instance of the ParallelCrossover
     * @return singleton instance of the parallelCrossover
     */
    public static ParallelCrossover getParallelCrossover() {
        if (parallelCrossover == null) {
            parallelCrossover = new ParallelCrossover();
            return parallelCrossover;
        } else {
            return parallelCrossover;
        }
    }

    /**
     * This parallel crossover function is implemented by another map function which combines
     * pairs into new offspring using crossover method provided in genetic operation class
     * yielding new generation. It performs single-point or multipoint crossover.
     *
     * @param selectedIndividuals RDD of crossover pairs
     * @param multipoint boolean value indicating whether to apply single point or multi point crossover
     * @param numberOfCrossPoints number of crossover points if multipoint crossover is selected
     * @param geneticOperations instance of the class with all operations including crossover provided
     * @return new generation as RDD
     */
    public JavaRDD<IndividualMapReduce> parallelCrossover(JavaRDD<CrossoverPair> selectedIndividuals, boolean multipoint, int numberOfCrossPoints, GeneticOperationsMapReduce geneticOperations) {
        JavaRDD<IndividualMapReduce> newGen;
        if (multipoint) {
            newGen = selectedIndividuals.map(crossoverPair -> multiPointCrossover(crossoverPair, numberOfCrossPoints, geneticOperations));
        } else {
            newGen = selectedIndividuals.map(crossoverPair -> singlePointCrossover(crossoverPair, geneticOperations));
        }
        return newGen;
    }

    /**
     * Method that performs single-point crossover on the crossover pair
     * @param pair crossover pair
     * @param geneticOperations instance of the class with all operations including crossover provided
     * @return new individual created by crossover
     */
    private IndividualMapReduce singlePointCrossover(CrossoverPair pair, GeneticOperationsMapReduce geneticOperations) {
        if (pair.getEliteIndividual() != null) {
            return pair.getEliteIndividual();
        } else {
            IndividualMapReduce newIndividual = geneticOperations.singlePointCrossover(pair.getParent1(), pair.getParent2());
            return newIndividual;
        }
    }

    /**
     * Method for performing multipoint crossover on the crossover pair
     * @param pair crossover pair
     * @param numberOfPoints number of crossover points
     * @param geneticOperations instance of the class with all operations including crossover provided
     * @return new individual created by crossover
     */
    private IndividualMapReduce multiPointCrossover(CrossoverPair pair, int numberOfPoints, GeneticOperationsMapReduce geneticOperations) {
        if (pair.getEliteIndividual() != null) {
            return pair.getEliteIndividual();
        } else {
            IndividualMapReduce newIndividual = geneticOperations.multiPointCrossover(pair.getParent1(), pair.getParent2(), numberOfPoints);
            return newIndividual;
        }
    }

}
