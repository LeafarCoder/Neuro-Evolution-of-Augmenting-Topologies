package rafa.GUI;
import java.util.*;

import processing.core.*;
import rafa.NEAT.*;
import rafa.utilities.Pair;

public class NetworkGraph extends PApplet{

	private static final long serialVersionUID = 1L;
	
	// Variables declaration
	private Population population;
	private int[][] xCoord;
	private int[][] yCoord;
	private double[][] inputs;
	private int inputSize;
	private ArrayList<Pair<Integer,Integer> > netIDs;
	private Hashtable<Integer, Integer> hashIDs;

	private int vert;	// vertical size of area to display one network
	private int hor;	// horizontal size of area to display one network
	private int grid_size_x;	// how many by how many cells
	private int grid_size_y;

	private int lockedNodeID;
	private int lockedNetID;
	private int lockedBoxID;

	private int fromBoarder = 30;
	private int nodeRadius = 30;

	private int auxWidth = 600;
	private int auxHeight = 600;
	
	
	public void setup(){
		size(auxWidth, auxHeight);

		int popSize = population.getPopSize();
		inputSize = population.getNumberOfInputNodes() + 1;

		inputs = new double[popSize][inputSize];
		for(int i = 0; i < popSize; i++){
			for(int j = 0; j < inputSize - 1; j++){
				// then try just 0 and 1 (or -1)
				inputs[i][j] = new Random().nextFloat();
			}
			// bias node
			inputs[i][inputSize-1] = 1;
		}

		calculateGridSize();

		vert = height/grid_size_y;
		hor = width/grid_size_x;

		for(int i = 0; i < netIDs.size(); i++){
			int id = netIDs.get(i).getFirst();

			int x1 = (i % grid_size_x)*width/grid_size_x;
			int y1 = floor(i/grid_size_x)*height/grid_size_y;

			setupCoordinates(population.getNetworkByID(id), x1, y1);
		}

	}


	public void draw(){
		if(frameCount == 1)resizeWindow(auxWidth, auxHeight);
		
		background(255);

		// draw lines to separate networks
		for(int i = 1; i < grid_size_y; i++) 
			line(0, i*height/grid_size_y, width, i*height/grid_size_y);
		for(int i = 1; i < grid_size_x; i++)
			line(i*width/grid_size_x, 0, i*width/grid_size_x, height);

		// store coordinates for networks' nodes
		for(int i = 0; i < netIDs.size(); i++){
			int id = netIDs.get(i).getFirst();
			int boxID = netIDs.get(i).getSecond();

			/*
			// update inputs (slightly)
			for(int j = 0; j < (inputSize - 1); j++){
			// then try just 0 and 1 (or -1)
				inputs[i][j] = map(noise((float)i*100, (float)time/200 +200*j), 0, 1, -1, 1);
			}
			//out.println(population.getNetworkByID(id).getNodeByID(inputSize-1).getNodeType());
			inputs[i][inputSize-1] = 1;

			population.getNetworkByID(id).feedInput(inputs[i]);
			 */
			int x1 = (boxID % grid_size_x)*width/grid_size_x;
			int y1 = floor(boxID/grid_size_x)*height/grid_size_y;

			drawNetwork(population.getNetworkByID(id), x1, y1);
		}
	}

	public void setPopulation(Population pop){
		netIDs = new ArrayList<Pair<Integer,Integer>>();
		hashIDs = new Hashtable<>();
		
		Enumeration<Integer> e = pop.getNetworkIDs();
		List<Integer> auxList = Collections.list(e);
		Collections.sort(auxList);

		for(int i = 0; i < auxList.size(); i++){
			netIDs.add(new Pair<Integer, Integer>(auxList.get(i), i));
			hashIDs.put(auxList.get(i), i);
		}
		
		
		
		population = pop;

		int maxPopSize = 100;
		int maxNodeSize = 1000;
		xCoord = new int[maxPopSize][maxNodeSize];
		yCoord = new int[maxPopSize][maxNodeSize];
	}

	private void setupCoordinates(Network net, int x, int y){
		
		int netIndex = hashIDs.get(net.getNetID());
				
		int maxLayerSize = 0;
		for(int i = 0; i <= net.getNumLayers(); i++){
			maxLayerSize = Math.max(maxLayerSize, net.getNumNodesInLayer(i));
		}

		int hor_space = (hor - 2*fromBoarder) / (net.getNumLayers() - 1);

		int vert_space;

		// for each layer
		for(int i = 0; i < net.getNumLayers(); i++) {
			double rand = Math.min(0.3f, 0.05f + new Random().nextFloat());

			// if single node in layer make special case
			if(net.getNumNodesInLayer(i) == 1)
				vert_space = -1;
			else
				vert_space = (int)((vert - 2*fromBoarder) * (1f - rand) / (double)(net.getNumNodesInLayer(i) - 1));

			int cnt = 0;

			// for each node (j) in layer (i)
			for(int j = 0; j < net.getNodeCount(); j++){
				// only for nodes at layer (i)
				if(net.getNodeByID(j).getLayer() != i)continue;

				// store coordinates
				xCoord[netIndex][j] = fromBoarder + x + i*hor_space;
				yCoord[netIndex][j] = 
						fromBoarder +
						((vert_space == -1) ? 
								(int)(y + (0.25f + new Random().nextFloat()/2)*vert) : 	// from 0.25 to 0.75 of the total vertical length
									(int)(y + vert*(0.5*rand) + cnt*vert_space));

				cnt++;
			}
		}

	}

