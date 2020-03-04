package rafa.Main.Simulations.Created.FlappyBird;

import java.io.Serializable;

import javax.swing.SwingWorker;

import rafa.GUI.NetworkGraph;
import rafa.Main.Simulations.Simulation;
import rafa.NEAT.Network;
import rafa.NEAT.Population;

public class FlappyBird_Sim implements Simulation, Serializable{


	private static final long serialVersionUID = 1L;

	private int outputNodesNum = 1;
	private int inputNodesNum = 2;

	private FlappyBird_GUI game_GUI;
	FlappyBird_JFrame game_JFrame;
	private Network net;

	public FlappyBird_Sim(){
		game_GUI = new FlappyBird_GUI(this);
	}

	public FlappyBird_Sim(Network net, boolean showGraphics){

		// ***************** Generate windows ********************
		if(showGraphics){
			game_GUI = new FlappyBird_GUI(this);
			game_GUI.enableSound(true);

			NetworkGraph net_graph = new NetworkGraph();
			Population pop = new Population();
			pop.addNetwork(net);
			net_graph.setPopulation(pop);

			game_JFrame = new FlappyBird_JFrame(game_GUI, net_graph, this);	// pass in the parent
		}
		// ******************* end of Generate windows ********************

		new RunFlappyBirdSimulationOnBackground(this, net, showGraphics);
	}

	public int getOutputNodesNum() {
		return outputNodesNum;
	}

	public int getInputNodesNum() {
		return inputNodesNum;
	}

	public double getFitness(double[] results) {
		// Results
		// 0: Distance traveled (in pixels)
		// 1: Average vertical distance from pipe's center when going through pipes
		// 2: Energy spent: Number of flaps
		// 3: number of pipes crossed

		double fitness = 0;

		// Distance traveled (in pixels)
		fitness += results[0];

		// Average vertical distance from pipe's center when going through pipes
		fitness += (1000 * results[3]) / (results[1] + 1);

		// Energy spent: Number of flaps
		fitness -= results[2] * 0;

		return fitness;
	}

	public Network getNetwork(){
		return net;
	}

	public void simulate(Network net, boolean showGraphics){
		this.net = net;

		if(showGraphics){
			simulateWithGraphics();
		}else{
			simulateWithoutGraphics(3);	// average fitness over 3 games
		}
	}


	private void simulateWithGraphics(){
		
		game_GUI.enableDraw(true);
		game_GUI.setup();

		// Results
		// 0: Distance traveled (in pixels)
		// 1: Average vertical distance from pipe's center when going through pipes
		// 2: Energy spent: Number of flaps 

		// create input for Network
		// 0: horizontal distance to next pipe
		// 1: vertical distance to the center of next pipe

	}

	private void simulateWithoutGraphics(int num_games){
		
		game_GUI.enableDraw(false);
		game_GUI.setup();

		// Results
		// 0: Distance traveled (in pixels)
		// 1: Average vertical distance from pipe's center when going through pipes
		// 2: Energy spent: Number of flaps 

		// create input for Network
		// 0: horizontal distance to next pipe
		// 1: vertical distance to the center of next pipe


	}

	boolean outputToMove(double output){
		if(output < 0.){
			return false;	// in range [-1, 0[ don't jump
		}else{
			return true;	// in range [0, 1] jump
		}
	}

	public class RunFlappyBirdSimulationOnBackground extends SwingWorker<Integer, String> {

		private Simulation simulation;
		private Network network;
		private boolean showGraphics;

		public RunFlappyBirdSimulationOnBackground(FlappyBird_Sim sim, Network net, boolean showGraphics){
			this.simulation = sim;
			this.network = net;
			this.showGraphics = showGraphics;
			this.execute();
		}

		@Override
		protected Integer doInBackground() throws Exception {
			simulation.simulate(network, showGraphics);
			return 0;
		}

	}


}
