package geneticClasses;

import java.util.Arrays;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class FitnessCalculator {

    private static Object[] problemSolution;
    private static int lengthOfSolution;

    public static void setProblemSolutionByte(Byte[] problemSolution) {
        FitnessCalculator.problemSolution = problemSolution;
        FitnessCalculator.lengthOfSolution = problemSolution.length;
    }

    public static void setProblemSolutionByte(String problemSolution) {
        FitnessCalculator.problemSolution = new Byte[problemSolution.length()];
        int i = 0;
        for (char c : problemSolution.toCharArray()) {
           if (c == '0') {
               FitnessCalculator.problemSolution[i] = (byte) 0;
           } else {
               FitnessCalculator.problemSolution[i] = (byte) 1;
           }
           i++;
        }
    }

    public static void setProblemSolutionString(String problemSolution) {
        FitnessCalculator.problemSolution = new String[problemSolution.length()];
        for(int i = 0; i < problemSolution.length(); i++) {
            FitnessCalculator.problemSolution[i] = problemSolution.substring(i, i+1);
        }
    }

    public static int getLengthOfSolution() {
        return lengthOfSolution;
    }

    public static void calculateFitnessOfPopulation(Population p) {
        for(int i = 0; i < p.getSizeOfPopulation(); i++) {
            Individual individual = p.getIndividual(i);
            if (individual instanceof BinaryIndividual) {
                individual.setFitness(compareChromosomeAndSolution((Byte[]) individual.getChromosome(), individual));
            } else {
                individual.setFitness(compareChromosomeAndSolution((String[]) individual.getChromosome(), individual));
            }
        }
    }

    protected static Integer compareChromosomeAndSolution(Object[] individualChromosome, Individual individual) {
        int fitness = 0;
        if (individual instanceof StringIndividual) {
            for (int i = 0; i < individualChromosome.length; i++) {
                if (individualChromosome[i].equals(problemSolution[i])) {
                    fitness++;
                }
            }
        } else {
            for (int i = 0; i < individualChromosome.length; i++) {
                if (individualChromosome[i] == problemSolution[i]) {
                    fitness++;
                }
            }
        }
        return fitness;
    }


}
