package sat;

import geneticClasses.FitnessFunction;
import geneticClasses.IndividualMapReduce;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.BooleanSupplier;

/**
 * Created by Michal Dorko on 22/01/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Satisfiability implements FitnessFunction {

    public ArrayList<String[]> getExpressionString() {
        return expressionString;
    }

    ArrayList<String[]> expressionString;
    ArrayList<Integer[]> expressionInteger;
    int numberOfClauses;
    int numOfVariables;
    Random random = new Random();

    public Satisfiability(int numberOfClauses, int numOfVariables) {
        this.numberOfClauses = numberOfClauses;
        this.numOfVariables = numOfVariables;
        generateRandomSAT();
    }

    @Override
    public int calculateFitness(Object[] chromosome, IndividualMapReduce individualMapReduce) {
        int fitness = 0;
        for (int i = 0; i < numberOfClauses; i++) {
            String[] clause = expressionString.get(i);
            Integer[] clauseInt = expressionInteger.get(i);
            boolean isTrue = false;
            for (int j = 0; j < 3; j++) {
                int variable = clauseInt[j];
                if (clause[j].substring(0,1).equals("~") && (Byte) chromosome[variable] == 0) {
                    isTrue = true;
                } else if(!clause[j].substring(0,1).equals("~") && (Byte) chromosome[variable] == 1) {
                    isTrue = true;
                }
            }
            if (isTrue) {
                fitness++;
            }
        }
        return fitness;
    }

    private void generateRandomSAT() {
        expressionString = new ArrayList<>();
        expressionInteger = new ArrayList<>();
        for (int i = 0; i < numberOfClauses; i++) {
            int[] randomVariables = random.ints(0, numOfVariables).distinct().limit(3).toArray();
            String[] vars = new String[3];
            Integer[] intVars = new Integer[3];
            for (int j = 0; j < 3; j++) {
                if(random.nextInt(20) <= 5) {
                    vars[j] = "~p" + randomVariables[j] + " OR ";
                } else {
                    vars[j] = "p" + randomVariables[j] + " OR ";
                }
                intVars[j] = randomVariables[j];
            }
            expressionString.add(vars);
            expressionInteger.add(intVars);
        }
    }
}
