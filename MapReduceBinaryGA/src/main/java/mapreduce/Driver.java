package mapreduce;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.FitnessCalculator;
import geneticClasses.Population;
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
    private JavaRDD<BinaryIndividualMapReduce> populationParallelized;

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

    public JavaRDD<BinaryIndividualMapReduce> getPopulationParallelized() {
        return populationParallelized;
    }

    public void initializePopulation(int sizeOfPopulation) {
        Population population = new Population(sizeOfPopulation);
        population.initializePopulation();
        FitnessCalculator.calculateFitnessOfPopulation(population);
        GlobalFile.setPopulation(population);
        populationParallelized = jsc.parallelize(Arrays.asList(population.getBinaryIndividualMapReduces()));
    }

}
