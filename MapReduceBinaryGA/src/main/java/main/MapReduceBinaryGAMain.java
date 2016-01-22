package main;

import geneticClasses.*;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.util.SystemClock;
import problemsdesc.Satisfiability;

import java.util.Arrays;

/**
 * Created by Michal Dorko on 11/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class MapReduceBinaryGAMain {

    public static void main(String[] args) {
        FitnessFunction function = new FitnessFunction() {
            @Override
            public int calculateFitness(Object[] chromosome, IndividualMapReduce individual) {
                String stringChromosome = getStringFromByteArray((Byte[])chromosome);
                int threshold = 1000;
                int a = Integer.parseInt(stringChromosome.substring(0, 16), 2);
                int b = Integer.parseInt(stringChromosome.substring(16, stringChromosome.length()), 2);
                double calculation = Math.sqrt(a*5) + b;
                if (calculation >= threshold) {
                    return 0;
                } else {
                    return (int) Math.round(calculation);
                }
            }
        };
        Satisfiability sat = new Satisfiability(86, 20);
        FitnessCalculator.setFitnessFunction(sat);
        int variableLength = 16;
        int numberOfVariables = 2;
        Driver driver = Driver.getDriver();
        BinaryIndividualMapReduce.setChromosomeLength(20);
        driver.initializePopulation(10, IndividualType.Binary);
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(86);

        JavaRDD<IndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();

        while (true) {
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation);
            if (GlobalFile.isSolutionFound()) {
                break; //if soulution is found or generation has converged to max and didn't change for some generations
            }
            GlobalFile.resetMaxNotChanged();
            IndividualMapReduce elite = mapper.getElite(populationWithFitness);
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite, SelectionMethod.tournament);
            JavaRDD<IndividualMapReduce> newGeneration = reducer.reduceCrossover(selectedIndividuals, true, 2);
            GlobalFile.setIndividualMapReduces(newGeneration.collect());
            parallelizedPopulation = driver.paralleliseData(GlobalFile.getIndividualMapReduces());
            generationCounter++;

            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            //Important step for RWS selection is to reset max fitness of current generation
            //and assign new generation of the individuals to the population in order to calculate
            //aggregate fitness of the population necessary for RWS selection method
            GlobalFile.resetCurrentMax();
            GlobalFile.assignNewGenerationToPopulation();
        }

       /* System.out.println("Solution Found: ");
        String solution = GlobalFile.getNewGeneration().getFittestIndividual().toString();
        int a = Integer.parseInt(solution.substring(0, variableLength), 2);
        int b = Integer.parseInt(solution.substring(variableLength, solution.length()), 2);
        System.out.println("Variable a = " + a);
        System.out.println("Variable b = " + b);
        */
        System.out.println("Solution Found: ");
        System.out.println("Problem: \n");
        for (String[] s: sat.getExpressionString()) {
            System.out.println(Arrays.toString(s));
        }
        String solution = GlobalFile.getNewGeneration().getFittestIndividual().toString();
        for (int i = 0; i < solution.length(); i++) {
            System.out.println("P" + i + " = " + solution.substring(i,i+1));
        }

    }

    public static String getStringFromByteArray(Byte[] chromosome) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i]);
        }
        return stringBuilder.toString();
    }
}
