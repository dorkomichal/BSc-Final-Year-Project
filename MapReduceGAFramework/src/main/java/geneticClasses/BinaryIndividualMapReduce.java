package geneticClasses;

import mapreduce.GlobalFile;

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
     * Default size of the chromosome. Can be changed using @setChromosomeLength
     */
    private Integer chromosomeLength = 16;
    private Byte[] chromosome;
    private long fitness;
    private double probabilityOfSelection;
    private Random random = new Random();
    private CrossoverPair crossoverPair;

    public BinaryIndividualMapReduce(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new Byte[chromosomeLength];
        this.fitness = 0;
    }
    @Override
    public CrossoverPair getCrossoverPair() {
        return crossoverPair;
    }
    @Override
    public void setCrossoverPair(CrossoverPair crossoverPair) {
        this.crossoverPair = crossoverPair;
    }
    @Override
    public void generateRandomIndividual() {
        for(int i = 0; i < chromosomeLength; i++) {
            byte gene = (byte) (random.nextBoolean() ? 1 : 0);
            chromosome[i] = gene;
        }
    }

    @Override
    public void generateRandomIndividual(Object[] source) {
        this.generateRandomIndividual();
    }

    public void setChromosomeLength(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }
    @Override
    public Byte[] getChromosome() {
        return chromosome;
    }
    @Override
    public void setGene(Object gene, int index) {
        this.chromosome[index] = (Byte) gene;
    }
    @Override
    public long getFitness() {
        return fitness;
    }
    @Override
    public int lengthOfChromosome() {
        return this.chromosomeLength;
    }
    @Override
    public void setChromosome(Object[] chromosome) {
        this.chromosome = (Byte[]) chromosome;
    }
    @Override
    public long calculateFitness(FitnessCalculator fitnessCalculator) {
        this.fitness = fitnessCalculator.calculateFitness(chromosome, this);
        return fitness;
    }
    @Override
    public void setFitness(long fitness) {
        this.fitness = fitness;
    }
    @Override
    public double getProbabilityOfSelection() {
        return probabilityOfSelection;
    }
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
