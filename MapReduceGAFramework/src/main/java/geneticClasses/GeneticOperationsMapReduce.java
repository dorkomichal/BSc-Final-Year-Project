package geneticClasses;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public final class GeneticOperationsMapReduce implements Serializable {

    private double crossoverRate = 0.7;

    public GeneticOperationsMapReduce(FitnessCalculator fc, Integer chromosomeLength, double tournamentParameterK, boolean elitism, double mutationRate, double crossoverRate) {
        this.fc = fc;
        this.chromosomeLength = chromosomeLength;
        this.tournamentParameterK = tournamentParameterK;
        this.elitism = elitism;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
    }

    private double mutationRate = 0.001;
    private boolean elitism = true;
    private double tournamentParameterK = 0.75;
    private Random random = new Random();
    private FitnessCalculator fc;
    private Integer chromosomeLength;

    public void setElitism(boolean elitism) {
        this.elitism = elitism;
    }

    public double getCrossoverRate() {
        return crossoverRate;
    }

    public boolean isElitism() {
        return elitism;
    }

    public void setTournamentParameterK(double tournamentParameterK) {
        this.tournamentParameterK = tournamentParameterK;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * Single point crossover is crossover where random single point is generated and chromosomes are split in two
     * parts producing two head and two tails sections. The two tail sections are then swapped to produce new individuals
     * (chromosomes)
     * @param parent1 First parent selected for crossover
     * @param parent2 Second parent selected for crossover
     * @return fitter individual from two new individuals
     */
    public IndividualMapReduce singlePointCrossover(IndividualMapReduce parent1, IndividualMapReduce parent2) {
        int crossoverPoint = random.nextInt(parent1.lengthOfChromosome());
        Object[] parent1Chromosome = parent1.getChromosome();
        Object[] parent2Chromosome = parent2.getChromosome();

        Object[] parent1ChromosomePart1 = Arrays.copyOfRange(parent1Chromosome, 0, crossoverPoint);
        Object[] parent1ChromosomePart2 = Arrays.copyOfRange(parent1Chromosome, crossoverPoint, parent1Chromosome.length);

        Object[] parent2ChromosomePart1 = Arrays.copyOfRange(parent2Chromosome, 0, crossoverPoint);
        Object[] parent2ChromosomePart2 = Arrays.copyOfRange(parent2Chromosome, crossoverPoint, parent2Chromosome.length);

        IndividualMapReduce child1;
        IndividualMapReduce child2;
        if (parent1 instanceof BinaryIndividualMapReduce) {
            child1 = new BinaryIndividualMapReduce(chromosomeLength);
            child2 = new BinaryIndividualMapReduce(chromosomeLength);
        } else if (parent1 instanceof StringIndividualMapReduce) {
            child1 = new StringIndividualMapReduce(chromosomeLength);
            child2 = new StringIndividualMapReduce(chromosomeLength);
        } else {
            child1 = new IntPermutationIndividualMapReduce(chromosomeLength);
            child2 = new IntPermutationIndividualMapReduce(chromosomeLength);
        }

        child1.setChromosome(ArrayUtils.addAll(parent1ChromosomePart1, parent2ChromosomePart2));
        child2.setChromosome(ArrayUtils.addAll(parent2ChromosomePart1, parent1ChromosomePart2));
        mutate(child1);
        mutate(child2);
        child1.calculateFitness(fc);
        child2.calculateFitness(fc);

        return fitterFromTwo(child1, child2) ;
    }

    /**
     * Multi point crossover is crossover where multiple random crossover points are generated and chromosomes
     * are folded over those crossover points
     * @param parent1 First parent selected for crossover
     * @param parent2 Second parent selected for crossover
     * @param numberOfPoints number of crossover points
     * @return fitter individual from two new individuals
     */
    public IndividualMapReduce multiPointCrossover(IndividualMapReduce parent1, IndividualMapReduce parent2, int numberOfPoints) {
        int[] crossoverPoints = random.ints(0, parent1.lengthOfChromosome() - 1).distinct().limit(numberOfPoints).toArray();
        Arrays.sort(crossoverPoints);
        List<Object[]> parent1ChromosomeParts = new ArrayList<>();
        List<Object[]> parent2ChromosomeParts = new ArrayList<>();
        Object[] parent1Chromosome = parent1.getChromosome();
        Object[] parent2Chromosome = parent2.getChromosome();
        int prev = 0;
        for (int i: crossoverPoints) {
            parent1ChromosomeParts.add(Arrays.copyOfRange(parent1Chromosome, prev, i));
            parent2ChromosomeParts.add(Arrays.copyOfRange(parent2Chromosome, prev, i));
            prev = i;
        }
        if (prev < parent1Chromosome.length) {
            parent1ChromosomeParts.add(Arrays.copyOfRange(parent1Chromosome, prev, parent1Chromosome.length));
            parent2ChromosomeParts.add(Arrays.copyOfRange(parent2Chromosome, prev, parent2Chromosome.length));
        }
        Object[] child1Chromosome = parent1ChromosomeParts.get(0);
        Object[] child2Chromosome = parent2ChromosomeParts.get(0);
        for(int i = 1; i < parent1ChromosomeParts.size(); i++) {
            if(i % 2 == 0) {
                child1Chromosome = ArrayUtils.addAll(child1Chromosome,parent1ChromosomeParts.get(i));
                child2Chromosome = ArrayUtils.addAll(child2Chromosome, parent2ChromosomeParts.get(i));
            } else {
                child1Chromosome = ArrayUtils.addAll(child1Chromosome,parent2ChromosomeParts.get(i));
                child2Chromosome = ArrayUtils.addAll(child2Chromosome, parent1ChromosomeParts.get(i));
            }
        }
        IndividualMapReduce child1;
        IndividualMapReduce child2;
        if (parent1 instanceof BinaryIndividualMapReduce) {
            child1 = new BinaryIndividualMapReduce(chromosomeLength);
            child2 = new BinaryIndividualMapReduce(chromosomeLength);
        } else if (parent1 instanceof StringIndividualMapReduce) {
            child1 = new StringIndividualMapReduce(chromosomeLength);
            child2 = new StringIndividualMapReduce(chromosomeLength);
        } else {
            child1 = new IntPermutationIndividualMapReduce(chromosomeLength);
            child2 = new IntPermutationIndividualMapReduce(chromosomeLength);
        }

        child1.setChromosome(child1Chromosome);
        child2.setChromosome(child2Chromosome);
        mutate(child1);
        mutate(child2);
        child1.calculateFitness(fc);
        child2.calculateFitness(fc);

        return fitterFromTwo(child1, child2) ;
    }

    /**
     * Mutation of the individuals chromosome. Flips value of the bit if randomly generated number is lower than
     * mutation rate.
     * @param individual the individual which chromosome will undergo mutation
     */
    protected void mutate(IndividualMapReduce individual) {
        if (individual instanceof BinaryIndividualMapReduce) {
            Byte[] chromosome = (Byte[]) individual.getChromosome();
            for (int i = 0; i < chromosome.length; i++) {
                if (Math.random() <= mutationRate) {
                    if (chromosome[i] == 1) {
                        individual.setGene((byte) 0, i);
                    } else {
                        individual.setGene((byte) 1, i);
                    }
                }
            }
        } else if (individual instanceof  StringIndividualMapReduce) {
            StringIndividualMapReduce ind = (StringIndividualMapReduce) individual;
            String[] source = ind.getSource();
            String[] chromosome = (String[]) individual.getChromosome();
            int chromosomeLength = individual.lengthOfChromosome();
            for (int i = 0; i < chromosome.length; i++) {
                if(Math.random() <= mutationRate) {
                    if(source != null) {
                        int ran = random.nextInt(chromosomeLength);
                        individual.setGene(source[ran],i);
                    } else {
                        individual.setGene(RandomStringUtils.randomAlphabetic(1).toUpperCase(), i);
                    }
                }
            }
        } else {
            Integer[] chromosome = (Integer[]) individual.getChromosome();
            for (int i = 0; i < chromosome.length; i++) {
                if(Math.random() <= mutationRate) {
                    /*
                     * Swap genes within chromosome as we are doing permutations
                     */
                    int pos2 = random.nextInt(chromosome.length);
                    Integer genePos2 = chromosome[pos2];
                    individual.setGene(pos2, chromosome[i]);
                    individual.setGene(i, genePos2);
                }
            }
        }
    }

    private void swapDuplicates(List<Integer> parent1Chromosome, List<Integer> parent2Chromosome) {
        List<Integer> duplicateIndexParent1 = new ArrayList<>();
        List<Integer> duplicateIndexParent2 = new ArrayList<>();
        for(int i = 0; i < parent1Chromosome.size(); i++) {
            for (int j = i; j < parent1Chromosome.size(); j++) {
                if (parent1Chromosome.get(i).equals(parent1Chromosome.get(j))) {
                    duplicateIndexParent1.add(i);
                }
                if (parent2Chromosome.get(i).equals(parent2Chromosome.get(j))) {
                    duplicateIndexParent2.add(i);
                }
            }
        }
        for (int i = 0; i < duplicateIndexParent1.size(); i++) {
            int duplicateIndexP1 = duplicateIndexParent1.get(i);
            int duplicateIndexP2 = duplicateIndexParent2.get(i);
            Integer swap1 = parent1Chromosome.get(duplicateIndexP1);
            Integer swap2 = parent2Chromosome.get(duplicateIndexP2);
            parent1Chromosome.set(duplicateIndexP1, swap2);
            parent2Chromosome.set(duplicateIndexP2, swap1);
        }
    }

    /**
     * Tournament selection where two random individuals are chosen from the population and passed to the function.
     * Random number r is generated. If parameter @tournamentParameterK is greater than r fitter individual of the two is returned
     * otherwise less fit one is returned (selected)
     * @return IndividualMapReduce Winner of the tournament
     */
    public IndividualMapReduce tournamentSelection(IndividualMapReduce competitor1, IndividualMapReduce competitor2) {
        double r = Math.random();
        IndividualMapReduce fitter = fitterFromTwo(competitor1, competitor2);
        if (r < tournamentParameterK) {
            return fitter;
        } else {
            if (fitter.equals(competitor1)) {
                return competitor2;
            } else {
                return competitor1;
            }
        }
    }

    /**
     * Simple function to compare fitness of two individuals and returns fitter individual of two
     * @return IndividualMapReduce fitter of the two individuals
     */
    private static IndividualMapReduce fitterFromTwo(IndividualMapReduce individual1, IndividualMapReduce individual2) {
        int fitness1 = individual1.getFitness();
        int fitness2 = individual2.getFitness();
        if (fitness1 <= fitness2) {
            return individual2;
        } else {
            return individual1;
        }
    }

    /**
     * Roulette Wheel Selection (RWS) selection method for selecting parent
     * @return IndividualMapReduce parent
     */
    public IndividualMapReduce rwsSelection(List<IndividualMapReduce> population) {
        double sum = 0.0;
        double r = random.nextDouble();
        for (IndividualMapReduce bi : population){
            sum += bi.getProbabilityOfSelection();
            if (sum > r) {
                return bi;
            }
        }
        return population.get(0);
    }

    public static IndividualMapReduce rwsSelectionProbabilityCalculation(IndividualMapReduce ind, double sumOfFitnesses) {
            double probability = ind.getFitness() / sumOfFitnesses;
            ind.setProbabilityOfSelection(probability);
            return ind;
    }

}
