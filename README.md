# NeuroEvolution-of-Augmenting-Topologies


(Work on progress...)

"NeuroEvolution of Augmenting Topologies (NEAT) is a genetic algorithm for the generation of evolving artificial neural networks. It alters both the weighting parameters and structures of networks, attempting to find a balance between the fitness of evolved solutions and their diversity. It is based on applying three key techniques: tracking genes with history markers to allow crossover among topologies, applying speciation (the evolution of species) to preserve innovations, and developing topologies incrementally from simple initial structures ("complexifying")." Wikipedia

State:

Halted due to university workload (resuming work as soon as possible)...
'Nodes', 'links' and 'networks' classes implemented.
Speciation implemented.
Complexifying structure implemented.
Reading documentation to learn the best constant values for cross-over and other methods (cross-over rate, mutation rate, etc...)
GUI's created to visualize and test network generation, mutation and cross-over. On the image "Networks" in the directory you can see four different networks. Green nodes stand for input nodes, yellow ones stand for bias nodes and red ones to output nodes. The blue nodes are nodes in the hidden layer created by mutation or cross-over.
Programming language: Java

Goals:

Create a general purpose NEAT-based program able to learn task and then execute them.
The first task the program will try to learn is to beat a real-life user in Tic Tac Toe.
(last update: 24/04/2017)
