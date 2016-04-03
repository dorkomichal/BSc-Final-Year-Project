package islandmodel;

import geneticClasses.CrossoverPair;
import geneticClasses.Population;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 18/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Island implements Serializable {

    /**
     * Population on this island
     */
    private Population population;
    /**
     * Size of this island
     */
    private int sizeOfIsland;
    /**
     * Array of the crossover pairs on this island after selection process
     */
    private CrossoverPair[] crossoverPairs;
    /**
     * Index of the individual that is selected for migration
     */
    private int emigrantIndex;

    /**
     * Creates new island with specified size (size of the population on this island)
     * @param sizeOfIsland size of the population on this island
     */
    public Island(int sizeOfIsland) {
        this.sizeOfIsland = sizeOfIsland;
        this.population = new Population(sizeOfIsland);
    }

    /**
     * Creates new island with specified population and the size of the island
     * @param population population to be assigned to this island
     * @param sizeOfIsland size of the population on this island
     */
    public Island(Population population, int sizeOfIsland) {
        this.population = population;
        this.sizeOfIsland = sizeOfIsland;
    }

    /**
     * Getter for the population
     * @return population on this island
     */
    public Population getPopulation() {
        return population;
    }

    /**
     * Setter for the population
     * @param population population to be assigned to this island
     */
    public void setPopulation(Population population) {
        this.population = population;
    }

    /**
     * Getter for all crossover pairs on this island
     * @return crossover pairs
     */
    public CrossoverPair[] getCrossoverPairs() {
        return crossoverPairs;
    }

    /**
     * Setter for the crossover pairs
     * @param crossoverPairs crossover pairs
     */
    public void setCrossoverPairs(CrossoverPair[] crossoverPairs) {
        this.crossoverPairs = crossoverPairs;
    }

    /**
     * Getter for size of the island
     * @return size of the island
     */
    public int getSizeOfIsland() {
        return sizeOfIsland;
    }

    /**
     * Setter for the size of the island
     * @param sizeOfIsland size of the island
     */
    public void setSizeOfIsland(int sizeOfIsland) {
        this.sizeOfIsland = sizeOfIsland;
    }

    /**
     * Getter for the emigrant index
     * @return emigrant index
     */
    public int getEmigrantIndex() {
        return emigrantIndex;
    }

    /**
     * Setter for the emigrant index
     * @param emigrantIndex emigrant index
     */
    public void setEmigrantIndex(int emigrantIndex) {
        this.emigrantIndex = emigrantIndex;
    }
}
