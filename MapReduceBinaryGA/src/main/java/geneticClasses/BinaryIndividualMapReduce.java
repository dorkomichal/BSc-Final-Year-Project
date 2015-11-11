package geneticClasses;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class BinaryIndividualMapReduce implements Serializable {

    /**
     * Default size of the chromosome. Can be changed using @setChromosomeLength
     */
    private static Integer chromosomeLength = 16;
    private byte[] chromosome;
    private int fitness;
    private double probabilityOfSelection;
    private Random random = new Random();

    public BinaryIndividualMapReduce() {
        this.chromosome = new byte[chromosomeLength];
        this.fitness = 0;
    }

    public void generateRandomIndividual() {
        for(int i = 0; i < chromosome.length; i++) {
            byte gene = (byte) (random.nextBoolean() ? 1 : 0);
            chromosome[i] = gene;
        }
    }
    public static void setChromosomeLength(Integer chromosomeLength) {
        BinaryIndividualMapReduce.chromosomeLength = chromosomeLength;
    }

    public byte[] getChromosome() {
        return chromosome;
    }

    public void setGene(byte gene, int index) {
        this.chromosome[index] = gene;
    }

    public int getFitness() {
        return fitness;
    }

    public int lengthOfChromosome() {
        return this.chromosome.length;
    }

    public void setChromosome(byte[] chromosome) {
        this.chromosome = chromosome;
    }

    public Integer calculateFitness() {
        this.fitness = FitnessCalculator.compareChromosomeAndSolution(chromosome);
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
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
