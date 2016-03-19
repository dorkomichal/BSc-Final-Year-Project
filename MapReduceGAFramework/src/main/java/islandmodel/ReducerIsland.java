package islandmodel;

import geneticClasses.CrossoverPair;
import geneticClasses.GeneticOperationsMapReduce;
import geneticClasses.IndividualMapReduce;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 18/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class ReducerIsland implements Serializable {

    private static ReducerIsland reducerIsland;

    public static ReducerIsland getReducerIsland() {
        if (reducerIsland == null) {
            reducerIsland = new ReducerIsland();
            return reducerIsland;
        } else {
            return reducerIsland;
        }
    }

    public JavaRDD<Island> reduceCrossover(JavaRDD<Island> population, boolean multipoint, int numberOfCrossPoints, GeneticOperationsMapReduce geneticOperations) {
        JavaRDD<Island> newGeneration;
        if(!multipoint) {
            newGeneration = population.map(island -> singlePointCrossover(island, geneticOperations));
        } else {
            newGeneration = population.map(island -> multipointCrossover(island, numberOfCrossPoints, geneticOperations));
        }
        return newGeneration;
    }

    private Island singlePointCrossover(Island isl, GeneticOperationsMapReduce geneticOperationsMapReduce) {
        IndividualMapReduce[] newGeneration = new IndividualMapReduce[isl.getSizeOfIsland()];
        int i = 0;
        for(CrossoverPair cp: isl.getCrossoverPairs()) {
            if(cp.getEliteIndividual() != null) {
                newGeneration[i] = cp.getEliteIndividual();
            } else {
                newGeneration[i] = geneticOperationsMapReduce.singlePointCrossover(cp.getParent1(), cp.getParent2());
            }
            i++;
        }
        isl.getPopulation().setIndividualMapReduces(newGeneration);
        return isl;
    }

    private Island multipointCrossover(Island isl, int numOfCrossPoints, GeneticOperationsMapReduce geneticOperationsMapReduce) {
        IndividualMapReduce[] newGeneration = new IndividualMapReduce[isl.getSizeOfIsland()];
        int i = 0;
        for(CrossoverPair cp: isl.getCrossoverPairs()) {
            if(cp.getEliteIndividual() != null) {
                newGeneration[i] = cp.getEliteIndividual();
            } else {
                newGeneration[i] = geneticOperationsMapReduce.multiPointCrossover(cp.getParent1(), cp.getParent2(), numOfCrossPoints);
            }
            i++;
        }
        isl.getPopulation().setIndividualMapReduces(newGeneration);
        return isl;
    }

}
