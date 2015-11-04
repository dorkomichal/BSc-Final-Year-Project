package geneticClasses;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GeneticAlgorithm {

    private static double crossoverRate = 0.7;
    private static double mutationRate = 0.001;
    private static boolean elitism = true;

    public static void setElitism(boolean elitism) {
        GeneticAlgorithm.elitism = elitism;
    }

    public static void setCrossoverRate(double crossoverRate) {
        GeneticAlgorithm.crossoverRate = crossoverRate;
    }

    public static void setMutationRate(double mutationRate) {
        GeneticAlgorithm.mutationRate = mutationRate;
    }

    /*
        TODO Implement Tournament selection and Stochastic Universal Sampling (SUS) selection methods
     */
    public static Population evolution(Population currentPopulation) {
        // TODO
        return null;
    }

    public static BinaryIndividual crossover(BinaryIndividual parent1, BinaryIndividual parent2) {
        // TODO
        return null;
    }


}
