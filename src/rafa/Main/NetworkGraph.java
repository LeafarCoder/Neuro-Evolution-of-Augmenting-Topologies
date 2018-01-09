package rafa.Main;
import java.util.*;

import processing.core.PApplet;
import processing.core.PSurface;
import rafa.NEAT.*;

public class NetworkGraph extends PApplet{
	
	// Variables declaration
	private Population population;
	private int[][] xCoord;
	private int[][] yCoord;

	private int vert;	// vertical size of area to display network
	private int hor;	// horizontal size of area to display network
	
	private int lockedNodeID;
	private int lockedNetID;
	
	private int fromBoarder = 50;
	
	// Prevents close of PApplet to finish all processes
	static final void removeExitEvent(final PSurface surf) {
		final java.awt.Window win= ((processing.awt.PSurfaceAWT.SmoothCanvas) surf.getNative()).getFrame();
		for (final java.awt.event.WindowListener evt : win.getWindowListeners())win.removeWindowListener(evt);
	}
	
	public void settings(){
		size(900,600);
		// fullScreen();
		// frame.setTitle("3 em linha");
	}
	public void setup(){
		//removeExitEvent(getSurface());
		
		setupCoordinates(population.getNetworkByID(0), fromBoarder, fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);
		
		setupCoordinates(population.getNetworkByID(1), fromBoarder, height/2 + fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);

		if(population.getPopSize() > 2){
			setupCoordinates(population.getNetworkByID(2), width/2 + fromBoarder, fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);
		}
		
		if(population.getPopSize() > 3){
			setupCoordinates(population.getNetworkByID(3), width/2 + fromBoarder, fromBoarder + height/2, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);
		}
	}
	
	public void draw(){
		background(255);

		line(0, height/2, width, height/2);
		line(width/2, 0, width/2, height);
		
		draw_network(population.getNetworkByID(0), fromBoarder, fromBoarder);
		draw_network(population.getNetworkByID(1), fromBoarder, height/2 + fromBoarder);
		
		if(population.getPopSize() > 2){
			draw_network(population.getNetworkByID(2), width/2 + fromBoarder, fromBoarder);
		}
		
		if(population.getPopSize() > 3){
			draw_network(population.getNetworkByID(3), width/2 + fromBoarder, height/2 + fromBoarder);
		}
	}
	
	public void setPopulation(Population pop){
		population = pop;
		xCoord = new int[100][500];
		yCoord = new int[100][500];
	}
	
	private void setupCoordinates(Network net, int x, int y, int hor, int vert){
		this.hor = hor;
		this.vert = vert;
		
		int maxLayerSize = 0;
		for(int i = 0; i <= net.getNumLayers(); i++){
			maxLayerSize = Math.max(maxLayerSize, net.getNumNodesInLayer(i));
		}
		
		int hor_space = hor / (net.getNumLayers() - 1);
		
		int vert_space;
		
		// for each layer
		for(int i = 0; i < net.getNumLayers(); i++) {
			float rand = Math.min(0.5f, 0.05f + new Random().nextFloat());

			if(net.getNumNodesInLayer(i) == 1)
				vert_space = -1;
			else
				vert_space = (int)(vert * (1f - rand) / (float)(net.getNumNodesInLayer(i) - 1));
			
			int cnt = 0;
			
			
			
			// for each node (j) in layer (i)
			for(int j = 0; j < net.getNodeCount(); j++){

				if(net.getNodeByID(j).getLayer() != i)continue;
				
				// store coordinates
				xCoord[net.getNetID()][j] = x + i*hor_space;
				yCoord[net.getNetID()][j] = 
						(vert_space == -1) ? 
						(int)(y + (0.25f + new Random().nextFloat()/2)*vert) : 	// from 0.25 to 0.75 of the total vertical length
						(int)(y + vert*(0.5*rand) + cnt*vert_space);
			
				cnt++;
			}
		}
		
	}
	
	private void draw_network(Network net, int x, int y){

		
		textAlign(CENTER);
		textSize(12);
		
		text("Network: "+net.getNetID()+ " ("+net.getNetFitness()+")", x + 100, y - fromBoarder/2);
	
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
					
			// add arrows to substitute flow visualization
			/*
			if(net.getNodeByID(inID).getLayer() <= net.getNodeByID(outID).getLayer()){
			 	strokeWeight(1);
			}else{
				strokeWeight(3);
			}
			*/
			
			int x1 = xCoord[net.getNetID()][inID];
			int x2 = xCoord[net.getNetID()][outID];
			int y1 = yCoord[net.getNetID()][inID];
			int y2 = yCoord[net.getNetID()][outID];
			
			// line has weight depending on the weight of the node
			strokeWeight(((float)Math.tanh(net.getGeneByID(i).getWeight()/2) + 1.5f)*3);
			
			line(x1, y1, x2, y2);
					
			// text(net.getGeneByID(i).getInnovation(), (x1 + x2)/2, (y1 + y2)/2);
			
			fill(0,0,255);
			textSize(20);
			text(net.getGeneByID(i).getWeight(), (x1 + x2)/2, (y1 + y2)/2);
		}
		
