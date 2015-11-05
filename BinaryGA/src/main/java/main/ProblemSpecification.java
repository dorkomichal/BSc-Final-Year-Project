package main;

import geneticClasses.FitnessCalculator;
import geneticClasses.GeneticAlgorithm;
import geneticClasses.Population;

/**
 * Created by Michal Dorko on 14/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class ProblemSpecification {

    public static void main(String[] args) {
        Population population = new Population(50);
        population.initializePopulation();
        String solution = "0110111000001100";
        FitnessCalculator.setProblemSolution(solution);
        int generation = 1;
        while (population.getFittestIndividual().getFitness() < solution.length()) {
            System.out.println("Generation number: " + generation);
            System.out.println("Fittest Individual: " + population.getFittestIndividual().getFitness());
            population = GeneticAlgorithm.evolveWithMultiPoint(population, 5);
            generation ++;
        }
        System.out.println("Generation number: " + generation);
        System.out.println("Fittest Individual: " + population.getFittestIndividual().getFitness());
        System.out.println("Solution: " + population.getFittestIndividual().toString());
    }
}
