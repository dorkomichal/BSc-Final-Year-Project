package driver;

import geneticClasses.IndividualMapReduce;
import geneticClasses.Population;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Dorko on 09/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GlobalFile {

    /**
     * This class stores global states used mostly by Global Parallelization model but also Island Model which
     * uses boolean flag to state whether optimal solution has been found or not
     */

    /**
     * New generation
     */
    private static Population newGeneration;
    /**
     * List of individuals
     */
    private static List<IndividualMapReduce> individualMapReduces;
    /**
     * Fittest individual
     */
    private static IndividualMapReduce fittestIndividual;
    /**
     * Goal fitness to be achieved
     */
    private static long maxFitness;
    /**
     * Max fitness in current generation
     */
    private static long currentMaxFitness;
    /**
     * Flags whether solution has been found
     */
    private static boolean solutionFound;

    /**
     * Getter for max fitness in current generation
     * @return max fitness in current generation
     */
    public static long getCurrentMaxFitness() {
        return currentMaxFitness;
    }

    /**
     * Setter for max fitness in current generation
     * @param fitness max fitness in current generation
     */
    public static void setCurrentMaxFitness(long fitness) {
        currentMaxFitness = fitness;
    }

    /**
     * Getter for fittest individual
     * @return fittest individual
     */
    public static IndividualMapReduce getFittestIndividual() {
        return fittestIndividual;
    }

    /**
     * Setter for fittest individual
     * @param fittestIndividual fittest individual
     */
    public static void setFittestIndividual(IndividualMapReduce fittestIndividual) {
        GlobalFile.fittestIndividual = fittestIndividual;
    }

    /**
     * Resets max fitness in current generation to 0
     */
    public static void resetCurrentMax() {
        GlobalFile.currentMaxFitness = 0;
    }

    /**
     * Getter for boolean flag solution found
     * @return true is solution has been found false otherwise
     */
    public static boolean isSolutionFound() {
        return solutionFound;
    }

    /**
     * Sets solution found flag
     * @param solutionFound boolean flag for solution found
     */
    public static void setSolutionFound(boolean solutionFound) {
        GlobalFile.solutionFound = solutionFound;
    }

    /**
     * Getter for goal fitness
     * @return goal fitness
     */
    public static long getMaxFitness() {
        return maxFitness;
    }

    /**
     * Setter for goal fitness
     * @param maxFitness goal fitness
     */
    public static void setMaxFitness(int maxFitness) {
        GlobalFile.maxFitness = maxFitness;
    }

    /**
     * Getter for sum of fitness values
     * @param population population on which sum will be performed
     * @return sum of fitness values
     */
    public static long getSumOfFitnesses(List<IndividualMapReduce> population) {
        newGeneration = new Population(population.size());
        individualMapReduces = new ArrayList<>();
        newGeneration.setIndividualMapReduces(population.toArray(new IndividualMapReduce[population.size()]));
        newGeneration.calculateSumOfFitness();
        return newGeneration.getSumOfFitness();
    }

    /**
     * Sets new population
     * @param p population
     */
    public static void setPopulation(Population p) {
        GlobalFile.newGeneration = p;
    }



}
