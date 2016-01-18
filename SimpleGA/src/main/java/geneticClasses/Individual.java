package geneticClasses;

/**
 * Created by Michal Dorko on 18/01/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public interface Individual<E> {
    E[] getChromosome();
    void setGene(E gene, int index);
    int getFitness();
    int lengthOfChromosome();
    void setChromosome(E[] chromosome);
    void calculateFitness();
    void setFitness(int fitness);
    void generateRandomIndividual();
    double getProbabilityOfSelection();
    void setProbabilityOfSelection(double probabilityOfSelection);

}
