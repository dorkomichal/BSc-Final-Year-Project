package islandmodel;

import geneticClasses.CrossoverPair;
import geneticClasses.GeneticOperationsMapReduce;
import geneticClasses.IndividualMapReduce;
import geneticClasses.Population;
import org.apache.spark.api.java.JavaRDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 18/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class ParallelCrossoverIsland implements Serializable {
    /**
     * Singleton of the ParallelCrossoverIsland
     */
    private static ParallelCrossoverIsland parallelCrossoverIsland;
    private final static Logger LOGGER = LoggerFactory.getLogger(ParallelCrossoverIsland.class);

    /**
     * Creates and/or returns singleton instance of the ParallelCrossoverIsland
     * @return singleton instance of the ParallelCrossoverIsland
     */
    public static ParallelCrossoverIsland getParallelCrossoverIsland() {
        if (parallelCrossoverIsland == null) {
            parallelCrossoverIsland = new ParallelCrossoverIsland();
            return parallelCrossoverIsland;
        } else {
            return parallelCrossoverIsland;
        }
    }

    /**
     * Method for performing crossover using Spark map() operation. Crossover is performed on the island
     * with individuals selected for crossover. RDD with islands with new generation is returned.
     * @param population islands on which crossover should be performed
     * @param multipoint boolean flag to set whether multi point crossover should be used of simple single-point crossover
     * @param numberOfCrossPoints if multipoint crossover is used specifies number of the crossover points
     * @param geneticOperations instance of the class with all operations including crossover provided
     * @return RDD of islands with new generation
     */
    public JavaRDD<Island> parallelCrossover(JavaRDD<Island> population, boolean multipoint, int numberOfCrossPoints, GeneticOperationsMapReduce geneticOperations) {
        JavaRDD<Island> newGeneration;
        if (!multipoint) {
            newGeneration = population.map(island -> singlePointCrossover(island, geneticOperations));
        } else {
            newGeneration = population.map(island -> multipointCrossover(island, numberOfCrossPoints, geneticOperations));
        }
        return newGeneration;
    }

    /**
     * Method for performing Single-point crossover on the island. Method for single-point crossover
     * is provided in genetic operations class
     * @param isl island on which crossover will be applied
     * @param geneticOperationsMapReduce instance of the class with all operations including crossover provided
     * @return island with new generation
     */
    private Island singlePointCrossover(Island isl, GeneticOperationsMapReduce geneticOperationsMapReduce) {
        IndividualMapReduce[] newGeneration = new IndividualMapReduce[isl.getSizeOfIsland()];
        int i = 0;
        for (CrossoverPair cp : isl.getCrossoverPairs()) {
            if (cp.getEliteIndividual() != null) {
                newGeneration[i] = cp.getEliteIndividual();
            } else {
                newGeneration[i] = geneticOperationsMapReduce.singlePointCrossover(cp.getParent1(), cp.getParent2());
            }
            i++;
        }
        Population newPopulation = new Population(isl.getSizeOfIsland());
        newPopulation.setIndividualMapReduces(newGeneration);
        return new Island(newPopulation, isl.getSizeOfIsland());

    }

    /**
     * Method for performing multi point crossover on the island. Method for mingle-point crossover
     * is provided in genetic operations class.
     * @param isl island on which crossover will be applied
     * @param numOfCrossPoints number of crossover points
     * @param geneticOperationsMapReduce instance of the class with all operations including crossover provided
     * @return island with new generation
     */
    private Island multipointCrossover(Island isl, int numOfCrossPoints, GeneticOperationsMapReduce geneticOperationsMapReduce) {
        IndividualMapReduce[] newGeneration = new IndividualMapReduce[isl.getSizeOfIsland()];
        int i = 0;
        for (CrossoverPair cp : isl.getCrossoverPairs()) {
            if (cp.getEliteIndividual() != null) {
                newGeneration[i] = cp.getEliteIndividual();
            } else {
                newGeneration[i] = geneticOperationsMapReduce.multiPointCrossover(cp.getParent1(), cp.getParent2(), numOfCrossPoints);
            }
            i++;
        }
        Population newPopulation = new Population(isl.getSizeOfIsland());
        newPopulation.setIndividualMapReduces(newGeneration);
        return new Island(newPopulation, isl.getSizeOfIsland());
    }

}
