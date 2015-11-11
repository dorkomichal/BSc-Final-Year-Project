package mapreduce;

import geneticClasses.BinaryIndividualMapReduce;
import geneticClasses.GeneticAlgorithmMapReduce;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

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

    public void reduceCrossover(JavaRDD<BinaryIndividualMapReduce> selectedIndividuals) {
        GlobalFile.createNewPopulation((int) selectedIndividuals.count());
        selectedIndividuals.reduce((ind1, ind2) -> singlePointCrossover(ind1, ind2));
    }

    public BinaryIndividualMapReduce singlePointCrossover(BinaryIndividualMapReduce bimr1, BinaryIndividualMapReduce bimr2) {
        BinaryIndividualMapReduce[] newIndividuals = GeneticAlgorithmMapReduce.singlePointCrossover(bimr1, bimr2);
        GlobalFile.addIndividual(newIndividuals[0]);
        GlobalFile.addIndividual(newIndividuals[1]);
        return newIndividuals[0];
    }

}
