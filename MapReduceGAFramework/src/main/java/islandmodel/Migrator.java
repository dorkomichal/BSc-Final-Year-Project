package islandmodel;

import geneticClasses.IndividualMapReduce;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Michal Dorko on 19/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Migrator implements Serializable {

    public IndividualMapReduce getEmigrant(Island isl) {
        Random random = new Random();
        IndividualMapReduce[] pop = isl.getPopulation().getIndividualMapReduces();
        int index = random.nextInt(isl.getSizeOfIsland());
        isl.setEmigrantIndex(index);
        return pop[index];
    }

    public Island applyMigration(Island isl, IndividualMapReduce individual) {
        IndividualMapReduce[] population = isl.getPopulation().getIndividualMapReduces();
        population[isl.getEmigrantIndex()] = individual;
        isl.getPopulation().setIndividualMapReduces(population);
        return isl;
    }
}
