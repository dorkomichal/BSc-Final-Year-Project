package main;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.CrossoverPair;
import geneticClasses.FitnessCalculator;
import geneticClasses.SelectionMethod;
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
        String solution = "011011100000110010011110101011001";
        FitnessCalculator.setProblemSolution(solution);
        Driver driver = Driver.getDriver();
        BinaryIndividualMapReduce.setChromosomeLength(solution.length());
        driver.initializePopulation(50);
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
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite, SelectionMethod.tournament);
            JavaRDD<BinaryIndividualMapReduce> newGeneration = reducer.reduceCrossover(selectedIndividuals, true, 2);
            GlobalFile.setBinaryIndividualMapReduces(newGeneration.collect());
            parallelizedPopulation = driver.getJsc().parallelize(GlobalFile.getBinaryIndividualMapReduces());
            generationCounter++;

            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            //Important step for RWS selection is to reset max fitness of current generation
            //and assign new generation of the individuals to the population in order to calculate
            //aggregate fitness of the population necessary for RWS selection method
            GlobalFile.resetCurrentMax();
            GlobalFile.assignNewGenerationToPopulation();
        }

        System.out.println("Solution Found " + GlobalFile.getNewGeneration().getFittestIndividual().toString());
    }
}