		textSize(12);
		strokeWeight(1);
		stroke(0);
		
		int maxLayerSize = 0;
		for(int i = 0; i < net.getNumLayers(); i++){
			maxLayerSize = Math.max(maxLayerSize, net.getNumNodesInLayer(i));
		}
		int nodeSize = (int)Math.max(25, (0.5 * vert / Math.max(net.getNumLayers(), maxLayerSize)));

		// for each layer (1 to getNumLayers)
		for(int i = 0; i < net.getNumLayers(); i++) {

			// for each node (j) in layer (i)
			for(int j = 0; j < net.getNodeCount(); j++){
				
				if(net.getNodeByID(j).getLayer() != i)continue;

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

				ellipse(xCoord[net.getNetID()][j], yCoord[net.getNetID()][j], nodeSize, nodeSize);

				// node's ID
				fill(0);
				text(j, xCoord[net.getNetID()][j], yCoord[net.getNetID()][j] + 10);
				text(net.getNodeByID(j).getLayer(), xCoord[net.getNetID()][j], yCoord[net.getNetID()][j]);

			}
			
		}

		
		
		for(int i = 0; i < net.getNumLayers(); i++){
			text(net.getNumNodesInLayer(i), x + i * hor / (net.getNumLayers() - 1), y + vert + fromBoarder/2);
		}
		
		// add text
		// fill(0);
		// text("Bias", xCoord[net.getNetID()][net.getNumberOfInputNodes() - 1], yCoord[net.getNetID()][net.getNumberOfInputNodes() - 1]);

	}
	
	
	public void mousePressed(){
		if(mouseButton == LEFT){
			
			if(mouseX < width/2){
				if(mouseY < height/2){
					lockedNetID  = 0;
				}else{
					lockedNetID = 1;
				}
			}else{
				if(mouseY < height/2){
					lockedNetID  = 2;
				}else{
					lockedNetID = 3;
				}
			}
			
			lockedNodeID = -1;
			
			int nodeX;
			int nodeY;
			
			int maxLayerSize = 0;
			for(int i = 0; i < population.getNetworkByID(lockedNetID).getNumLayers(); i++){
				maxLayerSize = Math.max(maxLayerSize, population.getNetworkByID(lockedNetID).getNumNodesInLayer(i));
			}
			int nodeSize = (int)Math.max(25, (0.7 * vert / Math.max(population.getNetworkByID(lockedNetID).getNumLayers(), maxLayerSize)));
			
			for (int i = 0; i < population.getNetworkByID(lockedNetID).getNodeCount(); i++) {
				nodeX = xCoord[lockedNetID][i];
				nodeY = yCoord[lockedNetID][i];
				
				if(dist(mouseX, mouseY, nodeX, nodeY) < nodeSize/2){
					// System.out.println("   -> In node "+i+" of network "+lockedNetID);
					
					lockedNodeID = i;
				}
			}
		}else if(mouseButton == RIGHT){
			if(mouseX < width/2){
				if(mouseY < height/2){
					population.getNetworkByID(0).mutate(5, 3, 50);
					setupCoordinates(population.getNetworkByID(0), fromBoarder, fromBoarder, hor, vert);
					
				}else{
					population.getNetworkByID(1).mutate(70, 60, 50);
					setupCoordinates(population.getNetworkByID(1), fromBoarder, height/2 + fromBoarder, hor, vert);
				}
			}else{
				if(mouseY < height/2){
					if(population.getPopSize() > 2){
						population.getNetworkByID(2).mutate(0, 100, 50);
						setupCoordinates(population.getNetworkByID(2), width/2 + fromBoarder, fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);					
					}
				}else{
					if(population.getPopSize() > 3){
						population.getNetworkByID(3).mutate(0, 100, 50);
						setupCoordinates(population.getNetworkByID(3), width/2 + fromBoarder, height/2 + fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);
					}
				}
			}
			
		}else if(mouseButton == CENTER){
			
			if(population.getPopSize() <= 3)
			population.addNetwork(Network.crossoverNetworks(population.getNetworkByID(0), population.getNetworkByID(1)));
			
			if(population.getPopSize() > 2)
				setupCoordinates(population.getNetworkByID(2), width/2 + fromBoarder, fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);
			
			if(population.getPopSize() > 3)
				setupCoordinates(population.getNetworkByID(3), width/2 + fromBoarder, height/2 + fromBoarder, width/2 - 2*fromBoarder, height/2 - 2*fromBoarder);
		}
	}
	
	public void mouseDragged(){
		if(mouseButton == LEFT){
			
			if(lockedNodeID != -1){
				xCoord[lockedNetID][lockedNodeID] = mouseX;
				yCoord[lockedNetID][lockedNodeID] = mouseY;
			}
		}
	}
	
	@ Override
	public void exit() {
	  }

}