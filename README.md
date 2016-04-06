# Map-Reduce Genetic Algorithms framework using Apache Spark
This project is development of Map-Reduce library/framework for Genetic Algorithms using Apache Spark.
It has implemented 2 parallelization models: Coarse-grained (Island model) and Global Parallelization model (Fitness Evaluation model)
for user to choose from. Island model divides population into smaller isolated islands and evolves them in isolation with occasional
migration between islands. Island model allows to run GAs with large population size as it doesn't require collection of the population from
the worker(executor) nodes to the driver node after each iteration. Global parallelzation model evolves whole population which leads to faster convergence towards solution however
due to implementation limitations of this model it's not suitable for large populations as it requires collection of the population from the worker
nodes to the driver node what can lead to the OutOfMemory error on the driver node.
## Module description
This project consists of 4 modules all enclosed in parent POM:
* MapReduceGAFramework
* gvgai-master
* examplegvgai
* exampletravellingsalesman

**MapReduceGAFramework** is core of the project. It's the framework that performs translates problem into genetic algorithm form and 
 distributes it to the cluster.
 
**gvgai-master** is framework developed by the team from the *University of Essex* which allows user to write AI controller for playing simple
 games. Framework supports different kind of games running in headless mode or in window mode.
 as well.
 
 **examplegvgai** this example runs GA on Pacman game using *gvgai-master* framework and aims to find optimal sequence of the instructions to win the game.
 Other games available.
 
 **exampletravellingsalesman** this example runs classical "Travelling Salesman Problem".
 
## Installation
 In order to install the framework with all examples run *mvn install* in the root directory with root POM.xml file. Alternatively you can
 compile and package individual modules executing Maven step on individual module. Please note example modules depends on *MapReduceGAFramework*
 therfore it's recommended to install it to the local repository first or calling *mvn install* from the root directory with root POM.xml file.
 
## User manual
 For detailed documentation generate/read JavaDoc of the project. 
 **Running this framework and examples requires Apache Spark**
 To implement **MapReduceGAFramework** to your project you must include it as a dependency. Then you must first create the class which implements
 *FitnessFunction* interface which defines method *calculateFitness*. Implementation of this method is problem specific and should provide framework with
 information how to evaluate and rank chromosomes. Framework currently supports 3 different encodings (String, Binary and IntegerPermutation).
 String encoding encodes chromosome using sequence of the string characters, Binary 0,1 and IntegerPermutation encodes chromosome as sequence of
 unique integer numbers (permutation allowing each number to occur only once).
 Once *FitnessFunction* is implemented you need to instantiate class *GARunner* which provides method for running Island model or Global model. This
  method takes number of parameters including our implemented *FitnessFunction* and other genetic parameters such as chromosome encoding, mutation rate,
  crossover rate, selection method etc. *GARunner* methods return solution as the fittest individual chromosome.
