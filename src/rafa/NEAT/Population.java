package rafa.NEAT;

import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import org.json.simple.*;
import org.json.simple.parser.*;

import rafa.GUI.NEATWindow;
import rafa.Main.Simulations.Simulation;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim;


public class Population implements Serializable{

	private int generation = 1;
	private Species species;
	private Hashtable<Integer, Network> population;

	private int numInputNodes = 0;
	private int numOutputNodes = 0;

	private int lastNetID = 0;

	// ************************************* PARAMETERS ****************************************

	// All
	ArrayList<Parameters> popParameters;

	// General
	static int popInitialSize;

	// Mutation
	static double mutateNodeAddProbability = 0.03;
	static double mutateGeneAddProbability = 0.05;
	static double mutateWeightProbability = 0.8;
	static double mutationWeightPower;
	static double uniformWeightMutationProb;

	// Crossover
	static double crossoverToggleInheritedGeneStateProb;

	// Speciation
	static double speciationSpeciesSize;
	static double speciationSurvivalThreshold;
	static double speciationDisjointCoefficient = 2;
	static double speciationExcessCoefficient = 2;
	static double speciationWeightDifferenceCoefficient = 1;
	static double speciationCompatibilityThreshold;
	static double speciationCompatibilityModifier;

	// ********************************** PARAMETERS (end) *************************************


	// create an empty population
	public Population(){
		population = new Hashtable<Integer, Network>();
		species = new Species(this);
	}

	// create population with N networks from scratch without Parameters
	public Population(int initialSize, int numberOfInputs, int numberOfOutputs){
		population = new Hashtable<Integer, Network>();
		species = new Species(this);

		// add N new random networks
		for(int i = 0; i < initialSize; i++){
			addNetwork(new Network(lastNetID++, numberOfInputs, numberOfOutputs));
		}

	}

	// create population with N networks from scratch
	public Population(int initialSize, int numberOfInputs, int numberOfOutputs, ArrayList<Parameters> popParam){
		population = new Hashtable<Integer, Network>();
		setParameters(popParam);
		species = new Species(this);

		// add N new random networks
		for(int i = 0; i < initialSize; i++){
			addNetwork(new Network(lastNetID++, numberOfInputs, numberOfOutputs));
		}

	}

	public Network getNetworkByID(int ID){
		if(!population.containsKey(ID)){
			System.err.println("rafa::Population::getNetworkByID: No Network with ID " + ID + " exists in the population!");
			return null;
		}
		return population.get(ID);
	}

	public Enumeration<Integer> getNetworkIDs(){
		return population.keys();
	}

	private Network[] getNetworks(){
		Network[] networks = new Network[population.size()];

		Enumeration<Network> enumeration = population.elements();
		for(int i = 0; i < networks.length; i++){
			networks[i] = enumeration.nextElement();
		}

		return networks;
	}

	public int getPopSize(){
		return population.size();
	}

	public int getLastNetID(){
		return lastNetID;
	}

	public int getGeneration(){
		return generation;
	}

	public double getMaxFitness(){
		double maxFitness = 0;

		Enumeration<Integer> k = population.keys();
		while(k.hasMoreElements()){
			Integer id = k.nextElement();
			maxFitness = Math.max(maxFitness, getNetworkByID(id).getNetFitness());
		}

		return maxFitness;
	}

	public int getNumberOfSpecies(){
		return species.getNumOfSpecies();
	}

	public Species getSpecies(){
		return species;
	}

	public int getNumberOfInputNodes(){
		return numInputNodes;
	}

	public int getNumberOfOutputNodes(){
		return numOutputNodes;
	}

