package main;

import geneticClasses.BinaryIndividualMapReduce;
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
        driver.initializePopulation(50);
        String solution = "0110111000001100";
        FitnessCalculator.setProblemSolution(solution);
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(solution.length());

        JavaRDD<BinaryIndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();

        while (!GlobalFile.isSolutionFound()) {
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<BinaryIndividualMapReduce, Integer> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation);
            System.out.println("Population with Fitness " + populationWithFitness.count());
            JavaRDD<BinaryIndividualMapReduce> selectedIndividuals = mapper.mapSelection(populationWithFitness);
            System.out.println("Selected individuals " + selectedIndividuals.count());
            reducer.reduceCrossover(selectedIndividuals);
            parallelizedPopulation = driver.getJsc().parallelize(GlobalFile.getBinaryIndividualMapReduces());
            generationCounter++;
            System.out.println("Population size " + GlobalFile.getBinaryIndividualMapReduces().size());
        }

        System.out.println("Generation " + generationCounter);
        System.out.println("Solution Found " + GlobalFile.getNewGeneration().getFittestIndividual().toString());
    }
}
