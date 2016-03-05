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
    private int fitness;
    private double probabilityOfSelection;
    private Random random = new Random();
    private CrossoverPair crossoverPair;
    private FitnessCalculator fitnessCalculator;

    public BinaryIndividualMapReduce(FitnessCalculator fc, Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new Byte[chromosomeLength];
        this.fitness = 0;
        this.fitnessCalculator = fc;
    }

    public CrossoverPair getCrossoverPair() {
        return crossoverPair;
    }

    public void setCrossoverPair(CrossoverPair crossoverPair) {
        this.crossoverPair = crossoverPair;
    }

    public void generateRandomIndividual() {
        for(int i = 0; i < chromosome.length; i++) {
            byte gene = (byte) (random.nextBoolean() ? 1 : 0);
            chromosome[i] = gene;
        }
    }

    @Override
    public void generateRandomIndividual(Object[] source) {
        for(int i = 0; i < chromosome.length; i++) {
            byte gene = (byte) (random.nextBoolean() ? 1 : 0);
            chromosome[i] = gene;
        }
    }

    public void setChromosomeLength(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

    public Byte[] getChromosome() {
        return chromosome;
    }

    public void setGene(Object gene, int index) {
        this.chromosome[index] = (Byte) gene;
    }

    public int getFitness() {
        return fitness;
    }

    public int lengthOfChromosome() {
        return this.chromosome.length;
    }

    public void setChromosome(Object[] chromosome) {
        this.chromosome = (Byte[]) chromosome;
    }

    public Integer calculateFitness() {
        this.fitness = fitnessCalculator.calculateFitness(chromosome, this);
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
