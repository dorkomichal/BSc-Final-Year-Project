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

    private IndividualMapReduce parent1;
    private IndividualMapReduce parent2;
    private IndividualMapReduce eliteIndividual;

    public IndividualMapReduce getEliteIndividual() {
        return eliteIndividual;
    }

    public void setEliteIndividual(IndividualMapReduce eliteIndividual) {
        this.eliteIndividual = eliteIndividual;
    }

    public IndividualMapReduce getParent1() {
        return parent1;
    }

    public void setParent1(IndividualMapReduce parent1) {
        this.parent1 = parent1;
    }

    public IndividualMapReduce getParent2() {
        return parent2;
    }

    public void setParent2(IndividualMapReduce parent2) {
        this.parent2 = parent2;
    }


}
