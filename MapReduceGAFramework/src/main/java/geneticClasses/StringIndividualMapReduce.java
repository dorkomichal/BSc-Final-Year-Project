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

    private Integer chromosomeLength = 16;
    private String[] chromosome;
    private long fitness;
    private double probabilityOfSelection;
    private CrossoverPair crossoverPair;
    private String[] source;

    public StringIndividualMapReduce(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
        this.chromosome = new String[chromosomeLength];
        this.fitness = 0;
    }

    public String[] getSource() {
        return source;
    }

    public void setChromosomeLength(Integer chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
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
        this.chromosome = RandomStringUtils.randomAlphabetic(chromosomeLength).toUpperCase().split("");
    }

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

    @Override
    public Object[] getChromosome() {
        return this.chromosome;
    }

    @Override
    public void setGene(Object gene, int index) {
        this.chromosome[index] = (String) gene;
    }

    @Override
    public long getFitness() {
        return this.fitness;
    }

    @Override
    public int lengthOfChromosome() {
        return chromosomeLength;
    }

    @Override
    public void setChromosome(Object[] chromosome) {
        this.chromosome = (String[]) chromosome;
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
        return this.probabilityOfSelection;
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