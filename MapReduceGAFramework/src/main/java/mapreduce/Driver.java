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

    private static Driver driver;
    private static SparkConf conf;
    private static JavaSparkContext jsc;
    private JavaRDD<IndividualMapReduce> populationParallelized;
    private JavaRDD<Island> populationIsland;

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

    public JavaRDD paralleliseData(List data) {
        return jsc.parallelize(data);
    }

    public JavaRDD<IndividualMapReduce> getPopulationParallelized() {
        return populationParallelized;
    }

    public JavaRDD<Island> getPopulationIsland() {
        return populationIsland;
    }

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

    public void initializePopulationIsland(FitnessCalculator fc, Integer chromosomeLength, int sizeOfPopulation, int sizeOfIsland, IndividualType type, String[] source) {
        int numberOfIslands = Math.floorDiv(sizeOfPopulation, sizeOfIsland);
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
