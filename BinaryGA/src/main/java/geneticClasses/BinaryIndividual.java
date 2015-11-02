package geneticClasses;

import java.util.Random;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class BinaryIndividual {

    /**
     * Default size of the chromosome. Can be changed using @setChromosomeLength
     */
    private static Integer chromosomeLength = 256;
    private boolean[] chromosome;
    private int fitness;
    private Random random = new Random();

    public BinaryIndividual() {
        this.chromosome = new boolean[chromosomeLength];
        this.fitness = 0;
    }

    public void generateRandomIndividual() {
        for(int i = 0; i < chromosome.length; i++) {
            boolean gene = random.nextBoolean();
            chromosome[i] = gene;
        }
    }

    public static void setChromosomeLength(Integer chromosomeLength) {
        BinaryIndividual.chromosomeLength = chromosomeLength;
    }

    public boolean[] getChromosome() {
        return chromosome;
    }

    public void setGene(boolean gene, int index) {
        this.chromosome[index] = gene;
    }

    public int getFitness() {
        return fitness;
    }

    public int lengthOfGenome() {
        return this.chromosome.length;
    }

    public void calculateFitness() {
        // TODO
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i] ? 1 : 0);
        }
        return stringBuilder.toString();
    }
}
