package mapreduce;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.FitnessCalculator;
import geneticClasses.Population;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Michal Dorko on 09/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GlobalFile {

    private static Population newGeneration;
    private static List<BinaryIndividualMapReduce> binaryIndividualMapReduces;
    private static int maxFitness;
    private static int currentMaxFitness;
    private static int maxNotChanged = 0;
    private static boolean solutionFound;


    public static int getCurrentMaxFitness() {
        return currentMaxFitness;
    }

    public synchronized static void submitFitness(int currentMaxFitness) {
        if (currentMaxFitness >= GlobalFile.currentMaxFitness) {
            GlobalFile.currentMaxFitness = currentMaxFitness;
        }
    }

    public static int getMaxNotChanged() {
        return maxNotChanged;
    }

    public static void incrementMaxNotChanged() {
        maxNotChanged ++;
    }

    public static void resetMaxNotChanged() { maxNotChanged = 0;}

    public static void resetCurrentMax() {
        GlobalFile.currentMaxFitness = 0;
    }

    public static boolean isSolutionFound() {
        return solutionFound;
    }


    public static void setBinaryIndividualMapReduces(List<BinaryIndividualMapReduce> binaryIndividualMapReduces) {
        GlobalFile.binaryIndividualMapReduces = binaryIndividualMapReduces;
    }

    public static void setSolutionFound(boolean solutionFound) {
        GlobalFile.solutionFound = solutionFound;
    }

    public static int getMaxFitness() {
        return maxFitness;
    }

    public static void setMaxFitness(int maxFitness) {
        GlobalFile.maxFitness = maxFitness;
    }

    public static void createNewPopulation(int sizeOfPopulation) {
        newGeneration = new Population(sizeOfPopulation);
        binaryIndividualMapReduces = new ArrayList<>();
    }

    public static void assignNewGenerationToPopulation() {
        newGeneration.setBinaryIndividualMapReduces(binaryIndividualMapReduces.toArray(new BinaryIndividualMapReduce[binaryIndividualMapReduces.size()]));
        FitnessCalculator.calculateFitnessOfPopulation(newGeneration);
    }

    public static Population getNewGeneration() {
        return newGeneration;
    }

    public static int getSumOfFitnesses() {
        newGeneration.calculateSumOfFitnesses();
        return newGeneration.getSumOfFitnesses();
    }

    public static void clearListOfIndividuals() {
        binaryIndividualMapReduces.clear();
    }

    public static void setPopulation(Population p) {
        GlobalFile.newGeneration = p;
    }

    public static List<BinaryIndividualMapReduce> getBinaryIndividualMapReduces() {
        return binaryIndividualMapReduces;
    }

    public static int sizeOfGeneration() {
        return GlobalFile.binaryIndividualMapReduces.size();
    }

}
