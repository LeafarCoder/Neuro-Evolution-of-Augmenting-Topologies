package rafa.Main.Simulations;

import rafa.NEAT.Network;
import rafa.NEAT.Population;

public interface Simulation {

	public int getOutputNodesNum();
	public int getInputNodesNum();
	
	public double getFitness(double results[]);
	
	public void simulate(Network net, boolean showGraphics, Population population);
}
