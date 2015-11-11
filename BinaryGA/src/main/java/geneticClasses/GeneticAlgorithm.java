package geneticClasses;

import org.apache.commons.lang3.ArrayUtils;

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
public class GeneticAlgorithm {

    private static double crossoverRate = 0.7;
    private static double mutationRate = 0.001;
    private static boolean elitism = true;
    private static double tournamentParameterK = 0.75;
    private static Random random = new Random();

    public static void setElitism(boolean elitism) {
        GeneticAlgorithm.elitism = elitism;
    }

    public static void setTournamentParameterK(double tournamentParameterK) {
        GeneticAlgorithm.tournamentParameterK = tournamentParameterK;
    }

    public static void setCrossoverRate(double crossoverRate) {
        GeneticAlgorithm.crossoverRate = crossoverRate;
    }

    public static void setMutationRate(double mutationRate) {
        GeneticAlgorithm.mutationRate = mutationRate;
    }

    /**
     * Evolution method that uses single point crossover method with Tournament Selection method
     * @param currentPopulation population to be evolved
     * @return new evolved generation
     */
    public static Population evolveWithSinglePointTournament(Population currentPopulation) {
        return evolution(currentPopulation, false, 1, true);
    }

    /**
     * Evolution method that uses multi point crossover method with Tournament Selection method
     * @param currentPopulation population to be evolved
     * @param numberOfCrossPoints number of crossover points to be used for crossover
     * @return new evolved generation
     */
    public static Population evolveWithMultiPointTournament(Population currentPopulation, int numberOfCrossPoints) {
        return evolution(currentPopulation, true, numberOfCrossPoints, true);
    }

    /**
     * Evolution method that uses single point crossover and Roulette Wheel selection method
     * @param currentPopulation population to be evolved
     * @return new generation
     */
    public static Population evolveWithSinglePointRoulette(Population currentPopulation) {
        return evolution(currentPopulation, false, 1, false);
    }

    /**
     * Evolution method that uses multiple point crossover and Roulette Wheel selection method
     * @param currentPopulation population to be evolved
     * @param numberOfCrossPoints number of crossover points to be used for crossover
     * @return new evolved generation
     */
    public static Population evolveWithMultiPointRoulette(Population currentPopulation, int numberOfCrossPoints) {
        return evolution(currentPopulation, true, numberOfCrossPoints, false);
    }

    /**
     * Evolution function that evolves population using either single point or multi point crossover method
     * @param currentPopulation population to be evolved
     * @param multipoint flag to define whether single point or multi point crossover should be used
     * @param numberOfCrossPoints number of crossover points if multi point crossover is used
     * @return new evolved generation
     */
    private static Population evolution(Population currentPopulation, boolean multipoint, int numberOfCrossPoints, boolean tournament) {
        Population newGeneration = new Population(currentPopulation.getSizeOfPopulation());
        int allocated = 0;
        boolean firstRound = true;
        if (elitism) {
            newGeneration.saveBinaryIndividual(currentPopulation.getFittestIndividual(), 0);
            allocated = 1;
        }
        for (int i = allocated; i < currentPopulation.getSizeOfPopulation() -1; i += 2) {
            BinaryIndividual parent1;
            BinaryIndividual parent2;
            if (tournament) {
                int[] randoms = random.ints(0, currentPopulation.getSizeOfPopulation()).distinct().limit(4).toArray();
                parent1 = tournamentSelection(currentPopulation.getIndividual(randoms[0]), currentPopulation.getIndividual(randoms[1]));
                parent2 = tournamentSelection(currentPopulation.getIndividual(randoms[2]), currentPopulation.getIndividual(randoms[3]));
            } else {
                parent1 = rwsSelection(currentPopulation, firstRound);
                parent2 = rwsSelection(currentPopulation, firstRound);
                firstRound = false;
            }
            BinaryIndividual[] offspring;
            if (Math.random() <= crossoverRate) {
                if (!multipoint) {
                    offspring = singlePointCrossover(parent1, parent2);
                } else {
                    offspring = multiPointCrossover(parent1, parent2, numberOfCrossPoints);
                }
            } else {
                offspring = new BinaryIndividual[]{parent1, parent2};
            }
            newGeneration.saveBinaryIndividual(offspring[0], allocated);
            newGeneration.saveBinaryIndividual(offspring[1], allocated + 1);
            allocated += 2;

        }
        for (int j = allocated; j < newGeneration.getSizeOfPopulation(); j++) {
            int randomNumber = random.nextInt(currentPopulation.getSizeOfPopulation());
            newGeneration.saveBinaryIndividual(currentPopulation.getIndividual(randomNumber), allocated);
            allocated ++;
        }
        FitnessCalculator.calculateFitnessOfPopulation(newGeneration);
        return newGeneration;
    }

