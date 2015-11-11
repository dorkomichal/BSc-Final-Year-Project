package geneticClasses;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Population {

    private BinaryIndividualMapReduce[] binaryIndividualMapReduces;
    private int sizeOfPopulation;
    private int sumOfFitnesses;

    public Population(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
        binaryIndividualMapReduces = new BinaryIndividualMapReduce[this.sizeOfPopulation];
    }


    public void initializePopulation() {
        for (int i = 0; i < sizeOfPopulation; i++) {
            BinaryIndividualMapReduce individual = new BinaryIndividualMapReduce();
            individual.generateRandomIndividual();
            this.binaryIndividualMapReduces[i] = individual;
        }
    }

    public BinaryIndividualMapReduce[] getBinaryIndividualMapReduces() {
        return binaryIndividualMapReduces;
    }

    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    public void setBinaryIndividualMapReduces(BinaryIndividualMapReduce[] binaryIndividualMapReduces) {
        this.binaryIndividualMapReduces = binaryIndividualMapReduces;
    }

    public BinaryIndividualMapReduce getIndividual(int index) {
        return binaryIndividualMapReduces[index];
    }


    public void saveBinaryIndividual(BinaryIndividualMapReduce binaryIndividualMapReduce, int index) {
        this.binaryIndividualMapReduces[index] = binaryIndividualMapReduce;
    }

    public BinaryIndividualMapReduce getFittestIndividual() {
        BinaryIndividualMapReduce fittestIndividual = null;
        int maxFitness = 0;
        for (BinaryIndividualMapReduce bi : binaryIndividualMapReduces) {
            if (bi.getFitness() > maxFitness) {
                fittestIndividual = bi;
                maxFitness = bi.getFitness();
            }
        }
        // If whole population has fitness 0 then return first individual
        if (fittestIndividual == null) {
            fittestIndividual = binaryIndividualMapReduces[0];
        }
        return fittestIndividual;
    }

    public int getSumOfFitnesses() {
        return sumOfFitnesses;
    }

    public void calculateSumOfFitnesses() {
        for (BinaryIndividualMapReduce bi : binaryIndividualMapReduces) {
            this.sumOfFitnesses += bi.getFitness();
        }
    }


}
