package rafa.Main.Simulations.Created.TicTacToe;

import rafa.GUI.NetworkGraph;
import rafa.Main.Simulations.Simulation;
import rafa.NEAT.Network;
import rafa.NEAT.Population;

import java.io.Serializable;
import java.util.*;

import processing.core.PConstants;

import java.util.Scanner;

import javax.swing.SwingWorker;


public class TicTacToe_Sim implements Simulation, Serializable{

	/*
	 * Results:
	 * Index | Result description
	 * 0 | End game state: Win(1); Loss(-1); Tie(0)
	 * 1 | Number of total turns (both sides) until the end
	 * 2 | Who started? Network (1); PC(-1)
	 * 3 | Number of wrong moves (moved into an already occupied cell)
	 */

	private static final long serialVersionUID = 1L;

	private int outputNodesNum = 2;
	private int inputNodesNum = 9;

	/* who plays against the NET?
	 * opponentMode = 0: Computer
	 * opponentMode = 1: User
	 */
	private int opponentMode = 1;	

	private TicTacToe_GUI TTT_GUI;
	private TicTacToe_JFrame TTT_JFrame;
	private Network net;

	public TicTacToe_Sim(Network net, boolean showGraphics){

		// ***************** Generate windows ********************
		if(showGraphics){
			TTT_GUI = new TicTacToe_GUI(3);


			NetworkGraph net_graph = new NetworkGraph();
			Population pop = new Population();
			pop.addNetwork(net);
			net_graph.setPopulation(pop);

			TTT_JFrame = new TicTacToe_JFrame(TTT_GUI, net_graph, this);	// pass in the parent
		}
		// ******************* end of Generate windows ********************


		new RunSimulationOnBackground(this, net, showGraphics);
	}

	public TicTacToe_Sim(){

	}

	public int getOutputNodesNum(){
		return outputNodesNum;
	}

	public int getInputNodesNum(){
		return inputNodesNum;
	}

	public Network getNetwork(){
		return net;
	}

	public double getFitness(double[] results) {
		double fitness = 0;

		// End game state (most influence)
		fitness += results[0] * 50;

		// Number of total turns (try to minimize)
		fitness -= results[1];

		// Who started? (does not influence for now...)
		fitness += results[2] * 0;

		// Number of wrong moves
		fitness -= results[3] * 70;

		return fitness;
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

		TTT_GUI.resetBoard();

		GameTicTacToe newGame = new GameTicTacToe();

		double[] results = new double[4];

		// randomly choose the first player 
		int player = (new Random().nextDouble() < 0.5) ? 1 : -1;
		results[2] = player;

		// create input for Network (all at zero)
		double[] inputs = new double[9];

		while(newGame.checkWin() == 0 && results[1] < 20){	// prevent more than 20 moves

			TTT_GUI.delay(500);

			TTT_JFrame.setPlayerTurnLabel(player, opponentMode);

			// If Network
			if(player == -1){
				// Feed network with board state:
				// Give inputs and read output
				double[] move = net.fireNet(inputs);
				int x = outputToMove(move[0]);
				int y = outputToMove(move[1]);

				// add one to "wrong move" if Network did a wrong move
				results[3] += newGame.checkAvailability(x, y) ? 0 : 1;

				newGame.makeMove(x, y, player);
			}else{
				int[] move = new int[2];

				if(opponentMode == 0){	// opponent is the User
					// mouse pressed input **************************
					while(!TTT_GUI.mouseClicked){
						System.out.print("");
					}
					TTT_GUI.mouseClicked = false;
					move = TTT_GUI.getBoardPressedCoord();

				}else if(opponentMode == 1){	// opponent is the Computer
					// difficulty: (use 10 to max)
					int difficulty = 4;
					move = newGame.getBestPosition(difficulty, player);
				}

				newGame.makeMove(move[0], move[1], player);
			}
			// change player
			player *= -1;

			// Add one more to the number of turns
			results[1] ++;

			TTT_JFrame.setGameState(newGame.getGameState());

			double fitness = getFitness(results);
			TTT_JFrame.setFitnessText(fitness);
			net.setNetFitness(fitness);

			// update Network
			inputs = new double[9];
			for(int i = 0; i < 3; i++){
				for(int j = 0; j < 3; j++){
					inputs[i*3 + j] = newGame.getCellState(i, j);
				}
			}
			net.fireNet(inputs);

		}

		// End game result:
		switch (newGame.checkWin()) {
		case 2:
			results[0] = 0;
			break;
		case 1:
			results[0] = -1;
			break;
		case -1:
			results[0] = 1;
			break;
		}

		double fitness = getFitness(results);
		net.setNetFitness(fitness);

		TTT_JFrame.setFitnessText(fitness);

		TTT_GUI.finished = true;

	}