	private void drawNetwork(Network net, int x, int y){

		// background depending on the species:
		/*
		colorMode(HSB,100);
		int hue = (net.getSpecie() * 5) % 100;
		int d = (net.getSpecie() * 2) % 30;
		fill(color(hue,100,50 + d));
		rect(x,y,hor,vert);
		 */

		int netIndex = hashIDs.get(net.getNetID());
		
		colorMode(RGB,255);
		fill(0);

		textAlign(CENTER);
		textSize(12);

		text("Network: "+net.getNetID()+ " ("+net.getNetFitness()+")", x + 100, y + fromBoarder/2);

		// draw genes
		for(int i = 0; i < net.getGeneCount(); i++){

			// only draw active genes
			if(!net.getGeneByID(i).getGeneState())continue;

			if(!net.getGeneByID(i).getGeneState()){
				stroke(0,255,0);
			}else{
				stroke(0);
			}

			int inID = net.getGeneByID(i).getInID();
			int outID = net.getGeneByID(i).getOutID();

			// line has weight depending on the weight of the node
			strokeWeight(((float)Math.abs(Math.tanh(net.getGeneByID(i).getWeight()/1)) + 0.6f)*4);

			noFill();

			if(net.getGeneByID(i).getWeight() < 0){
				stroke(255,0,0);
			}else{
				stroke(0,255,0);
			}

			
			int x1 = xCoord[netIndex][inID];
			int y1 = yCoord[netIndex][inID];
			int x2 = xCoord[netIndex][outID];
			int y2 = yCoord[netIndex][outID];
			// control points
			float x3 = (x1 + x2)/2;
			float y3 = y1;
			float x4 = (x1 + x2)/2;
			float y4 = y2;
			bezier(x1,y1,x3,y3,x4,y4,x2,y2);

			// text(net.getGeneByID(i).getInnovation(), (x1 + x2)/2, (y1 + y2)/2);

			//fill(0,0,255);
			textSize(10);
			text((float)net.getGeneByID(i).getWeight(), (x1 + x2)/2, (y1 + y2)/2);
			// -> text(net.getGeneByID(i).getID(), (x1 + x2)/2, (y1 + y2)/2 - 10);
			//text(net.getGeneByID(i).getInnovation(), (x1 + x2)/2, (y1 + y2)/2);
		}

		textSize(12);
		strokeWeight(1);
		stroke(0);

		int maxLayerSize = 0;
		for(int i = 0; i < net.getNumLayers(); i++){
			maxLayerSize = Math.max(maxLayerSize, net.getNumNodesInLayer(i));
		}

		//int nodeSize = (int)Math.max(25, (0.5 * vert / Math.max(net.getNumLayers(), maxLayerSize)));

		// Draw nodes

		// for each layer (1 to getNumLayers)
		for(int i = 0; i < net.getNumLayers(); i++) {

			// for each node (j) in layer (i)
			for(int j = 0; j < net.getNodeCount(); j++){

				Node node = net.getNodeByID(j);
				if(node.getLayer() != i)continue;


				fill(0);
				rect(xCoord[netIndex][j]-nodeRadius/2, yCoord[netIndex][j]-nodeRadius/2, nodeRadius, nodeRadius);


				// set colors
				switch (net.getNodeByID(j).getNodeType()){
				case Node.INPUT_NODE:
					fill(0,255,0);
					break;
				case Node.INPUT_BIAS_NODE:
					fill(255,255,0);
					break;
				case Node.OUTPUT_NODE:
					fill(255,0,0);
					break;
				case Node.HIDDEN_NODE:
					fill(0,150,255);
					break;
				}

				//ellipse(xCoord[netIndex][j], yCoord[netIndex][j], nodeRadius, nodeRadius);
				int nodeSignalPx = (int)Math.ceil((nodeRadius * map((float)node.getNodeValue(), -1, 1, 0, 1)));

				rect(xCoord[netIndex][j]-nodeRadius/2, yCoord[netIndex][j]+nodeRadius/2, nodeRadius, -nodeSignalPx);

				// node's ID
				fill(0);
				text(node.getNodeID(), xCoord[netIndex][j], yCoord[netIndex][j] - 15);
				// fill(255);
				// text(node.getLayer(), xCoord[netIndex][j], yCoord[netIndex][j] + 10);
			}

		}

		for(int i = 0; i < net.getNumLayers(); i++){
			text(
					net.getNumNodesInLayer(i),
					x + i * (hor - 2*fromBoarder) / (net.getNumLayers() - 1) + fromBoarder,
					y + vert - fromBoarder/2
					);
		}

		// add text
		// fill(0);
		// text("Bias", xCoord[netIndex][net.getNumberOfInputNodes() - 1], yCoord[netIndex][net.getNumberOfInputNodes() - 1]);

	}


