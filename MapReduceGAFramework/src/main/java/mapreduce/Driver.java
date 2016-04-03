package mapreduce;

import geneticClasses.FitnessCalculator;
import geneticClasses.IndividualMapReduce;
import geneticClasses.IndividualType;
import geneticClasses.Population;
import islandmodel.Island;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Michal Dorko on 09/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Driver {
    /**
     * Singleton instance of the Driver class
     */
    private static Driver driver;
    /**
     * Singleton instance of SparkConf
     */
    private static SparkConf conf;
    /**
     * Singleton instance of JavaSparkContext
     */
    private static JavaSparkContext jsc;
    /**
     * Parallelized population used for Global Parallelization Model
     */
    private JavaRDD<IndividualMapReduce> populationParallelized;
    /**
     * Parallelized islands used for Island Parallelization Model
     */
    private JavaRDD<Island> populationIsland;

    /**
     * Static method for creating and getting Driver singleton object
     * @return singleton Driver object
     */
    public static Driver getDriver() {
        if (driver != null) {
            return driver;
        } else {
            driver = new Driver();
            conf = new SparkConf().setAppName("Map-Reduce Genetic Algorithm");
            jsc = new JavaSparkContext(conf);
            return driver;
        }
    }

    /**
     * Method for parallelizing data into Spark JavaRDD
     * @param data
     * @return
     */
    public JavaRDD paralleliseData(List data) {
        return jsc.parallelize(data);
    }

    /**
     * Getter for parallelized population for Global Parallelization Model
     * @return parallelized RDD population
     */
    public JavaRDD<IndividualMapReduce> getPopulationParallelized() {
        return populationParallelized;
    }

    /**
     * Getter for parallelized population for Island (Coarse-Grained) Parallelization Model
     * @return parallelized RDD population
     */
    public JavaRDD<Island> getPopulationIsland() {
        return populationIsland;
    }

    /**
     * Initializes population for Global Parallelization Model and evaluates fitness of this initial
     * population on driver node.
     * @param fc instance of the fitness class with implemented fitness evaluation function
     * @param chromosomeLength length of individual's chromosome
     * @param sizeOfPopulation size of the population
     * @param type type of encoding of the individual
     * @param source set of allowed characters for String encoded individuals
     */
    public void initializePopulation(FitnessCalculator fc, Integer chromosomeLength, int sizeOfPopulation, IndividualType type, String[] source) {
        Population population = new Population(sizeOfPopulation);
        if (type.equals(IndividualType.Binary)) {
            population.initializePopulationBinary(chromosomeLength);
        } else if (type.equals(IndividualType.String)) {
            population.initializePopulationString(chromosomeLength, source);
        } else {
            population.initializePopulationIntegerPermutation(chromosomeLength);
        }
        fc.calculateFitnessOfPopulation(population);
        GlobalFile.setPopulation(population);
        List data = Arrays.asList(population.getIndividualMapReduces());
        this.populationParallelized = jsc.parallelize(data);
    }

    /**
     * Initializes population for Island (Coarse-Grained) Parallelization Model and evaluates fitness of this initial
     * population on driver node.
     * @param fc instance of the fitness class with implemented fitness evaluation function
     * @param chromosomeLength length of individual's chromosome
     * @param sizeOfPopulation size of the overall population to be divided onto individual islands
     * @param sizeOfIsland size of population on each island
     * @param type type of encoding of the individual
     * @param source set of allowed characters for String encoded individuals
     */
    public void initializePopulationIsland(FitnessCalculator fc, Integer chromosomeLength, int sizeOfPopulation, int sizeOfIsland, IndividualType type, String[] source) {
        int numberOfIslands = Math.floorDiv(sizeOfPopulation, sizeOfIsland); //determine number of islands from the overall population size
        Island[] islands = new Island[numberOfIslands];
        for (int i = 0; i < islands.length; i++) {
            Island isl = new Island(sizeOfIsland);
            if (type.equals(IndividualType.Binary)) {
                isl.getPopulation().initializePopulationBinary(chromosomeLength);
            } else if (type.equals(IndividualType.String)) {
                isl.getPopulation().initializePopulationString(chromosomeLength, source);
            } else {
                isl.getPopulation().initializePopulationIntegerPermutation(chromosomeLength);
            }
            fc.calculateFitnessOfPopulation(isl.getPopulation());
            islands[i] = isl;
        }
        List data = Arrays.asList(islands);
        this.populationIsland = jsc.parallelize(data);
    }


}
