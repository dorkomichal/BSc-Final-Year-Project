package main;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.CrossoverPair;
import geneticClasses.FitnessCalculator;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

/**
 * Created by Michal Dorko on 11/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class MapReduceBinaryGAMain {

    public static void main(String[] args) {
        Driver driver = Driver.getDriver();
        String solution = "011011100000110011110101010111010";
        BinaryIndividualMapReduce.setChromosomeLength(solution.length());
        driver.initializePopulation(50);
        FitnessCalculator.setProblemSolution(solution);
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(solution.length());

        JavaRDD<BinaryIndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();

        while (true) {
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<BinaryIndividualMapReduce, Integer> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation);
            if (GlobalFile.isSolutionFound()) {
                break;
            }
            BinaryIndividualMapReduce elite = mapper.getElite(populationWithFitness);
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite);
            JavaRDD<BinaryIndividualMapReduce> newGeneration = reducer.reduceCrossover(selectedIndividuals, true, 3);
            GlobalFile.setBinaryIndividualMapReduces(newGeneration.collect());
            parallelizedPopulation = driver.getJsc().parallelize(GlobalFile.getBinaryIndividualMapReduces());
            generationCounter++;
           /* for (BinaryIndividualMapReduce bi : newGeneration.collect()) {
                System.out.println("New Gen individual " + bi.toString());
            }*/
            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            GlobalFile.resetCurrentMax();
        }

        System.out.println("Solution Found " + GlobalFile.getNewGeneration().getFittestIndividual().toString());
    }
}