	private void simulateWithoutGraphics(int num_games){

		float fitness = 0;

		for(int game = 0; game < num_games; game++){

			GameTicTacToe newGame = new GameTicTacToe();

			double[] results = new double[4];

			// randomly choose the first player 
			int player = (new Random().nextDouble() < 0.5) ? 1 : -1;
			results[2] = player;

			// create input for Network (all at zero)
			double[] inputs = new double[9];

			while(newGame.checkWin() == 0){

				// If Network
				if(player == -1){
					// Feed network with board state:
					// Give inputs and read output
					double[] move = net.fireNet(inputs);
					int x = outputToMove(move[0]);
					int y = outputToMove(move[1]);

					// add one to "wrong move" if Network did a wrong move
					results[3] += newGame.checkAvailability(x, y) ? 0 : 1;

					newGame.makeMove(x, y, player);
				}else{
					// opponent is always the Computer in Without Graphics mode
					// difficulty: (use 10 to max)
					int difficulty = 4;
					int[] move = newGame.getBestPosition(difficulty, player);

					newGame.makeMove(move[0], move[1], player);
				}
				// change player
				player *= -1;

				// Add one more to the number of turns
				results[1] ++;

				// update Network
				inputs = new double[9];
				for(int i = 0; i < 3; i++){
					for(int j = 0; j < 3; j++){
						inputs[i*3 + j] = newGame.getCellState(i, j);
					}
				}
				net.fireNet(inputs);


			}

			// End game result:
			switch (newGame.checkWin()) {
			case 2:
				results[0] = 0;
				break;
			case 1:	// network lost
				results[0] = -1;
				break;
			case -1:	// network won
				results[0] = 1;
				break;
			}

			double single_fitness = getFitness(results);

			fitness += single_fitness;
		}

		fitness /= num_games;	// average over the number of games
		net.setNetFitness(fitness);

	}

	public void setOpponentMode(int opMode){
		opponentMode = opMode;
	}

	// Transforms the tanh function counter-domain into board coordinates (0,1,2) = (left, middle, right)
	private int outputToMove(double value){
		double thr = 0.5;

		if(value < -thr){
			return 0;
		}else if( -thr <= value && value < thr){
			return 1;
		}else{
			return 2;
		}
	}


	public boolean validMode(int coord[]){
		if(coord[0] >= 0 && coord[0] <= 2 && coord[1] >= 0 && coord[1] <= 2){
			return true;
		}else{
			return false;
		}
	}


	public class GameTicTacToe {

		private int boardSize = 3;
		private int[][] gameState = new int[boardSize][boardSize];

		public GameTicTacToe(){
			for(int i = 0; i < boardSize; i++){
				for(int j = 0; j < boardSize; j++){
					gameState[i][j] = 0;
				}
			}
		}

		public int getCellState(int x, int y){
			return gameState[x][y];
		}

		public int[][] getGameState(){
			return gameState;
		}

