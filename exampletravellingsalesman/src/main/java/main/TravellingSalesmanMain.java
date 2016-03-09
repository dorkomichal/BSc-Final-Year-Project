package main;

import geneticClasses.SelectionMethod;
import problemdescription.FitnessEval;

import java.util.List;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class TravellingSalesmanMain {

    public static void main(String[] args) {
        int numberOfTheCities = 1000;
        FitnessEval fitnessEval = new FitnessEval(numberOfTheCities, 90000);
        int chromosomeLength = numberOfTheCities;
        int populationSize = 100;
        int maxFit = 0;
        int numberOfTheGenerations = 1000;
        SelectionMethod selectionMethod = SelectionMethod.tournament;
        boolean multipoint = false;
        int numberOfCrossPoints = 0;

        GARunner gaRunner = GARunner.getGARunner(fitnessEval, null, chromosomeLength, populationSize, maxFit, numberOfTheGenerations,
                selectionMethod, multipoint, numberOfCrossPoints);
        Integer[] bestSolutionCities = (Integer[]) gaRunner.runGA();
    }

}
