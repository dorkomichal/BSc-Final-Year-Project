package islandmodel;

import geneticClasses.CrossoverPair;
import geneticClasses.IndividualMapReduce;
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

    private Population population;
    private int sizeOfIsland;
    private CrossoverPair[] crossoverPairs;

    public Island(int sizeOfIsland) {
        this.sizeOfIsland = sizeOfIsland;
        this.population = new Population(sizeOfIsland);
    }

    public Island(Population population, int sizeOfIsland) {
        this.population = population;
        this.sizeOfIsland = sizeOfIsland;
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public CrossoverPair[] getCrossoverPairs() {
        return crossoverPairs;
    }

    public void setCrossoverPairs(CrossoverPair[] crossoverPairs) {
        this.crossoverPairs = crossoverPairs;
    }

    public int getSizeOfIsland() {
        return sizeOfIsland;
    }

    public void setSizeOfIsland(int sizeOfIsland) {
        this.sizeOfIsland = sizeOfIsland;
    }

}
