package geneticClasses;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 11/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class CrossoverPair implements Serializable {

    /**
     * Parent 1 selected for the crossover
     */
    private IndividualMapReduce parent1;
    /**
     * Parent 2 selected for the crossover
     */
    private IndividualMapReduce parent2;
    /**
     * When elitism is enabled this elite individual is assigned and it's
     * preserved for the next generation
     */
    private IndividualMapReduce eliteIndividual;

    /**
     * Getter elite individual
     * @return elite individual
     */
    public IndividualMapReduce getEliteIndividual() {
        return eliteIndividual;
    }

    /**
     * Setter for elite individual
     * @param eliteIndividual elite individual
     */
    public void setEliteIndividual(IndividualMapReduce eliteIndividual) {
        this.eliteIndividual = eliteIndividual;
    }

    /**
     * Getter for the first crossover parent
     * @return first crossover parent
     */
    public IndividualMapReduce getParent1() {
        return parent1;
    }

    /**
     * Setter for the first crossover parent
     * @param parent1 first crossover parent
     */
    public void setParent1(IndividualMapReduce parent1) {
        this.parent1 = parent1;
    }

    /**
     * Getter for the second crossover parent
     * @return second crossover parent
     */
    public IndividualMapReduce getParent2() {
        return parent2;
    }

    /**
     * Setter for the second crossover parent
     * @param parent2 second crossover parent
     */
    public void setParent2(IndividualMapReduce parent2) {
        this.parent2 = parent2;
    }


}
