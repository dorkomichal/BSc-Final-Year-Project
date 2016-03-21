package geneticClasses;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Created by Michal Dorko on 30/10/15.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public final class GeneticOperationsMapReduce implements Serializable {

    /**
     * Creates instance of the genetic operations class which encapsulates all genetic operations
     * including selection methods and crossover methods necessary to run genetic algorithm.
     * @param fc instance of the FitnessCalculator with concrete implementation of the {@link FitnessFunction}
     * @param chromosomeLength length of the individual's chromosome
     * @param tournamentParameterK parameter used within tournament selection as probability of selecting
     *                             less fit individual - higher parameter k lower probability of selecting less fit individual
     * @param elitism boolean value to determine whether to use elitism (true) or not (false)
     * @param mutationRate rate of mutation
     * @param crossoverRate rate of crossover (some individuals are not crossed over even when selected for crossover)
     */
    public GeneticOperationsMapReduce(FitnessCalculator fc, Integer chromosomeLength, double tournamentParameterK, boolean elitism, double mutationRate, double crossoverRate) {
        this.fc = fc;
        this.chromosomeLength = chromosomeLength;
        this.tournamentParameterK = tournamentParameterK;
        this.elitism = elitism;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
    }

    /**
     * Crossover rate (value between 0 and 1)
     */
    private double crossoverRate;
    /**
     * Mutation rate (value between 0 and 1)
     */
    private double mutationRate;
    /**
     * Flag to specify whether to use elitism or not
     */
    private boolean elitism = true;
    /**
     * Tournament parameter k - probability of choosing fitter of the two
     * (value between 0 and 1)
     */
    private double tournamentParameterK = 0.75;
    private Random random = new Random();
    /**
     * Fitness Calculator used to evaluate fitness of the individuals
     */
    private FitnessCalculator fc;
    /**
     * Length of the chromosome
     */
    private Integer chromosomeLength;

    /**
     * Setter for elitism
     * @param elitism boolean flag (true) if use elitism (false) otherwise
     */
    public void setElitism(boolean elitism) {
        this.elitism = elitism;
    }

    /**
     * Getter for crossover rate
     * @return crossover rate
     */
    public double getCrossoverRate() {
        return crossoverRate;
    }

    /**
     * Getter for elitism
     * @return elitism flag
     */
    public boolean isElitism() {
        return elitism;
    }

    /**
     * Setter for tournament k parameter (value between 0 and 1)
     * @param tournamentParameterK tournament parameter
     */
    public void setTournamentParameterK(double tournamentParameterK) {
        this.tournamentParameterK = tournamentParameterK;
    }

    /**
     * Setter for crossover rate (value between 0 and 1)
     * @param crossoverRate rate of the crossover
     */
    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    /**
     * Setter for mutation rate
     * @param mutationRate mutation rate (value between 0 and 1)
     */
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    /**
     * Single point crossover is crossover where random single point is generated and chromosomes are split in two
     * parts producing two head and two tails sections. The two tail sections are then swapped to produce new individuals
     * (chromosomes)
     *
     * @param parent1 First parent selected for crossover
     * @param parent2 Second parent selected for crossover
     * @return fitter individual from two new individuals
     */
    public IndividualMapReduce singlePointCrossover(IndividualMapReduce parent1, IndividualMapReduce parent2) {
        int crossoverPoint = random.nextInt(parent1.lengthOfChromosome());
        Object[] parent1Chromosome = parent1.getChromosome();
        Object[] parent2Chromosome = parent2.getChromosome();
        //Split chromosome in two parts
        Object[] parent1ChromosomePart1 = Arrays.copyOfRange(parent1Chromosome, 0, crossoverPoint);
        Object[] parent1ChromosomePart2 = Arrays.copyOfRange(parent1Chromosome, crossoverPoint, parent1Chromosome.length);
        //Split chromosome in two parts
        Object[] parent2ChromosomePart1 = Arrays.copyOfRange(parent2Chromosome, 0, crossoverPoint);
        Object[] parent2ChromosomePart2 = Arrays.copyOfRange(parent2Chromosome, crossoverPoint, parent2Chromosome.length);

        IndividualMapReduce child1;
        IndividualMapReduce child2;
        //Swap chromosome parts and assign them to new individual
        Object[] child1Chromosome = ArrayUtils.addAll(parent1ChromosomePart1, parent2ChromosomePart2);
        Object[] child2Chromosome = ArrayUtils.addAll(parent2ChromosomePart1, parent1ChromosomePart2);
        if (parent1 instanceof BinaryIndividualMapReduce) {
            child1 = new BinaryIndividualMapReduce(chromosomeLength);
            child2 = new BinaryIndividualMapReduce(chromosomeLength);
        } else if (parent1 instanceof StringIndividualMapReduce) {
            child1 = new StringIndividualMapReduce(chromosomeLength);
            child2 = new StringIndividualMapReduce(chromosomeLength);
        } else {
            swapDuplicates((Integer[]) child1Chromosome, (Integer[]) child2Chromosome);
            child1 = new IntPermutationIndividualMapReduce(chromosomeLength);
            child2 = new IntPermutationIndividualMapReduce(chromosomeLength);
        }

        child1.setChromosome(child1Chromosome);
        child2.setChromosome(child2Chromosome);
        mutate(child1);
        mutate(child2);
        child1.calculateFitness(fc);
        child2.calculateFitness(fc);

        return fitterFromTwo(child1, child2);
    }

    /**
     * Multi point crossover is crossover where multiple random crossover points are generated and chromosomes
     * are folded over those crossover points
     *
     * @param parent1        First parent selected for crossover
     * @param parent2        Second parent selected for crossover
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
        //Split chromosome into n parts where n is number of crossover points + 1
        int prev = 0;
        for (int i : crossoverPoints) {
            parent1ChromosomeParts.add(Arrays.copyOfRange(parent1Chromosome, prev, i));
            parent2ChromosomeParts.add(Arrays.copyOfRange(parent2Chromosome, prev, i));
            prev = i;
        }
        // if last crossover point isn't last index assign remaining part of the chromosome
        if (prev < parent1Chromosome.length) {
            parent1ChromosomeParts.add(Arrays.copyOfRange(parent1Chromosome, prev, parent1Chromosome.length));
            parent2ChromosomeParts.add(Arrays.copyOfRange(parent2Chromosome, prev, parent2Chromosome.length));
        }
        Object[] child1Chromosome = parent1ChromosomeParts.get(0);
        Object[] child2Chromosome = parent2ChromosomeParts.get(0);
        //add up chromosome parts alternating parts from parent 1 and parent 2 into child 1 and 2
        for (int i = 1; i < parent1ChromosomeParts.size(); i++) {
            if (i % 2 == 0) {
                child1Chromosome = ArrayUtils.addAll(child1Chromosome, parent1ChromosomeParts.get(i));
                child2Chromosome = ArrayUtils.addAll(child2Chromosome, parent2ChromosomeParts.get(i));
            } else {
                child1Chromosome = ArrayUtils.addAll(child1Chromosome, parent2ChromosomeParts.get(i));
                child2Chromosome = ArrayUtils.addAll(child2Chromosome, parent1ChromosomeParts.get(i));
            }
        }
        // create new individual with newly created chromosome
        IndividualMapReduce child1;
        IndividualMapReduce child2;
        if (parent1 instanceof BinaryIndividualMapReduce) {
            child1 = new BinaryIndividualMapReduce(chromosomeLength);
            child2 = new BinaryIndividualMapReduce(chromosomeLength);
        } else if (parent1 instanceof StringIndividualMapReduce) {
            child1 = new StringIndividualMapReduce(chromosomeLength);
            child2 = new StringIndividualMapReduce(chromosomeLength);
        } else {
            swapDuplicates((Integer[]) child1Chromosome, (Integer[]) child2Chromosome);
            child1 = new IntPermutationIndividualMapReduce(chromosomeLength);
            child2 = new IntPermutationIndividualMapReduce(chromosomeLength);
        }

        child1.setChromosome(child1Chromosome);
        child2.setChromosome(child2Chromosome);
        mutate(child1);
        mutate(child2);
        child1.calculateFitness(fc);
        child2.calculateFitness(fc);

        return fitterFromTwo(child1, child2);
    }

    /**
     * Mutation of the individuals chromosome. Flips value of the bit if randomly generated number is lower than
     * mutation rate.
     *
     * @param individual the individual which chromosome will undergo mutation
     */
    protected void mutate(IndividualMapReduce individual) {
        // if individual is Binary individual mutation will flip the bit
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
        } else if (individual instanceof StringIndividualMapReduce) {
            //if individual is String individual mutation will either generate random alphanumeric character
            //or get random character from set of allowed characters stored in "source[]" field
            StringIndividualMapReduce ind = (StringIndividualMapReduce) individual;
            String[] source = ind.getSource();
            String[] chromosome = (String[]) individual.getChromosome();
            int chromosomeLength = individual.lengthOfChromosome();
            for (int i = 0; i < chromosome.length; i++) {
                if (Math.random() <= mutationRate) {
                    if (source != null) {
                        int ran = random.nextInt(chromosomeLength);
                        individual.setGene(source[ran], i);
                    } else {
                        individual.setGene(RandomStringUtils.randomAlphabetic(1).toUpperCase(), i);
                    }
                }
            }
        } else {
            //if individual is of type Integer Permutation we randomly swap two genes within the chromosome
            Integer[] chromosome = (Integer[]) individual.getChromosome();
            for (int i = 0; i < chromosome.length; i++) {
                if (Math.random() <= mutationRate) {
                    /*
                     * Swap genes within chromosome as we are doing permutations
                     */
                    int pos2 = random.nextInt(chromosome.length);
                    Integer genePos2 = chromosome[pos2];
                    individual.setGene(chromosome[i], pos2);
                    individual.setGene(genePos2, i);
                }
            }
        }
    }

    /**
     * This method swaps duplicates in Integer Permutation individual after crossover operation
     * as permutation allows only one unique integer within chromosome therefore we have to swap duplicates
     * @param parent1Chromosome
     * @param parent2Chromosome
     */
    private void swapDuplicates(Object[] parent1Chromosome, Object[] parent2Chromosome) {
        List<Integer> duplicateIndexParent1 = new ArrayList<>();
        List<Integer> duplicateIndexParent2 = new ArrayList<>();
        HashSet<Object> hashParent1 = new HashSet<>();
        HashSet<Object> hashParent2 = new HashSet<>();

        for (int i = 0; i < parent1Chromosome.length; i++) {
            if (!hashParent1.add(parent1Chromosome[i])) {
                duplicateIndexParent1.add(i);
            }
            if (!hashParent2.add(parent2Chromosome[i])) {
                duplicateIndexParent2.add(i);
            }
        }
        for (int i = 0; i < duplicateIndexParent1.size(); i++) {
            int duplicateIndexP1 = duplicateIndexParent1.get(i);
            int duplicateIndexP2 = duplicateIndexParent2.get(i);
            Object swap1 = parent1Chromosome[duplicateIndexP1];
            Object swap2 = parent2Chromosome[duplicateIndexP2];
            parent1Chromosome[duplicateIndexP1] = swap2;
            parent2Chromosome[duplicateIndexP2] = swap1;
        }
    }

    /**
     * Tournament selection where two random individuals are chosen from the population and passed to the function.
     * Random number r is generated. If parameter @tournamentParameterK is greater than r fitter individual of the two is returned
     * otherwise less fit one is returned (selected)
     *
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
        long fitness1 = individual1.getFitness();
        long fitness2 = individual2.getFitness();
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
        for (IndividualMapReduce bi : population) {
            sum += bi.getProbabilityOfSelection();
            if (sum > r) {
                return bi;
            }
        }
        return population.get(0);
    }

    /**
     * Method for calculating probability of selecting the individual based on it's fitness and overall
     * fitness of the population. This operation is required only for Roulette Wheel selection
     * @param ind individual for which probability is to be calculated
     * @param sumOfFitnesses overall fitness of the population
     * @return individual with calculated probability
     */
    public IndividualMapReduce rwsSelectionProbabilityCalculation(IndividualMapReduce ind, double sumOfFitnesses) {
        double probability = ind.getFitness() / sumOfFitnesses;
        ind.setProbabilityOfSelection(probability);
        return ind;
    }

}
