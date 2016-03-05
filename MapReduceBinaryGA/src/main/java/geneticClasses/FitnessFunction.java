package geneticClasses;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 18/01/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public interface FitnessFunction extends Serializable, Cloneable {

    int calculateFitness(Object[] chromosome, IndividualMapReduce individualMapReduce);
}