	public void setParameters(ArrayList<Parameters> popParam){
		popParameters = popParam;

		for(int i = 0; i < popParameters.size(); i++){

			switch (popParameters.get(i).getName()) {

			// ********************** general **********************
			case "popInitialSize":
				Population.popInitialSize = (int) popParameters.get(i).getValue();
				break;

				// ********************** mutate **********************
			case "mutateNodeAddProbability":
				Population.mutateNodeAddProbability = popParameters.get(i).getValue();
				break;

			case "mutateGeneAddProbability":
				Population.mutateGeneAddProbability = popParameters.get(i).getValue();
				break;

			case "mutateWeightProbability":
				Population.mutateWeightProbability = popParameters.get(i).getValue();
				break;

			case "mutationWeightPower":
				Population.mutationWeightPower = popParameters.get(i).getValue();
				break;

			case "uniformWeightMutationProb":
				Population.uniformWeightMutationProb = popParameters.get(i).getValue();
				break;

				// ********************** crossover **********************
			case "crossoverToggleInheritedGeneStateProb":
				Population.crossoverToggleInheritedGeneStateProb = popParameters.get(i).getValue();
				break;

				// ********************** speciation **********************
			case "speciationSpeciesSize":
				Population.speciationSpeciesSize = popParameters.get(i).getValue();
				break;

			case "speciationSurvivalThreshold":
				Population.speciationSurvivalThreshold = popParameters.get(i).getValue();
				break;

			case "speciationDisjointCoefficient":
				Population.speciationDisjointCoefficient = popParameters.get(i).getValue();
				break;

			case "speciationExcessCoefficient":
				Population.speciationExcessCoefficient = popParameters.get(i).getValue();
				break;

			case "speciationWeightDifferenceCoefficient":
				Population.speciationWeightDifferenceCoefficient = popParameters.get(i).getValue();
				break;

			case "speciationCompatibilityThreshold":
				Population.speciationCompatibilityThreshold = popParameters.get(i).getValue();
				break;

			case "speciationCompatibilityModifier":
				Population.speciationCompatibilityModifier = popParameters.get(i).getValue();
				break;
			}

		}

	}

	public void addNetwork(Network net){

		// separate networks by species
		if(net.hasSpecie()){
			species.addToSpecie(net.getSpecie(), net);
		}else{
			// if this is the first network added to the population initialize species
			if(getPopSize() == 0){
				species.addSpecie(net);
			}else{
				// for every species currently existing
				Enumeration<Integer> k = species.getSpeciesIDs();
				while (k.hasMoreElements()){
					// if the topologies are close enough then the net belongs to this species
					Integer specie_id =  k.nextElement();
					if(Species.sameSpecies(net, species.getSpecieRepresentative(specie_id))){
						species.addToSpecie(specie_id, net);
						break;
					}
				}	
			}

			// if after searching over all species we couldn't find a match then add a new species
			if(!net.hasSpecie()){
				species.addSpecie(net);
				net.setSpecieID(species.getNumOfSpecies() - 1);
			}
		}

		// if first network then set #input and #output nodes
		if(getPopSize() == 0){
			numInputNodes = net.getNumberOfInputNodes();
			numOutputNodes = net.getNumberOfOutputNodes();
		}
		// add this network to population
		population.put(net.getNetID(),net);
	}

	public void removeNetwork(Network net){
		population.remove(net.getNetID());
	}

	public void mutatePopulation(){
		Enumeration<Integer> netIDs = getNetworkIDs();

		while(netIDs.hasMoreElements()){
			int netID = netIDs.nextElement();
			getNetworkByID(netID).mutate(mutateGeneAddProbability, mutateNodeAddProbability, mutateWeightProbability);
		}
	}

	public void chooseBest(){


	}

	public void savePopulationToFile(String pathName){

		FileOutputStream fos;
		try {
			File file = new File(pathName);
			fos = new FileOutputStream(file);
			ObjectOutputStream oos;
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.close();
		} catch (FileNotFoundException er) {
			er.printStackTrace();
		} catch (IOException er) {
			er.printStackTrace();
		}

	}

	public static Population loadPopulationFromFile(String filePath){

		Population pop = null;
		try {
			FileInputStream fileIn = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			pop = (Population) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}

		return pop;
	}

