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

    /**
     * Concrete implementation of the Fitness Function interface
     * which has method calculateFitness used for evaluation of
     * the population
     */
    private FitnessFunction fitnessFunctionInstance;

    /**
     * Creates FitnessCalculator with provided FitnessFunction
     * @param f concrete implementation of the FitnessFunction interface
     */
    public FitnessCalculator(FitnessFunction f) {
        this.fitnessFunctionInstance = f;
    }

    /**
     * Calculates fitness of the whole population
     * @param p population to be evaluated
     */
    public void calculateFitnessOfPopulation(Population p) {
        for (int i = 0; i < p.getSizeOfPopulation(); i++) {
            IndividualMapReduce individualMapReduce = p.getIndividual(i);
            individualMapReduce.setFitness(calculateFitness(individualMapReduce.getChromosome(), individualMapReduce));
        }
    }

    /**
     * Calculates/Evaluates fitness of the single individual
     * @param individualChromosome chromosome of the individual to be evaluated
     * @param individualMapReduce instance of the individual itself
     * @return evaluated fitness of the individual
     */
    public Long calculateFitness(Object[] individualChromosome, IndividualMapReduce individualMapReduce) {
        long fitness;
        fitness = fitnessFunctionInstance.calculateFitness(individualChromosome, individualMapReduce);
        return fitness;
    }

}
