package geneticClasses;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class FitnessCalculator {

    private static byte[] problemSolution;

    public static void setProblemSolution(byte[] problemSolution) {
        FitnessCalculator.problemSolution = problemSolution;
    }

    public static void setProblemSolution(String problemSolution) {
        FitnessCalculator.problemSolution = new byte[problemSolution.length()];
        int i = 0;
        for (char c : problemSolution.toCharArray()) {
           FitnessCalculator.problemSolution[i] = (byte) c;
            i++;
        }
    }



}
