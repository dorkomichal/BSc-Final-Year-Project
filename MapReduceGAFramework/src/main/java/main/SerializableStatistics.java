package main;

import geneticClasses.IndividualMapReduce;
import islandmodel.Island;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 18/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class SerializableStatistics implements Serializable {

    /**
     * Method for getting fittest individual on each island enclosed within class which
     * is serializable so this method can be used in reduce() phase
     * @param island1
     * @param island2
     * @return
     */
    public Island finalReduce(Island island1, Island island2) {
        IndividualMapReduce fittest1 = island1.getPopulation().getFittestIndividual();
        IndividualMapReduce fittest2 = island2.getPopulation().getFittestIndividual();
        if (fittest1.getFitness() >= fittest2.getFitness()) {
            return island1;
        } else {
            return island2;
        }
    }
}
