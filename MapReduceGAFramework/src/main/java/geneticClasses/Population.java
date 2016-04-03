package geneticClasses;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Population implements Serializable {
    /**
     * Array of the individuals in this population
     */
    private IndividualMapReduce[] individualMapReduces;
    /**
     * Size of this population
     */
    private int sizeOfPopulation;
    /**
     * Sum of fitness values of the individuals in the population
     */
    private long sumOfFitness;

    /**
     * Creates population of the specified size
     * @param sizeOfPopulation size of the population
     */
    public Population(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
        individualMapReduces = new IndividualMapReduce[this.sizeOfPopulation];
    }

    /**
     * Initializes population with Binary encoding of the individuals with specified
     * chromosome length
     * @param chromosomeLength length of the individual's chromosome
     */
    public void initializePopulationBinary(Integer chromosomeLength) {
        for (int i = 0; i < sizeOfPopulation; i++) {
            BinaryIndividualMapReduce individual = new BinaryIndividualMapReduce(chromosomeLength);
            individual.generateRandomIndividual();
            this.individualMapReduces[i] = individual;
        }
    }

    /**
     * Initializes population with String encoding of the individuals with specified
     * chromosome length and uses characters form source as set of allowed characters
     * to generate individuals in the population. If set of allowed characters is null
     * individual is generated using alphanumeric characters
     * @param chromosomeLength length of the individual's chromosome
     * @param source set of allowed characters used to generate new individuals
     */
    public void initializePopulationString(Integer chromosomeLength, String[] source) {
        for (int i = 0; i < sizeOfPopulation; i++) {
            StringIndividualMapReduce individual = new StringIndividualMapReduce(chromosomeLength);
            if (source == null) {
                individual.generateRandomIndividual();
            } else {
                individual.generateRandomIndividual(source);
            }
            this.individualMapReduces[i] = individual;
        }
    }

    /**
     * Initializes population with Integer Permutation encoding with specified
     * length of the chromosome.
     * @param chromosomeLength length of the individual's chromosome
     */
    public void initializePopulationIntegerPermutation(Integer chromosomeLength) {
        for (int i = 0; i < sizeOfPopulation; i++) {
            IntPermutationIndividualMapReduce individual = new IntPermutationIndividualMapReduce(chromosomeLength);
            individual.generateRandomIndividual();
            this.individualMapReduces[i] = individual;
        }
    }

    /**
     * Getter for array of the individuals in this population
     * @return individuals in this population
     */
    public IndividualMapReduce[] getIndividualMapReduces() {
        return individualMapReduces;
    }

    /**
     * Getter for population size
     * @return size of this population
     */
    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    /**
     * Sets individuals for this population
     * @param individualMapReduces array of the individuals
     */
    public void setIndividualMapReduces(IndividualMapReduce[] individualMapReduces) {
        this.individualMapReduces = individualMapReduces;
    }

    /**
     * Getter for single individual in this population at specified index
     * @param index position of the individual in the array
     * @return individual at specified index
     */
    public IndividualMapReduce getIndividual(int index) {
        return individualMapReduces[index];
    }

    /**
     * Saves individual at the specified index into the population
     * @param individualMapReduce individual to be saved
     * @param index position at which individual should be saved
     */
    public void saveIndividual(IndividualMapReduce individualMapReduce, int index) {
        this.individualMapReduces[index] = individualMapReduce;
    }

    /**
     * Getter for the fittest individual in the population.
     * @return fittest individual from this population
     */
    public IndividualMapReduce getFittestIndividual() {
        IndividualMapReduce fittestIndividual = null;
        long maxFitness = Long.MIN_VALUE;
        for (IndividualMapReduce bi : individualMapReduces) {
            if (bi.getFitness() >= maxFitness) {
                fittestIndividual = bi;
                maxFitness = bi.getFitness();
            }
        }
        return fittestIndividual;
    }

    /**
     * Getter for sum of fitness values of the population.
     * @return sum of fitness values
     */
    public long getSumOfFitness() {
        return sumOfFitness;
    }

    /**
     * Calculates sum of fitness values for this population
     */
    public void calculateSumOfFitness() {
        for (IndividualMapReduce bi : individualMapReduces) {
            this.sumOfFitness += bi.getFitness();
        }
    }


}
