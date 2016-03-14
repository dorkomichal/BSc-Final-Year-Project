package main;

import geneticClasses.*;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Dorko on 03/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GARunner {

    /**
     * Singleton instance of GARunner
     */
    private static GARunner garunner;
    /**
     * Fitness function that's passed to the worker nodes to calculate
     * fitness of the individual
     */
    private FitnessFunction fitnessFunction;
    /**
     * When solving problem with String Individual encoding source can be used to specify
     * set of allowed characters
     */
    private String[] source;
    /**
     * Specifies length of the chromosome of the individual
     */
    private int chromosomeLength;
    /**
     * Size of the population
     */
    private int populationSize;
    /**
     * Max fitness that when any individual meets algorithm terminates as successful
     */
    private int maxFitness;
    /**
     * Specifies if we preserve individual with the highest fitness to the next
     * generation without undergoing crossover. Default value true but can be
     * changed using setter method
     */
    private boolean elitism = true;
    /**
     * Max number of the generations/iterations before algorithm terminates
     */
    private int maxGeneration;
    /**
     * Method used for selection process
     */
    private SelectionMethod selectionMethod;
    /**
     * Boolean value stating whether to use multipoint crossover or simple single point
     * crossover
     */
    private boolean multipointCrossover;
    /**
     * Specifies number of crossover points when multipoint crossover method is used
     */
    private int numberOfCrossoverPoints;
    /**
     * Type of the individual encoding
     */
    IndividualType individualType;

    /**
     * Parameter k used during tournament selection when we generate random number r
     * and if that random number r is smaller than parameter k we choose fitter individual
     * otherwise we choose less fit individual
     */
    private double tournamentParamK = 0.75;
    /**
     * Mutation rate - this parameter has default value 0.01 and can be changed with
     * setter method
     */
    private double mutation = 0.01;
    /**
     * Crossover rate - this parameter has default value 0.7 and can be changed with
     * setter method
     */
    private double crossoverRate = 0.7;
    /**
     * This parameter stops algorithm if max fitness hasn't changed over certain number
     * of the consecutive generations/iterations. Default value is set to 10 but can be changed using setter method
     */
    private int convergenceMax = 10;
    /**
     * Instance of the class encapsulating all genetic operations
     */
    private GeneticOperationsMapReduce geneticOperations;

    /*
     * STATISTICS - Fields used for collecting statistics
     */
    private static boolean enableStatistics = false;

    private List<Double> mean;

    private List<Double> std;

    private List<Double> standardError;

    private double averageFitnessOverGenerations;

    private long lastGenerationMaxFitness;

    private long oneIterationRunningTime;

    /**
     * Private empty constructor
     */
    private GARunner(){}
    /*
     * Constructor called from static getter method that creates and returns GARunner
     */
    private GARunner(FitnessFunction f, IndividualType indType, String[] source, int chromosomeLength, int popSize, int maxFit, int maxGen,
                     SelectionMethod selMeth, boolean multiCross, int numberCrossPoints) {
        this.fitnessFunction = f;
        this.source = source;
        this.individualType = indType;
        this.chromosomeLength = chromosomeLength;
        this.populationSize = popSize;
        this.maxFitness = maxFit;
        this.maxGeneration = maxGen;
        this.selectionMethod = selMeth;
        this.multipointCrossover = multiCross;
        this.numberOfCrossoverPoints = numberCrossPoints;
    }

    /**
     * Method that creates and returns singleton object of GARunner. Mutation rate, crossover rate,
     * tournament k parameter and convergenceMax condition can be set using respective setter methods on the singleton.
     * @param f Fitness Function used to evaluate population
     * @param source set of allowed characters when creating Individual with String chromosome
     * @param indType type of encoding of the individual to be used
     * @param chromosomeLength length of the chromosome of each individual
     * @param popSize size of the initial population
     * @param maxFit fitness value that terminates algorithm when any Individual meets this fitness
     * @param maxGen maximum number of generations/iterations before algorithm terminates
     * @param selMeth selection method to be used
     * @param multiCross false if single point crossover true if multipoint crossover method should be used
     * @param numberCrossPoints if multipoint crossover method is used specifies number of the crossover points
     * @return singleton GARunner object
     */
    public static GARunner getGARunner(FitnessFunction f, IndividualType indType, String[] source, int chromosomeLength, int popSize, int maxFit, int maxGen,
                                       SelectionMethod selMeth, boolean multiCross, int numberCrossPoints) {
        if (garunner != null) {
            return garunner;
        } else {
            garunner = new GARunner(f, indType, source, chromosomeLength, popSize, maxFit, maxGen, selMeth, multiCross, numberCrossPoints);
            return garunner;
        }
    }

    /**
     * Getter for the convergenceMax parameter. Convergence parameter stops algorithm if the highest
     * fitness within population hasn't changed over convergenceMax runs
     * @return value of the convergenceMax parameter
     */
    public int getConvergenceMax() {
        return convergenceMax;
    }

    /**
     * Setter for the convergenceMax parameter. Convergence parameter stops algorithm if the highest
     * fitness within population hasn't changed over convergenceMax runs
     * @param convergenceMax number of generations with unchanged max fitness before algorithm stops
     */
    public void setConvergenceMax(int convergenceMax) {
        this.convergenceMax = convergenceMax;
    }

    /**
     * Sets mutation rate (number between 0 and 1)
     * @param mutation mutation rate (number between 0 and 1)
     */
    public void setMutation(double mutation) {
        this.mutation = mutation;
    }

    /**
     * Sets crossover rate (number between 0 and 1)
     * @param crossoverRate crossover rate (number between 0 and 1)
     */
    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    /**
     * Sets parameter k for tournament selection. Parameter k used during tournament selection
     * when we generate random number r and if that random number r is smaller than parameter k
     * we choose fitter individual otherwise we choose less fit individual
     * @param tournamentParamK parameter k for tournament (number between 0 and 1)
     */
    public void setTournamentParamK(double tournamentParamK) {
        this.tournamentParamK = tournamentParamK;
    }

    /**
     * Sets if elitism should be used. True if yes false otherwise
     * @param elitism sets elitism
     */
    public void setElitism(boolean elitism) {
        this.elitism = elitism;
    }

    /**
     * Sets number of crossover points if multipoint crossover is used
     * @param numberOfCrossoverPoints number of crossover points
     */
    public void setNumberOfCrossoverPoints(int numberOfCrossoverPoints) {
        this.numberOfCrossoverPoints = numberOfCrossoverPoints;
    }

    /**
     * Sets whether multipoint crossover (true) or single point crossover (false)
     * should be used
     * @param multipointCrossover multipoint (true) single point (false)
     */
    public void setMultipointCrossover(boolean multipointCrossover) {
        this.multipointCrossover = multipointCrossover;
    }

    /**
     * Sets selection method used in selection process. Available methods are Tournament selection
     * and Roulette Wheel Selection (RWS)
     * @param selectionMethod selection method (Tournament or RWS)
     */
    public void setSelectionMethod(SelectionMethod selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    /**
     * Sets maximal number of the generations before algorithm stops
     * @param maxGeneration max number of the generations
     */
    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
    }

    /**
     * Sets maximal fitness that terminates algorithm whenever any individual
     * evaluates to this fitness or higher
     * @param maxFitness max desired fitness
     */
    public void setMaxFitness(int maxFitness) {
        this.maxFitness = maxFitness;
    }

    /**
     * Sets size of the population
     * @param populationSize size of the population
     */
    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    /**
     * Sets length of the chromosome
     * @param chromosomeLength length of the chromosome
     */
    public void setChromosomeLength(int chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

    /**
     * Sets set of allowed characters when using String individual encoding
     * @param source set of allowed characters
     */
    public void setSource(String[] source) {
        this.source = source;
    }

    /**
     * Sets fitness function
     * @param fitnessFunction fitness function
     */
    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    /**
     * ANALYTICS PURPOSES ONLY. Allows analytics calculations on each generation to compute mean,
     * standard deviation, standard error, average fitness of the population over the generation.
     * May affect performance - don't use when analytics are not needed
     * @param enableStatistics true if enable false otherwise
     */
    public static void setEnableStatistics(boolean enableStatistics) {
        GARunner.enableStatistics = enableStatistics;
    }

    /**
     * Analytics - get number of the individuals in the last generation having
     * max fitness
     * @return number of individual with max fitness in last generation
     */
    public long getLastGenerationMaxFitness() {
        return lastGenerationMaxFitness;
    }

    /**
     * Get average fitness over all generations
     * @return avg fitness over generations
     */
    public double getAverageFitnessOverGenerations() {
        return averageFitnessOverGenerations;
    }

    /**
     * Getter for standard error in each generation
     * @return standard error
     */
    public List<Double> getStandardError() {
        return standardError;
    }

    /**
     * Getter for standard deviation in each generation
     * @return standard deviation in each generation
     */
    public List<Double> getStd() {
        return std;
    }

    /**
     * Getter for mean in each generation
     * @return mean in each generation
     */
    public List<Double> getMean() {
        return mean;
    }

    /**
     * Getter for running time of one iteration
     * @return running time
     */
    public long getOneIterationRunningTime() {
        return oneIterationRunningTime;
    }

    public Object[] runGA() {
        Driver driver = Driver.getDriver();
        FitnessCalculator fitnessCalculator = new FitnessCalculator(fitnessFunction);
        driver.initializePopulation(fitnessCalculator, chromosomeLength, populationSize, individualType, source);
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(maxFitness);
        geneticOperations = new GeneticOperationsMapReduce(fitnessCalculator, chromosomeLength, tournamentParamK, elitism, mutation, crossoverRate);

        JavaRDD<IndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();
        JavaRDD<IndividualMapReduce> newGeneration;
        long previousFitness = 0;
        int convergenceCounter = 0;
        long start = 0;
        if(enableStatistics) {
            mean = new ArrayList<>();
            standardError = new ArrayList<>();
            std = new ArrayList<>();
        }
        while (true) {
            if(generationCounter == 5) {
                start = System.currentTimeMillis();
            }
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<IndividualMapReduce, Long> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation, fitnessCalculator);

            IndividualMapReduce elite = mapper.getElite(populationWithFitness);
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite, selectionMethod, geneticOperations);
            System.out.println("Selected individuals " + selectedIndividuals.count());
            newGeneration = reducer.reduceCrossover(selectedIndividuals, multipointCrossover, numberOfCrossoverPoints, geneticOperations);

            parallelizedPopulation = newGeneration;
            if(generationCounter == 5) {
                long stop = System.currentTimeMillis();
                oneIterationRunningTime = start - stop;
            }
            if(enableStatistics) {
                generationStatistics(newGeneration);
            }

            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            //Important step for RWS selection is to reset max fitness of current generation
            //and assign new generation of the individuals to the population in order to calculate
            //aggregate fitness of the population necessary for RWS selection method
            if(GlobalFile.getCurrentMaxFitness() == previousFitness) {
                convergenceCounter++;
            } else {
                convergenceCounter = 0;
            }
            previousFitness = GlobalFile.getCurrentMaxFitness();
            if (GlobalFile.isSolutionFound() || generationCounter >= maxGeneration || convergenceCounter >= convergenceMax) {
                if (enableStatistics) {
                    lastGenerationStatistics(newGeneration);
                }
                JavaPairRDD<Long, IndividualMapReduce> finalGeneration = newGeneration.mapToPair(bi -> new Tuple2<Long, IndividualMapReduce>(bi.getFitness(),bi)).sortByKey(false);
                IndividualMapReduce fittestInd = finalGeneration.first()._2;
                GlobalFile.setFittestIndividual(fittestInd);
                break; //if solution is found or generation has converged to max and didn't change for some generations
            }
            generationCounter++;
            GlobalFile.resetCurrentMax();
        }
        System.out.println(GlobalFile.getFittestIndividual().toString());
        return GlobalFile.getFittestIndividual().getChromosome();
    }

    private void generationStatistics(JavaRDD<IndividualMapReduce> population) {
        JavaDoubleRDD elements = population.mapToDouble(IndividualMapReduce::getFitness);
        long numberOfElements = elements.count();
        mean.add(elements.mean());
        std.add(elements.stdev());
        standardError.add(elements.sampleStdev()/Math.sqrt(numberOfElements));
    }

    private void lastGenerationStatistics(JavaRDD<IndividualMapReduce> population) {
        averageFitnessOverGenerations = mean.stream().reduce((a,b) -> (a+b)).get()/mean.size();
        long maxFitnessLastGen = GlobalFile.getCurrentMaxFitness();
        lastGenerationMaxFitness = population.filter(ind -> ind.getFitness() >= maxFitnessLastGen).count();
    }
}
