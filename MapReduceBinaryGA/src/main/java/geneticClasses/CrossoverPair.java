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

    private BinaryIndividualMapReduce parent1;
    private BinaryIndividualMapReduce parent2;
    private BinaryIndividualMapReduce eliteIndividual;

    public BinaryIndividualMapReduce getEliteIndividual() {
        return eliteIndividual;
    }

    public void setEliteIndividual(BinaryIndividualMapReduce eliteIndividual) {
        this.eliteIndividual = eliteIndividual;
    }

    public BinaryIndividualMapReduce getParent1() {
        return parent1;
    }

    public void setParent1(BinaryIndividualMapReduce parent1) {
        this.parent1 = parent1;
    }

    public BinaryIndividualMapReduce getParent2() {
        return parent2;
    }

    public void setParent2(BinaryIndividualMapReduce parent2) {
        this.parent2 = parent2;
    }


}
