package mapreduce;

import geneticClasses.CrossoverPair;
import geneticClasses.GeneticOperationsMapReduce;
import geneticClasses.IndividualMapReduce;
import org.apache.spark.api.java.JavaRDD;

import java.io.Serializable;

/**
 * Created by Michal Dorko on 09/11/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Reducer implements Serializable {

    private static Reducer reducer;

    public static Reducer getReducer() {
        if(reducer == null) {
            reducer = new Reducer();
            return reducer;
        } else {
            return reducer;
        }
    }

    /**
     * This reduce crossover function is in fact implemented by another map function however due to
     * conventions I have categorised it as reduce step because we reduce pairs into new offspring
     * yielding new generation
     * @param selectedIndividuals RDD of crossover pairs
     * @param multipoint boolean value indicating whether to apply single point or multi point crossover
     * @param numberOfCrossPoints number of crossover points if multipoint crossover is selected
     * @return new generations as RDD
     */
    public JavaRDD<IndividualMapReduce> reduceCrossover(JavaRDD<CrossoverPair> selectedIndividuals, boolean multipoint, int numberOfCrossPoints) {
        GlobalFile.createNewPopulation((int) selectedIndividuals.count());
        JavaRDD<IndividualMapReduce> newGen;
        if (multipoint) {
             newGen = selectedIndividuals.map(crossoverPair -> multiPointCrossover(crossoverPair, numberOfCrossPoints));
        } else {
             newGen = selectedIndividuals.map(crossoverPair -> singlePointCrossover(crossoverPair));
        }
        return newGen;
    }

    private IndividualMapReduce singlePointCrossover(CrossoverPair pair) {
        if(pair.getEliteIndividual() != null) {
            return pair.getEliteIndividual();
        } else {
            IndividualMapReduce newIndividual = GeneticOperationsMapReduce.singlePointCrossover(pair.getParent1(), pair.getParent2());
            return newIndividual;
        }
    }

    private IndividualMapReduce multiPointCrossover(CrossoverPair pair, int numberOfPoints) {
        if(pair.getEliteIndividual() != null) {
            return pair.getEliteIndividual();
        } else {
            IndividualMapReduce newIndividual = GeneticOperationsMapReduce.multiPointCrossover(pair.getParent1(), pair.getParent2(), numberOfPoints);
            return newIndividual;
        }
    }

}
