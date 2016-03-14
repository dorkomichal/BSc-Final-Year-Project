package mapreduce;

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

    private static Population newGeneration;
    private static List<IndividualMapReduce> individualMapReduces;
    private static IndividualMapReduce fittestIndividual;
    private static long maxFitness;
    private static long currentMaxFitness;
    private static boolean solutionFound;


    public static long getCurrentMaxFitness() {
        return currentMaxFitness;
    }

    public static void submitMaxFitness(long fitness) {
        currentMaxFitness = fitness;
    }

    public static IndividualMapReduce getFittestIndividual() {
        return fittestIndividual;
    }

    public static void setFittestIndividual(IndividualMapReduce fittestIndividual) {
        GlobalFile.fittestIndividual = fittestIndividual;
    }

    public static void resetCurrentMax() {
        GlobalFile.currentMaxFitness = 0;
    }

    public static boolean isSolutionFound() {
        return solutionFound;
    }


    public static void setIndividualMapReduces(List<IndividualMapReduce> individualMapReduces) {
        GlobalFile.individualMapReduces = individualMapReduces;
    }

    public static void setSolutionFound(boolean solutionFound) {
        GlobalFile.solutionFound = solutionFound;
    }

    public static long getMaxFitness() {
        return maxFitness;
    }

    public static void setMaxFitness(int maxFitness) {
        GlobalFile.maxFitness = maxFitness;
    }

    public static void createNewPopulation(int sizeOfPopulation) {
        newGeneration = new Population(sizeOfPopulation);
        individualMapReduces = new ArrayList<>();
    }

    public static Population getNewGeneration() {
        return newGeneration;
    }

    public static int getSumOfFitnesses() {
        newGeneration.calculateSumOfFitnesses();
        return newGeneration.getSumOfFitnesses();
    }

    public static void clearListOfIndividuals() {
        individualMapReduces.clear();
    }

    public static void setPopulation(Population p) {
        GlobalFile.newGeneration = p;
    }

    public static List<IndividualMapReduce> getIndividualMapReduces() {
        return individualMapReduces;
    }

    public static int sizeOfGeneration() {
        return GlobalFile.individualMapReduces.size();
    }

}
