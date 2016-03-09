package main;

import geneticClasses.*;
import mapreduce.Driver;
import mapreduce.GlobalFile;
import mapreduce.Mapper;
import mapreduce.Reducer;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.Tuple2;

/**
 * Created by Michal Dorko on 03/03/16.
 * BSc Final Year project
 * King's College London
 * Map-Reduce library for Genetic Algorithms
 * Licensed under the Academic Free License version 3.0
 */
public class GARunner {

    private static GARunner garunner;
    private FitnessFunction fitnessFunction;
    private String[] source;
    private int chromosomeLength;
    private int populationSize;
    private int maxFitness;
    private boolean elitism = true;
    private double tournamentParamK = 0.75;

    private double mutation = 0.01;
    private double crossoverRate = 0.7;
    private int maxGeneration;

    private SelectionMethod selectionMethod;
    private boolean multipointCrossover;
    private int numberOfCrossoverPoints;
    private GeneticOperationsMapReduce geneticOperations;

    private GARunner(){}

    private GARunner(FitnessFunction f, String[] source, int chromosomeLength, int popSize, int maxFit, int maxGen,
                     SelectionMethod selMeth, boolean multiCross, int numberCrossPoints) {
        this.fitnessFunction = f;
        this.source = source;
        this.chromosomeLength = chromosomeLength;
        this.populationSize = popSize;
        this.maxFitness = maxFit;
        this.maxGeneration = maxGen;
        this.selectionMethod = selMeth;
        this.multipointCrossover = multiCross;
        this.numberOfCrossoverPoints = numberCrossPoints;
    }

    /**
     * Method that creates and returns singleton object of GARunner. Mutation rate, crossover rate and
     * tournament k parameter can be set using respective setter methods on the singleton.
     * @param f Fitness Function used to evaluate population
     * @param source set of allowed characters when creating Individual with String chromosome
     * @param chromosomeLength length of the chromosome of each individual
     * @param popSize size of the initial population
     * @param maxFit fitness value that terminates algorithm when any Individual meets this fitness
     * @param maxGen maximum number of generations/iterations before algorithm terminates
     * @param selMeth selection method to be used
     * @param multiCross false if single point crossover true if multipoint crossover method should be used
     * @param numberCrossPoints if multipoint crossover method is used specifies number of the crossover points
     * @return singleton GARunner object
     */
    public static GARunner getGARunner(FitnessFunction f, String[] source, int chromosomeLength, int popSize, int maxFit, int maxGen,
                                       SelectionMethod selMeth, boolean multiCross, int numberCrossPoints) {
        if (garunner != null) {
            return garunner;
        } else {
            garunner = new GARunner(f, source, chromosomeLength, popSize, maxFit, maxGen, selMeth, multiCross, numberCrossPoints);
            return garunner;
        }
    }

    public void setMutation(double mutation) {
        this.mutation = mutation;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public void setTournamentParamK(double tournamentParamK) {
        this.tournamentParamK = tournamentParamK;
    }

    public void setElitism(boolean elitism) {
        this.elitism = elitism;
    }

    public void setGeneticOperations(GeneticOperationsMapReduce geneticOperations) {
        this.geneticOperations = geneticOperations;
    }

    public void setNumberOfCrossoverPoints(int numberOfCrossoverPoints) {
        this.numberOfCrossoverPoints = numberOfCrossoverPoints;
    }

    public void setMultipointCrossover(boolean multipointCrossover) {
        this.multipointCrossover = multipointCrossover;
    }

    public void setSelectionMethod(SelectionMethod selectionMethod) {
        this.selectionMethod = selectionMethod;
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
    }

    public void setMaxFitness(int maxFitness) {
        this.maxFitness = maxFitness;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setChromosomeLength(int chromosomeLength) {
        this.chromosomeLength = chromosomeLength;
    }

    public void setSource(String[] source) {
        this.source = source;
    }

    public void setFitnessFunction(FitnessFunction fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public Object[] runGA() {
        Driver driver = Driver.getDriver();
        FitnessCalculator fitnessCalculator = new FitnessCalculator(fitnessFunction);
        driver.initializePopulation(fitnessCalculator, chromosomeLength, populationSize, IndividualType.String, source); //50
        Mapper mapper = Mapper.getMapper();
        Reducer reducer = Reducer.getReducer();
        int generationCounter = 1;
        GlobalFile.setMaxFitness(maxFitness); //1000
        geneticOperations = new GeneticOperationsMapReduce(fitnessCalculator, chromosomeLength, tournamentParamK, elitism, mutation, crossoverRate);

        JavaRDD<IndividualMapReduce> parallelizedPopulation = driver.getPopulationParallelized();
        JavaRDD<IndividualMapReduce> newGeneration;
        while (true) {
            System.out.println("Generation " + generationCounter);
            JavaPairRDD<IndividualMapReduce, Integer> populationWithFitness = mapper.mapCalculateFitness(parallelizedPopulation, fitnessCalculator);

            IndividualMapReduce elite = mapper.getElite(populationWithFitness);
            JavaRDD<CrossoverPair> selectedIndividuals = mapper.mapSelection(populationWithFitness, elite, selectionMethod, geneticOperations);
            //System.out.println("Size of selected individuals: " + selectedIndividuals.count());
            newGeneration = reducer.reduceCrossover(selectedIndividuals, multipointCrossover, numberOfCrossoverPoints, geneticOperations);

            parallelizedPopulation = newGeneration;

            System.out.println("Fittest Individual " + GlobalFile.getCurrentMaxFitness());
            //Important step for RWS selection is to reset max fitness of current generation
            //and assign new generation of the individuals to the population in order to calculate
            //aggregate fitness of the population necessary for RWS selection method
            if (GlobalFile.isSolutionFound() || generationCounter >= maxGeneration) {
                JavaPairRDD<Integer, IndividualMapReduce> finalGereration = newGeneration.mapToPair(bi -> new Tuple2<Integer, IndividualMapReduce>(bi.getFitness(),bi)).sortByKey(false);
                IndividualMapReduce fittestInd = finalGereration.first()._2;
                GlobalFile.setFittestIndividual(fittestInd);
                break; //if soulution is found or generation has converged to max and didn't change for some generations
            }
            generationCounter++;
            GlobalFile.resetCurrentMax();
            GlobalFile.resetMaxNotChanged();
        }
        System.out.println(GlobalFile.getFittestIndividual().toString());
        return GlobalFile.getFittestIndividual().getChromosome();
    }
}
