package rafa.Main.Simulations.Created.XOR;

import java.io.Serializable;
import java.util.Random;

import javax.swing.SwingWorker;

import rafa.GUI.NEATWindow;
import rafa.GUI.NetworkGraph;
import rafa.Main.Simulations.Simulation;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_GUI;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_JFrame;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim.RunSimulationOnBackground;
import rafa.NEAT.Network;
import rafa.NEAT.Population;

public class XOR_Sim implements Simulation, Serializable{

	private static final long serialVersionUID = 1L;

	private int outputNodesNum = 1;
	private int inputNodesNum = 2;

	private Network net;

	private Population population;
	
	private XOR_JFrame xor_JFrame;

	private Random rand;

	int inputA;
	int inputB;
	boolean calculateRandom;

	public static void main(String[] args) {

	}

	public XOR_Sim(Network net, boolean showGraphics, Population population){
		rand = new Random(System.currentTimeMillis());
		this.population = population;
		
		// ***************** Generate windows ********************
		if(showGraphics){
			NetworkGraph net_graph = new NetworkGraph();
			Population pop = new Population();
			pop.addNetwork(net);
			net_graph.setPopulation(pop);

			xor_JFrame = new XOR_JFrame(net_graph, this);	// pass in the parent
		}
		// ******************* end of Generate windows ********************

		SwingWorker<Integer, String> worker = new RunXORSimulationOnBackground(this, net, showGraphics, population);
	}

	// to construct the simulation object
	public XOR_Sim(){
		rand = new Random(System.currentTimeMillis());
	}

	public int getOutputNodesNum() {
		return outputNodesNum;
	}

	public int getInputNodesNum() {
		return inputNodesNum;
	}

	public Network getNetwork(){
		return net;
	}
	
	public Population getPopulation(){
		return population;
	}

	public double getFitness(double[] results) {
		double expected = results[0];
		double outputed = results[1];
		return - Math.pow(expected - outputed, 2);	// squared difference
		// negative: the greater the difference the worst the net is!
	}

	public void setCalculateInputs(int a, int b){
		inputA = a;
		inputB = b;
	}

	public double transformOutput(double output){
		// transform from [-1, 1] to [0, 1]
		return (output + 1) / 2.;
	}
	public void simulate(Network net, boolean showGraphics, Population population) {
		this.net = net;

		if(showGraphics){
			simulateWithGraphics(population);
		}else{
			simulateWithoutGraphics(population);
		}
	}

	private void simulateWithGraphics(Population population){

		float fitness = 0;

		double[] results = new double[2];	// 0: expected output; 1: net given output

		double[] inputs = new double[2];
		if(calculateRandom){
			inputs[0] = (double)rand.nextInt(2);	// 0 or 1;
			inputs[1] = (double)rand.nextInt(2);
		}else{
			inputs[0] = (double)inputA;
			inputs[1] = (double)inputB;
		}
		
		double[] net_inputs = new double[2];
		net_inputs[0] = (inputs[0] * 2) - 1;	// convert from [0, 1] to [-1, 1]
		net_inputs[1] = (inputs[1] * 2) - 1;

		xor_JFrame.setInputs((int)inputs[0], (int)inputs[1]);

		double[] output = net.fireNet(net_inputs);	// result from the XOR operation

		results[0] = (int)inputs[0] ^ (int)inputs[1];
		results[1] = transformOutput(output[0]);

		xor_JFrame.setOutputs((float)results[1], (float)results[0]);

		fitness = (float)getFitness(results);
		xor_JFrame.setFitnessText(fitness);
		net.setNetFitness(fitness);
		net.setSimulationRunning(false);
	}

	private void simulateWithoutGraphics(Population population){

		double fitness = 0;
		double[] results = new double[2];	// 0: expected output; 1: net given output
		double[] inputs = new double[2];
		double[] net_inputs = new double[2];
		net_inputs[0] = (inputs[0] * 2) - 1;	// convert from [0, 1] to [-1, 1]
		net_inputs[1] = (inputs[1] * 2) - 1;

		// for each one of the possibilities in the Truth table calculate the fitness
		for(int in_a = 0; in_a <= 1; in_a++){
			for(int in_b = 0; in_b <= 1; in_b++){

				inputs[0] = (double)in_a;
				inputs[1] = (double)in_b;
				
				net_inputs[0] = (inputs[0] * 2) - 1;	// convert from [0, 1] to [-1, 1]
				net_inputs[1] = (inputs[1] * 2) - 1;
				
				double[] output = net.fireNet(net_inputs);	// result from the XOR operation

				results[0] = (int)inputs[0] ^ (int)inputs[1];	// calculate real XOR
				results[1] = transformOutput(output[0]);

				double single_fitness = getFitness(results);
				fitness += single_fitness;
			}
		}

		net.setNetFitness(fitness);

		System.out.println(net.getNetID() + " --> DONE! " + fitness);
		net.setSimulationRunning(false);

	}

	public class RunXORSimulationOnBackground extends SwingWorker<Integer, String> {

		private Simulation simulation;
		private Network network;
		private boolean showGraphics;
		private Population population;

		public RunXORSimulationOnBackground(XOR_Sim sim, Network net, boolean showGraphics, Population population){
			this.simulation = sim;
			this.network = net;
			this.showGraphics = showGraphics;
			this.population = population;
			this.execute();
		}

		@Override
		protected Integer doInBackground() throws Exception {
			simulation.simulate(network, showGraphics, population);
			return 0;
		}

	}


}