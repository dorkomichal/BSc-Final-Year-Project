package geneticClasses;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Michal Dorko on 20/01/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class StringIndividualMapReduce implements Serializable, IndividualMapReduce {

    /**
     * Lenght of the chromosome
     */
    private Integer chromosomeLength;
    /**
     * Actual representation of the chromosome
     */
    private String[] chromosome;
    /**
     * Fitness of the individual
     */
    private long fitness;
    /**
     * Probability of the selection
     */
    private double probabilityOfSelection;
    /**
     * Crossover pair this individual is assigned to during the selection
     */
    private CrossoverPair crossoverPair;
    /**
     * Set of allowed characters for each gene within chromosome
     */
    private String[] source;

    /**
     * Creates new individual with set chromosome length. Chromosome of this individual must
     * be either initialized by random generation or set with appropriate setter method
     * @param chromosomeLength length of the chromosome
     */
    public StringIndividualMapReduce(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new String[chromosomeLength];
        this.fitness = 0;
    }

    public String[] getSource() {
        return source;
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
        this.chromosome = RandomStringUtils.randomAlphabetic(chromosomeLength).toUpperCase().split("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateRandomIndividual(Object[] source) {
        this.source = (String[]) source;
        this.chromosome = new String[chromosomeLength];
        Random random = new Random();
        for (int i = 0; i < chromosomeLength; i++) {
            int ran = random.nextInt(source.length);
            this.chromosome[i] = (String) source[ran];
        }
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
        this.chromosome[index] = (String) gene;
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
        return chromosomeLength;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setChromosome(Object[] chromosome) {
        this.chromosome = (String[]) chromosome;
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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chromosome.length; i++) {
            stringBuilder.append(chromosome[i]);
        }
        return stringBuilder.toString();
    }
}
