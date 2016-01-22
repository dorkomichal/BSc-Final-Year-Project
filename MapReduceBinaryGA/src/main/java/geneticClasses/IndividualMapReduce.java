package geneticClasses;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 20/01/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public interface IndividualMapReduce<E> extends Serializable {
    /**
     * Get a crossover pair
     * @return crossover pair
     */
    CrossoverPair getCrossoverPair();

    /**
     * Sets a crossover pair assigned to this individual
     * @param crossoverPair crossover pair assined to this individual
     */
    void setCrossoverPair(CrossoverPair crossoverPair);

    /**
     * Generates a random individual with random sequence chromosome
     */
    void generateRandomIndividual();

    /**
     * Getter method for chromosome
     * @return chromosome
     */
    E[] getChromosome();

    /**
     * Method for setting particular gene at particular index
     * @param gene gene to be set
     * @param index position at which gene will be set
     */
    void setGene(E gene, int index);

    /**
     * Getter for fitness value
     * @return fitness value
     */
    int getFitness();

    /**
     * Getter for chromosome length
     * @return chromosome length
     */
    int lengthOfChromosome();

    /**
     * Sets entire chromosome of the individual
     * @param chromosome chromosome to be set
     */
    void setChromosome(E[] chromosome);

    /**
     * Calculates fitness of this individual and returns it
     * @return fitness of the individual
     */
    Integer calculateFitness();

    /**
     * Setter for fitness
     * @param fitness value of the fitness
     */
    void setFitness(int fitness);

    /**
     * Getter for probability of selection (RWS selection)
     * @return probability of selection
     */
    double getProbabilityOfSelection();

    /**
     * Setter for probability of selection
     * @param probabilityOfSelection value of probability
     */
    void setProbabilityOfSelection(double probabilityOfSelection);

}