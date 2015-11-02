package geneticClasses;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Population {

    private BinaryIndividual[] binaryIndividuals;
    private int sizeOfPopulation;

    public Population(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
        binaryIndividuals = new BinaryIndividual[this.sizeOfPopulation];
    }

    public void initializePopulation() {
        for(int i = 0; i < sizeOfPopulation; i++) {

        }
    }
}
