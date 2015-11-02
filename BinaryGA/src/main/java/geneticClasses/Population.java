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
            BinaryIndividual individual = new BinaryIndividual();
            individual.generateRandomIndividual();
            this.binaryIndividuals[i] = individual;
        }
    }

    public BinaryIndividual getIndividual(int index) {
        return binaryIndividuals[index];
    }

    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    public void saveBinaryIndividual(BinaryIndividual binaryIndividual, int index) {
        this.binaryIndividuals[index] = binaryIndividual;
    }

    public BinaryIndividual getFittestIndividual() {
        BinaryIndividual fittestIndividual = null;
        int maxFitness = 0;
        for (BinaryIndividual bi : binaryIndividuals) {
            if (bi.getFitness() > maxFitness) {
                fittestIndividual = bi;
                maxFitness = bi.getFitness();
            }
        }
        // If whole population has fitness 0 then return first individual
        if (fittestIndividual == null) {
            fittestIndividual = binaryIndividuals[0];
        }
        return  fittestIndividual;
    }

}
