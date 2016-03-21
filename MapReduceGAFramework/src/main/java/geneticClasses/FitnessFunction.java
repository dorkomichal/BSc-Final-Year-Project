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

    /**
     * Implementation of the fitness function which is problem dependent therefore user has to
     * implement this interface and provide implementation of this method. This ensures flexibility of
     * the framework to solve various kind of problems without changing base of the code
     * @param chromosome chromosome of the individual
     * @param individualMapReduce instance of the individual itself
     * @return fitness of the individual
     */
    int calculateFitness(Object[] chromosome, IndividualMapReduce individualMapReduce);
}