		public int checkWin(){

			for(int player = -1; player<=1; player+=2){
				boolean win = false;
				for(int i = 0; i < boardSize; i++){
					boolean win_row = true;
					boolean win_col = true;
					for(int j = 0; j < boardSize; j++){
						win_row &= (gameState[i][j] == player);
						win_col &= (gameState[j][i] == player);
					}
					win |= (win_row || win_col);
					if(win)return player;
				}

				boolean win_diag1 = true;
				boolean win_diag2 = true;
				for(int i = 0; i < boardSize; i++){
					win_diag1 &= (gameState[i][i] == player);
					win_diag2 &= (gameState[i][boardSize - i - 1] == player);			
				}
				win |= (win_diag1 || win_diag2);
				if(win)return player;
			}

			boolean full = true;
			for(int i = 0; i < boardSize; i++){
				for(int j = 0; j < boardSize; j++){
					full &= gameState[i][j] != 0;
				}
			}
			if(full) return 2;

			return 0;
		}

		public void makeMove(int x, int y, int player){
			gameState[x][y] = player;
		}

		public boolean checkAvailability(int x, int y){
			return gameState[x][y] == 0;
		}

		public void printGame(){

			for(int i = 0; i < boardSize; i++){
				for(int j = 0; j < boardSize; j++){
					System.out.print("|");
					if(gameState[i][j] == 1){
						System.out.print("X");
					}else if(gameState[i][j] == -1){
						System.out.print("O");
					}else{
						System.out.print(" ");
					}

					if(j == boardSize - 1){
						System.out.println("|");
					}

				}

				if(i != boardSize - 1)
					System.out.println("-------");
			}


		}

		public void copyBoard(GameTicTacToe g){
			for(int i = 0; i < boardSize; i++)
				for(int j = 0; j < boardSize; j++)
					this.gameState[i][j] = g.gameState[i][j];			
		}

		public int miniMax(int depth, GameTicTacToe gameBoard, int player){

			int checkWin = gameBoard.checkWin();
			if(depth == 0 || checkWin != 0){
				if(checkWin == 2)checkWin = 0;
				return checkWin * depth;
			}

			if(player == 1){
				// player = 1
				int maxValue = -10;
				for(int i = 0; i < boardSize; i++){
					for(int j = 0; j < boardSize; j++){
						if(gameBoard.checkAvailability(i, j)){
							GameTicTacToe newGameBoard = new GameTicTacToe();
							newGameBoard.copyBoard(gameBoard);
							newGameBoard.makeMove(i, j, player);

							maxValue = Math.max(maxValue, miniMax(depth - 1, newGameBoard, -player));
						}
					}
				}

				return maxValue;

			}else{
				// player = -1
				int minValue = 10;
				for(int i = 0; i < boardSize; i++){
					for(int j = 0; j < boardSize; j++){
						if(gameBoard.checkAvailability(i, j)){
							GameTicTacToe newGameBoard = new GameTicTacToe();
							newGameBoard.copyBoard(gameBoard);
							newGameBoard.makeMove(i, j, player);

							minValue = Math.min(minValue, miniMax(depth - 1, newGameBoard, -player));
						}
					}
				}

				return minValue;
			}
		}

		public int[] getBestPosition(int depth, int player){
			List<int[]> ans = new ArrayList<int[]>();
			int maxValue = -10;

			for(int i = 0; i < boardSize; i++){
				for(int j = 0; j < boardSize; j++){
					GameTicTacToe auxGame = new GameTicTacToe();
					auxGame.copyBoard(this);

					if(auxGame.checkAvailability(i, j)){
						auxGame.makeMove(i, j, player);

						int v = miniMax(depth - 1, auxGame, -player);

						if(maxValue < v){
							ans = new ArrayList<int[]>();
							maxValue = v;
						}
						if(maxValue == v){
							ans.add(new int[]{i,j});
						}

					}
				}
			}

			for(int i = 0; i < ans.size(); i++){
				//System.out.print(ans.get(i)[0]+","+ans.get(i)[1]+" | ");
			}
			//System.out.println();

			int idx = new Random().nextInt(ans.size());
			return ans.get(idx);
		}

	}


	public class RunSimulationOnBackground extends SwingWorker<Integer, String> {

		private Simulation simulation;
		private Network network;
		private boolean showGraphics;

		public RunSimulationOnBackground(TicTacToe_Sim sim, Network net, boolean showGraphics){
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
