package geneticClasses;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

/**
 * Created by Michal Dorko on 18/01/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class StringIndividual implements Individual {

    private static Integer chromosomeLength = 16;
    private String[] chromosome;
    private int fitness;
    private double probabilityOfSelection;

    public static void setChromosomeLength(Integer chromosomeLength) {
        StringIndividual.chromosomeLength = chromosomeLength;
    }

    public String[] getChromosome() {
        return chromosome;
    }

    public void setGene(Object gene, int index) {
        this.chromosome[index] = (String) gene;
    }

    public int getFitness() {
        return fitness;
    }

    public int lengthOfChromosome() {
        return chromosomeLength;
    }

    public void setChromosome(Object[] chromosome) {
        this.chromosome = (String[]) chromosome;
    }

    public void calculateFitness() {
        FitnessCalculator.compareChromosomeAndSolution(chromosome, this);
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public void generateRandomIndividual() {
        chromosome = RandomStringUtils.randomAlphabetic(chromosomeLength).toUpperCase().split("");
    }

    public double getProbabilityOfSelection() {
        return probabilityOfSelection;
    }

    public void setProbabilityOfSelection(double probabilityOfSelection) {
        this.probabilityOfSelection = probabilityOfSelection;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i]);
        }
        return stringBuilder.toString();
    }
}