    /**
     * Single point crossover is crossover where random single point is generated and chromosomes are split in two
     * parts producing two head and two tails sections. The two tail sections are then swapped to produce new individuals
     * (chromosomes)
     * @param parent1 First parent selected for crossover
     * @param parent2 Second parent selected for crossover
     * @return Offspring produced during crossover as array of BinaryIndividual
     */
    private static BinaryIndividual[] singlePointCrossover(BinaryIndividual parent1, BinaryIndividual parent2) {
        int crossoverPoint = random.nextInt(parent1.lengthOfChromosome());
        byte[] parent1Chromosome = parent1.getChromosome();
        byte[] parent2Chromosome = parent2.getChromosome();

        byte[] parent1ChromosomePart1 = Arrays.copyOfRange(parent1Chromosome, 0, crossoverPoint);
        byte[] parent1ChromosomePart2 = Arrays.copyOfRange(parent1Chromosome, crossoverPoint, parent1Chromosome.length);

        byte[] parent2ChromosomePart1 = Arrays.copyOfRange(parent2Chromosome, 0, crossoverPoint);
        byte[] parent2ChromosomePart2 = Arrays.copyOfRange(parent2Chromosome, crossoverPoint, parent2Chromosome.length);

        BinaryIndividual child1 = new BinaryIndividual();
        child1.setChromosome(ArrayUtils.addAll(parent1ChromosomePart1, parent2ChromosomePart2));
        mutate(child1);
        child1.calculateFitness();
        BinaryIndividual child2 = new BinaryIndividual();
        child2.setChromosome(ArrayUtils.addAll(parent2ChromosomePart1, parent1ChromosomePart2));
        mutate(child2);
        child2.calculateFitness();

        return new BinaryIndividual[] {child1, child2};
    }

    private static BinaryIndividual[] multiPointCrossover(BinaryIndividual parent1, BinaryIndividual parent2, int numberOfPoints) {
        int[] crossoverPoints = random.ints(0, parent1.lengthOfChromosome() - 1).distinct().limit(numberOfPoints).toArray();
        Arrays.sort(crossoverPoints);
        List<byte[]> parent1ChromosomeParts = new ArrayList<byte[]>();
        List<byte[]> parent2ChromosomeParts = new ArrayList<byte[]>();
        byte[] parent1Chromosome = parent1.getChromosome();
        byte[] parent2Chromosome = parent2.getChromosome();
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
        byte[] child1Chromosome = parent1ChromosomeParts.get(0);
        byte[] child2Chromosome = parent2ChromosomeParts.get(0);
        for(int i = 1; i < parent1ChromosomeParts.size(); i++) {
            if(i % 2 == 0) {
                child1Chromosome = ArrayUtils.addAll(child1Chromosome,parent1ChromosomeParts.get(i));
                child2Chromosome = ArrayUtils.addAll(child2Chromosome, parent2ChromosomeParts.get(i));
            } else {
                child1Chromosome = ArrayUtils.addAll(child1Chromosome,parent2ChromosomeParts.get(i));
                child2Chromosome = ArrayUtils.addAll(child2Chromosome, parent1ChromosomeParts.get(i));
            }
        }
        BinaryIndividual child1 = new BinaryIndividual();
        child1.setChromosome(child1Chromosome);
        mutate(child1);
        BinaryIndividual child2 = new BinaryIndividual();
        child2.setChromosome(child2Chromosome);
        mutate(child2);
        return new BinaryIndividual[]{child1, child2};
    }

    /**
     * Mutation of the individuals chromosome. Flips value of the bit if randomly generated number is lower than
     * mutation rate.
     * @param individual the individual which chromosome will undergo mutation
     */
    protected static void mutate(BinaryIndividual individual) {
        byte[] chromosome = individual.getChromosome();
        for(int i = 0; i < chromosome.length; i++) {
            if (Math.random() <= mutationRate) {
                if (chromosome[i] == 1) {
                    individual.setGene((byte) 0, i);
                } else {
                    individual.setGene((byte) 1, i);
                }
            }
        }
    }

    /**
     * Tournament selection where two random individuals are chosen from the population and passed to the function.
     * Random number r is generated. If parameter @tournamentParameterK is greater than r fitter individual of the two is returned
     * otherwise less fit one is returned (selected)
     * @return BinaryIndividual Winner of the tournament
     */
    private static BinaryIndividual tournamentSelection(BinaryIndividual competitor1, BinaryIndividual competitor2) {
        double r = Math.random();
        BinaryIndividual fitter = fitterFromTwo(competitor1, competitor2);
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
     * @return BinaryIndividual fitter of the two individuals
     */
    private static BinaryIndividual fitterFromTwo(BinaryIndividual individual1, BinaryIndividual individual2) {
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
     * @return BinaryIndividual parent
     */
    public static BinaryIndividual rwsSelection(Population population, boolean firstRound) {
        if (firstRound) {
            population.calculateSumOfFitnesses();
            for (int i = 0; i < population.getSizeOfPopulation(); i++) {
                BinaryIndividual individual = population.getIndividual(i);
                double probability = individual.getFitness() / population.getSumOfFitnesses();
                individual.setProbabilityOfSelection(probability);
            }
        }
        double sum = 0.0;
        double r = random.nextDouble();
        for (int i = 0; i < population.getSizeOfPopulation(); i++){
            sum += population.getIndividual(i).getProbabilityOfSelection();
            if (sum > r) {
                return population.getIndividual(i);
            }
        }
        return population.getFittestIndividual();
    }

}
