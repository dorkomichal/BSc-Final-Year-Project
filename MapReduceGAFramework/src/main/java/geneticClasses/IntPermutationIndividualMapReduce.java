package geneticClasses;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class IntPermutationIndividualMapReduce implements IndividualMapReduce {
    /**
     * Length of the chromosome
     */
    private Integer chromosomeLength;
    /**
     * Actual representation of the chromosome
     */
    private Integer[] chromosome;
    /**
     * Fitness of the individual
     */
    private long fitness;
    /**
     * Probability of the selection used for RWS selection
     */
    private double probabilityOfSelection;
    /**
     * Crossover pair to which this individual is assigned to during selection
     */
    private CrossoverPair crossoverPair;
    private Random random = new Random();

    /**
     * Creates new individual with set chromosome length. Chromosome of this individual must
     * be either initialized by random generation or set with appropriate setter method
     * @param chromosomeLength length of the chromosome
     */
    public IntPermutationIndividualMapReduce(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new Integer[chromosomeLength];
        this.fitness = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CrossoverPair getCrossoverPair() {
        return this.crossoverPair;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCrossoverPair(CrossoverPair crossoverPair) {
        this.crossoverPair = crossoverPair;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void generateRandomIndividual() {
        chromosome = new Integer[chromosomeLength];
        /*
         * Generating array of integer values from 0 to chromosomeLength
         */
        for (int i = 0; i < chromosomeLength; i++) {
            chromosome[i] = i;
        }
        /*
         * Fisher–Yates shuffle of the array of possible values
         */
        for (int i = chromosome.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Simple swap
            int a = chromosome[index];
            chromosome[index] = chromosome[i];
            chromosome[i] = a;
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void generateRandomIndividual(Object[] source) {
        this.generateRandomIndividual();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getChromosome() {
        return this.chromosome;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGene(Object gene, int index) {
        this.chromosome[index] = (Integer) gene;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getFitness() {
        return this.fitness;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int lengthOfChromosome() {
        return this.chromosomeLength;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setChromosome(Object[] chromosome) {
        this.chromosome = (Integer[]) chromosome;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long calculateFitness(FitnessCalculator fitnessCalculator) {
        this.fitness = fitnessCalculator.calculateFitness(chromosome, this);
        return this.fitness;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setFitness(long fitness) {
        this.fitness = fitness;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public double getProbabilityOfSelection() {
        return this.probabilityOfSelection;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setProbabilityOfSelection(double probabilityOfSelection) {
        this.probabilityOfSelection = probabilityOfSelection;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.chromosome);
    }
}