	public void mousePressed(){
		// determine the network the mouse is selecting
		int index = floor(mouseX/hor) + (width/hor) * floor(mouseY/vert);
		if(index >= netIDs.size())return;

		lockedNetID = netIDs.get(index).getFirst();
		lockedBoxID = netIDs.get(index).getSecond();

		// upper-left corner coordinates of the network's box 
		int x1 = (lockedBoxID % grid_size_x)*width/grid_size_x;
		int y1 = floor(lockedBoxID/grid_size_x)*height/grid_size_y;

		if(mouseButton == LEFT){
			/*
			 for(int i = 0; i < population.getNetworkByID(lockedNetID).getNumLayers(); i++){
				maxLayerSize = Math.max(maxLayerSize, population.getNetworkByID(lockedNetID).getNumNodesInLayer(i));
			}
			 int nodeSize = (int)Math.max(25, (0.7 * vert / Math.max(population.getNetworkByID(lockedNetID).getNumLayers(), maxLayerSize)));
			 */
			for (int i = 0; i < population.getNetworkByID(lockedNetID).getNodeCount(); i++){
				int nodeX = xCoord[lockedBoxID][i];
				int nodeY = yCoord[lockedBoxID][i];

				if(dist(mouseX, mouseY, nodeX, nodeY) < nodeRadius/2){
					lockedNodeID = i;
					break;
				}
			}

		}else if(mouseButton == RIGHT){
			for(int i = 0; i < 1; i++){
				population.getNetworkByID(lockedNetID).mutate(0.2f, 0.3f, 0.5f);
				setupCoordinates(population.getNetworkByID(lockedNetID), x1, y1);
			}

			//population.getNetworkByID(lockedNetID).seeTest();

		}else if(mouseButton == CENTER){

		}
	}

	public void mouseDragged(){
		if(mouseButton == LEFT){

			if(lockedNodeID != -1){
				int x1 = (lockedBoxID % grid_size_x)*width/grid_size_x;
				int y1 = floor(lockedBoxID/grid_size_x)*height/grid_size_y;
				xCoord[lockedBoxID][lockedNodeID] = min(max(x1,mouseX),x1 + hor);
				yCoord[lockedBoxID][lockedNodeID] = min(max(y1,mouseY),y1 + vert);
			}
		}
	}


	public void mouseReleased() {
		lockedNetID = -1;
		lockedNodeID = -1;
	}

	@ Override
	public void exit() {
	}

	public void resizeWindow(int width_, int height_){
		
		setSize(width_, height_);
		
		calculateGridSize();
		
		vert = height_/grid_size_y;
		hor = width_/grid_size_x;

		for(int i = 0; i < netIDs.size(); i++){
			int id = netIDs.get(i).getFirst();
			int boxID = netIDs.get(i).getSecond();

			int x1 = (boxID % grid_size_x)*width_/grid_size_x;
			int y1 = floor(boxID/grid_size_x)*height_/grid_size_y;

			setupCoordinates(population.getNetworkByID(id), x1, y1);
		}
	}
	
	private void calculateGridSize(){
		
		int popSize = population.getPopSize();
		int grid_size = ceil(sqrt(popSize));
		grid_size_x = grid_size;
		grid_size_y = grid_size;

		// try to get the best grid ratio
		int remain = popSize - grid_size*grid_size;
		int max_t_x = grid_size_x;
		int max_t_y = grid_size_y;
		for(int i = 1; (grid_size_y - i) > 0 && i <= 3; i++){

			int t_x = (popSize/(grid_size_y-i)) + 1;
			if(abs((grid_size_y-i) - t_x) > 3)continue;

			if(popSize - (grid_size_y - i)*t_x > remain){
				max_t_y = grid_size_y - i;
				max_t_x = t_x;
				remain = popSize - (grid_size_y-i)*t_x;
			}
		}
		grid_size_x = max_t_x;
		grid_size_y = max_t_y;
		
		if(grid_size_y == 1)grid_size_x = popSize;
	}
	
	public void setAuxSize(int w, int h){
		auxWidth = w;
		auxHeight = h;
	}

}