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
public class BinaryIndividualMapReduce implements Serializable, IndividualMapReduce {

    /**
     * Chromosome length
     */
    private Integer chromosomeLength;
    /**
     * Actual representation of the chromosome
     */
    private Byte[] chromosome;
    /**
     * Fitness of the individual
     */
    private long fitness;
    /**
     * Probability of the selection used in RWS selection
     */
    private double probabilityOfSelection;
    private Random random = new Random();
    /**
     * Crossover pair this individual is assigned to
     */
    private CrossoverPair crossoverPair;

    /**
     * Creates new individual with chromosome length passed as parameter,
     * sets fitness to 0 and initialises chromosome array
     *
     * @param chromosomeLength length of the chromosome
     */
    public BinaryIndividualMapReduce(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new Byte[chromosomeLength];
        this.fitness = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CrossoverPair getCrossoverPair() {
        return crossoverPair;
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
        for (int i = 0; i < chromosomeLength; i++) {
            byte gene = (byte) (random.nextBoolean() ? 1 : 0);
            chromosome[i] = gene;
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
    public Byte[] getChromosome() {
        return chromosome;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGene(Object gene, int index) {
        this.chromosome[index] = (Byte) gene;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFitness() {
        return fitness;
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
        this.chromosome = (Byte[]) chromosome;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long calculateFitness(FitnessCalculator fitnessCalculator) {
        this.fitness = fitnessCalculator.calculateFitness(chromosome, this);
        return fitness;
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
        return probabilityOfSelection;
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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i]);
        }
        return stringBuilder.toString();
    }
}