	public void nextGeneration(Simulation sim){

		int popSize = getPopSize();

		// ****************************** simulate ******************************
		System.out.println("\n\n------ SIMULATE ------");
		for(Network net: getNetworks()){
			// sim.simulate(net, false);	// simulate without visual graphics

			// System.out.println(net.getNetID() + " --> " + net.getNetFitness());

			try {
				sim.getClass().getConstructor(Network.class, boolean.class, Population.class).newInstance(net, false, this);
				net.setSimulationRunning(true);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}

			System.out.println(net.getNetID() + " --> " + net.getNetFitness());

		}
		
		boolean some_is_running;
		do{
			System.out.println("waiting...");
			
			some_is_running = false;
			for(Network net: getNetworks()){
				some_is_running |= net.getSimulationRunning();
			}
		} while (some_is_running);

		// ********************* delete the worst networks from each species **********************
		System.out.println("------ DELETE ------");
		species.killUnfitNetworks();

		// ****************************** order remaining by Fitness ******************************
		System.out.println("------ ORDER ------");
		ArrayList<Network> ordered_nets = new ArrayList<Network>();
		Enumeration<Network> en = population.elements();
		while(en.hasMoreElements()){
			ordered_nets.add(en.nextElement());
		}

		Collections.sort(ordered_nets, new NetworkComparatorBestFirst());
		
		for(int i = 0; i < ordered_nets.size(); i++){
			System.out.println(ordered_nets.get(i).getNetID()+ " --> " + ordered_nets.get(i).getNetFitness());
		}
		
		// ****************************** crossover ******************************
		System.out.println("------ CROSSOVER ------");
		ArrayList<Network> temp_breed = new ArrayList<Network>();
		int fathers_size = ordered_nets.size();
		int[] count = new int[fathers_size];
		Random rand = new Random();
		while(ordered_nets.size() + temp_breed.size() < popSize){
			// make it more probable to choose a better Network
			int indx1 = (int)Math.min(fathers_size - 1, Math.abs(rand.nextGaussian() * fathers_size /4));
			int indx2 = (int)Math.min(fathers_size - 1, Math.abs(rand.nextGaussian() * fathers_size /4));
			count[indx1]++;
			count[indx2]++;
			Network parent1 = ordered_nets.get(indx1);
			Network parent2 = ordered_nets.get(indx2);
			temp_breed.add(Network.crossoverNetworks(lastNetID++, parent1, parent2));
		}
		ordered_nets.addAll(temp_breed);

		// ****************************** mutate ******************************
		System.out.println("------ MUTATE ------");
		// leave the best 0 untouched

		for(int i = 0; i < ordered_nets.size(); i++){
			ordered_nets.get(i).mutate(mutateGeneAddProbability, mutateNodeAddProbability, mutateWeightProbability);
		}

		// ****************************** set population to this new population ******************************
		System.out.println("------ NEW POPULATION ------");
		population = new Hashtable<Integer, Network>();
		for(int i = 0; i < species.getNumOfSpecies(); i++){
			species.removeSpecie(i);
		}
		species.resetSpeciesControlID();
		for(int i = 0; i < ordered_nets.size(); i++){
			Network temp_net = ordered_nets.get(i);
			if(temp_net.hasSpecie())temp_net.setSpecieControlToDefault();
			addNetwork(temp_net);
		}

		// control Speciation rate
		species.adjustDeltaThreshold();

		System.out.println("------ RE-SIMULATE ------");
		for(Network net: getNetworks()){
			// sim.simulate(net, false);	// simulate without visual graphics

			try {
				sim.getClass().getConstructor(Network.class, boolean.class, Population.class).newInstance(net, false, this);
				net.setSimulationRunning(true);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		do{
			System.out.println("waiting...");
			
			some_is_running = false;
			for(Network net: getNetworks()){
				some_is_running |= net.getSimulationRunning();
			}
		} while (some_is_running);
		
		NEATWindow.updatePopView();
		System.out.println("NEAT_POP_INFO_UPDATED");
	}


	public class Parameters{
		private String name;
		private double value;
		private String description;

		public Parameters(String n, double v, String d){
			name = n;
			value = v;
			description = d;
		}

		public String getName(){
			return name;
		}

		public double getValue(){
			return value;
		}

		public String getDescription(){
			return description;
		}
	}

	public class NetworkComparatorBestFirst implements Comparator<Network>{
		// Overriding compare()method of Comparator 
		public int compare(Network net1, Network net2) {
			return (net1.getNetFitness() < net2.getNetFitness()) ? 1 : -1;
		}
	}

}
