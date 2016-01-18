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
            BinaryIndividualMapReduce binaryIndividualMapReduce = p.getIndividual(i);
            binaryIndividualMapReduce.setFitness(calculateFitness(binaryIndividualMapReduce.getChromosome()));
        }
    }

    public static void setFitnessFunction(FitnessFunction fitnessFunction) {
        FitnessCalculator.fitnessFunction = fitnessFunction;
    }

    public static Integer calculateFitness(byte[] individualChromosome) {
        int fitness = 0;
        fitness = fitnessFunction.calculateFitness(individualChromosome);
        return fitness;
    }

}
