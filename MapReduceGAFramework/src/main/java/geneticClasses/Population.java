package geneticClasses;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class Population {

    private IndividualMapReduce[] individualMapReduces;
    private int sizeOfPopulation;
    private int sumOfFitnesses;

    public Population(int sizeOfPopulation) {
        this.sizeOfPopulation = sizeOfPopulation;
        individualMapReduces = new IndividualMapReduce[this.sizeOfPopulation];
    }


    public void initializePopulationBinary(Integer chromosomeLength) {
        for (int i = 0; i < sizeOfPopulation; i++) {
            BinaryIndividualMapReduce individual = new BinaryIndividualMapReduce(chromosomeLength);
            individual.generateRandomIndividual();
            this.individualMapReduces[i] = individual;
        }
    }


    public void initializePopulationString(Integer chromosomeLength, String[] source) {
        for(int i = 0; i < sizeOfPopulation; i++) {
            StringIndividualMapReduce individual = new StringIndividualMapReduce(chromosomeLength);
            if (source == null) {
                individual.generateRandomIndividual();
            } else {
                individual.generateRandomIndividual(source);
            }
            this.individualMapReduces[i] = individual;
        }
    }

    public void initializePopulationIntegerPermutation(Integer chromosomeLength) {
        for (int i = 0; i < sizeOfPopulation; i++) {
            IntPermutationIndividualMapReduce individual = new IntPermutationIndividualMapReduce(chromosomeLength);
            individual.generateRandomIndividual();
            this.individualMapReduces[i] = individual;
        }
    }


    public IndividualMapReduce[] getIndividualMapReduces() {
        return individualMapReduces;
    }

    public int getSizeOfPopulation() {
        return sizeOfPopulation;
    }

    public void setIndividualMapReduces(IndividualMapReduce[] individualMapReduces) {
        this.individualMapReduces = individualMapReduces;
    }

    public IndividualMapReduce getIndividual(int index) {
        return individualMapReduces[index];
    }


    public void saveIndividual(IndividualMapReduce individualMapReduce, int index) {
        this.individualMapReduces[index] = individualMapReduce;
    }

    public IndividualMapReduce getFittestIndividual() {
        IndividualMapReduce fittestIndividual = null;
        int maxFitness = 0;
        for (IndividualMapReduce bi : individualMapReduces) {
            if (bi.getFitness() > maxFitness) {
                fittestIndividual = bi;
                maxFitness = bi.getFitness();
            }
        }
        // If whole population has fitness 0 then return first individual
        if (fittestIndividual == null) {
            fittestIndividual = individualMapReduces[0];
        }
        return fittestIndividual;
    }

    public int getSumOfFitnesses() {
        return sumOfFitnesses;
    }

    public void calculateSumOfFitnesses() {
        for (IndividualMapReduce bi : individualMapReduces) {
            this.sumOfFitnesses += bi.getFitness();
        }
    }


}
