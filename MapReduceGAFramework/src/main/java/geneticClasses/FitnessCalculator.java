package geneticClasses;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public final class FitnessCalculator implements Serializable {

    private FitnessFunction fitnessFunctionInstance;

    public FitnessCalculator(FitnessFunction f) {
        this.fitnessFunctionInstance = f;
    }

    public void calculateFitnessOfPopulation(Population p) {
        for(int i = 0; i < p.getSizeOfPopulation(); i++) {
            IndividualMapReduce individualMapReduce = p.getIndividual(i);
            individualMapReduce.setFitness(calculateFitness(individualMapReduce.getChromosome(), individualMapReduce));
        }
    }

    public Long calculateFitness(Object[] individualChromosome, IndividualMapReduce individualMapReduce) {
        long fitness;
        fitness = fitnessFunctionInstance.calculateFitness(individualChromosome, individualMapReduce);
        return fitness;
    }

}
