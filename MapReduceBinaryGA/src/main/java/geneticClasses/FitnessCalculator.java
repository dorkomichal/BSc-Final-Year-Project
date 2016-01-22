package geneticClasses;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public final class FitnessCalculator {

    private static FitnessFunction fitnessFunction;

    public static void calculateFitnessOfPopulation(Population p) {
        for(int i = 0; i < p.getSizeOfPopulation(); i++) {
            IndividualMapReduce individualMapReduce = p.getIndividual(i);
            individualMapReduce.setFitness(calculateFitness(individualMapReduce.getChromosome(), individualMapReduce));
        }
    }

    public static void setFitnessFunction(FitnessFunction fitnessFunction) {
        FitnessCalculator.fitnessFunction = fitnessFunction;
    }

    public static Integer calculateFitness(Object[] individualChromosome, IndividualMapReduce individualMapReduce) {
        int fitness;
        fitness = fitnessFunction.calculateFitness(individualChromosome, individualMapReduce);
        return fitness;
    }

}
