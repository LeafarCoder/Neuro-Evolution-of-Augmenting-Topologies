/**
 * This package collects all the Simulations meant to run using the NEAT algorithm.
 * 
 * Every Simulation Class must implement the Simulation Interface and therefore contain
 * two functions:
 * 	- simulate: Receives a Network 'net' and simulates the environment of the experience
 * 		with net.
 * 	- getFitness: Receives the Results from the 'simulate' function and calculates a fitness
 * 		for the Network.
 * 
 * To generate a Simulation Object run the GenerateSimulationObject class as a Java Application.
 */
/**
 * @author Rafael Correia
 *
 */
package rafa.Main.Simulations;