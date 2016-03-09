package geneticClasses;

import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class IntPermutationIndividualMapReduce implements IndividualMapReduce {

    private Integer chromosomeLength;
    private Integer[] chromosome;
    private int fitness;
    private double probabilityOfSelection;
    private CrossoverPair crossoverPair;
    private Random random = new Random();

    public IntPermutationIndividualMapReduce(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new Integer[chromosomeLength];
        this.fitness = 0;
    }


    @Override
    public CrossoverPair getCrossoverPair() {
        return this.crossoverPair;
    }

    @Override
    public void setCrossoverPair(CrossoverPair crossoverPair) {
        this.crossoverPair = crossoverPair;
    }

    @Override
    public void generateRandomIndividual() {
        chromosome = new Integer[chromosomeLength];
        /*
         * Generating array of integer values from 1 to chromosomeLength
         */
        for (int i = 0; i < chromosomeLength; i++) {
            chromosome[i] = i+1;
        }
        /*
         * Fisher–Yates shuffle of the array of possible values
         */
        for (int i = chromosome.length - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);
            // Simple swap
            int a = chromosome[index];
            chromosome[index] = chromosome[i];
            chromosome[i] = a;
        }
    }

    @Override
    public void generateRandomIndividual(Object[] source) {
        this.generateRandomIndividual();
    }

    @Override
    public Object[] getChromosome() {
        return this.chromosome;
    }

    @Override
    public void setGene(Object gene, int index) {
        this.chromosome[index] = (Integer) gene;
    }

    @Override
    public int getFitness() {
        return this.fitness;
    }

    @Override
    public int lengthOfChromosome() {
        return this.chromosomeLength;
    }

    @Override
    public void setChromosome(Object[] chromosome) {
        this.chromosome = (Integer []) chromosome;
    }

    @Override
    public Integer calculateFitness(FitnessCalculator fitnessCalculator) {
        this.fitness = fitnessCalculator.calculateFitness(chromosome, this);
        return this.fitness;
    }

    @Override
    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    @Override
    public double getProbabilityOfSelection() {
        return this.probabilityOfSelection;
    }

    @Override
    public void setProbabilityOfSelection(double probabilityOfSelection) {
        this.probabilityOfSelection = probabilityOfSelection;
    }
}
