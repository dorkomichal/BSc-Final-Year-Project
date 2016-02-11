package geneticClasses;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomStringUtils;

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
public final class GeneticOperationsMapReduce {

    private static double crossoverRate = 0.7;
    private static double mutationRate = 0.001;
    private static boolean elitism = true;
    private static double tournamentParameterK = 0.75;
    private static Random random = new Random();

    public static void setElitism(boolean elitism) {
        GeneticOperationsMapReduce.elitism = elitism;
    }

    public static double getCrossoverRate() {
        return crossoverRate;
    }

    public static boolean isElitism() {
        return elitism;
    }

    public static void setTournamentParameterK(double tournamentParameterK) {
        GeneticOperationsMapReduce.tournamentParameterK = tournamentParameterK;
    }

    public static void setCrossoverRate(double crossoverRate) {
        GeneticOperationsMapReduce.crossoverRate = crossoverRate;
    }

    public static void setMutationRate(double mutationRate) {
        GeneticOperationsMapReduce.mutationRate = mutationRate;
    }

    /**
     * Single point crossover is crossover where random single point is generated and chromosomes are split in two
     * parts producing two head and two tails sections. The two tail sections are then swapped to produce new individuals
     * (chromosomes)
     * @param parent1 First parent selected for crossover
     * @param parent2 Second parent selected for crossover
     * @return fitter individual from two new individuals
     */
    public static IndividualMapReduce singlePointCrossover(IndividualMapReduce parent1, IndividualMapReduce parent2) {
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
            child1 = new BinaryIndividualMapReduce();
            child2 = new BinaryIndividualMapReduce();
        } else {
            child1 = new StringIndividualMapReduce();
            child2 = new StringIndividualMapReduce();
        }

        child1.setChromosome(ArrayUtils.addAll(parent1ChromosomePart1, parent2ChromosomePart2));
        mutate(child1);
        child1.calculateFitness();

        child2.setChromosome(ArrayUtils.addAll(parent2ChromosomePart1, parent1ChromosomePart2));
        mutate(child2);
        child2.calculateFitness();

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
    public static IndividualMapReduce multiPointCrossover(IndividualMapReduce parent1, IndividualMapReduce parent2, int numberOfPoints) {
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
            child1 = new BinaryIndividualMapReduce();
            child2 = new BinaryIndividualMapReduce();
        } else {
            child1 = new StringIndividualMapReduce();
            child2 = new StringIndividualMapReduce();
        }

        child1.setChromosome(child1Chromosome);
        mutate(child1);
        child1.calculateFitness();

        child2.setChromosome(child2Chromosome);
        mutate(child2);
        child2.calculateFitness();

        return fitterFromTwo(child1, child2) ;
    }

    /**
     * Mutation of the individuals chromosome. Flips value of the bit if randomly generated number is lower than
     * mutation rate.
     * @param individual the individual which chromosome will undergo mutation
     */
    protected static void mutate(IndividualMapReduce individual) {
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
        } else {
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
        }
    }

    /**
     * Tournament selection where two random individuals are chosen from the population and passed to the function.
     * Random number r is generated. If parameter @tournamentParameterK is greater than r fitter individual of the two is returned
     * otherwise less fit one is returned (selected)
     * @return IndividualMapReduce Winner of the tournament
     */
    public static IndividualMapReduce tournamentSelection(IndividualMapReduce competitor1, IndividualMapReduce competitor2) {
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
    public static IndividualMapReduce rwsSelection(List<IndividualMapReduce> population) {
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
