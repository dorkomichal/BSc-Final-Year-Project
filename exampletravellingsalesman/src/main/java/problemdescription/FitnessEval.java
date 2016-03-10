package problemdescription;

import geneticClasses.FitnessFunction;
import geneticClasses.IndividualMapReduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Michal Dorko on 09/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class FitnessEval implements FitnessFunction {

    List<City> cities;
    Random random = new Random();

    public FitnessEval(int numberOfCities, int maxSize) {
        this.cities = new ArrayList<>();
        for(int i = 0; i < numberOfCities; i++) {
            int x = random.nextInt(maxSize);
            int y = random.nextInt(maxSize);
            City c = new City(x,y);
            cities.add(c);
        }
    }

    public FitnessEval(List<City> cities) {
        this.cities = cities;
    }
    @Override
    public int calculateFitness(Object[] chromosome, IndividualMapReduce individualMapReduce) {
        double cost = 0.0;
        for(int i = 1; i < chromosome.length; i++) {
            City c1 = cities.get((Integer) chromosome[i-1]);
            City c2 = cities.get((Integer) chromosome[i]);
            int xdiff = c1.getPosx() - c2.getPosx();
            int ydiff = c1.getPosy() - c2.getPosy();
            double euclideanDistance = Math.sqrt(xdiff*xdiff + ydiff*ydiff );
            cost -= euclideanDistance;
        }
        return (int) cost;
    }

    public List<City> getCities() {
        return this.cities;
    }
}
