package main;

import geneticClasses.IndividualType;
import geneticClasses.SelectionMethod;
import problemdescription.FitnessEval;

import java.util.Arrays;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class TravellingSalesmanMain {

    public static void main(String[] args) {
        int numberOfTheCities = 10;
        FitnessEval fitnessEval = new FitnessEval(numberOfTheCities, 900);
        int chromosomeLength = numberOfTheCities;
        int populationSize = 20;
        int maxFit = 0;
        int numberOfTheGenerations = 30;
        SelectionMethod selectionMethod = SelectionMethod.tournament;
        boolean multipoint = false;
        int numberOfCrossPoints = 0;

        GARunner.setEnableStatistics(true);
        GARunner gaRunner = GARunner.getGARunner(fitnessEval, IndividualType.IntegerPermutation, null, chromosomeLength, populationSize, maxFit, numberOfTheGenerations,
                selectionMethod, multipoint, numberOfCrossPoints);
        Integer[] bestSolutionCities = (Integer[]) gaRunner.runGA();
        fitnessEval.getCities().stream().forEach(System.out::println);
        System.out.println("Solution: " + Arrays.toString(bestSolutionCities));
        System.out.println("Mean");
        gaRunner.getMean().stream().forEach(x -> System.out.print( x+ ","));
        System.out.println("\nStandard Deviation");
        gaRunner.getStd().stream().forEach(x -> System.out.print(x + ","));
        System.out.println("\nStandard Error");
        gaRunner.getStandardError().stream().forEach(x -> System.out.print(x + ","));
        System.out.println("\nAverageFitness over runs: " + gaRunner.getAverageFitnessOverGenerations());
        System.out.println("Number of individuals in final population with optimal fitness: " + gaRunner.getLastGenerationMaxFitness());

    }

}
