package mapreduce;

import geneticClasses.*;
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

    public static Driver driver;
    private static SparkConf conf;
    private static JavaSparkContext jsc;
    private JavaRDD<IndividualMapReduce> populationParallelized;

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

    public void initializePopulation(int sizeOfPopulation, IndividualType type, String[] source) {
        Population population = new Population(sizeOfPopulation);
        if (type.equals(IndividualType.Binary)) {
            population.initializePopulationBinary();
        } else {
            population.initializePopulationString(source);
        }
        FitnessCalculator.calculateFitnessOfPopulation(population);
        GlobalFile.setPopulation(population);
        List data = Arrays.asList(population.getIndividualMapReduces());
        populationParallelized = jsc.parallelize(data);
    }

    public void createIslands(int numberOfIslands) {
        populationParallelized.repartition(numberOfIslands);
    }

}
