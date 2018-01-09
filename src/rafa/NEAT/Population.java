package rafa.NEAT;

import java.util.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.json.simple.*;
import org.json.simple.parser.*;


public class Population {

	private int generation;
	private Species species = new Species();
	private List<Network> population = new ArrayList<Network>();
	
	private int mutateNodeAddProbability;
	private int mutateGeneAddProbability;
	private int mutateWeightProbability;
	
	
	// create an empty population
	public Population(){
	
		generation = 1;
		
	}
	
	
	// create population with N networks from scratch
	public Population(int n, int numberOfInputs, int numberOfOutputs, float time){
		
		generation = 1;
		
		// add N new random networks
		population = new ArrayList<Network>();
		for(int i = 0; i < n; i++){
			addNetwork(new Network(i, numberOfInputs, numberOfOutputs));
		}
		
		/*
		for(int i = 0; i < getPopSize(); i++){
			System.out.println("Network "+i+": "+getNetworkByID(i).getSpecie());
		}
		
		for(int i = 0; i < species.getNumOfSpecies(); i++){
			System.out.println("Species "+i+": "+species.getSpecieSize(i));
		}
		*/
	}

	
	public Network getNetworkByID(int ID){
		return population.get(ID);
	}
	
	public int getPopSize(){
		return population.size();
	}
	
	public int getGeneration(){
		return generation;
	}

	public float getMaxFitness(){
		
		float maxFitness = 0;
		for(int i = 0; i < getPopSize(); i++){
			maxFitness = Math.max(maxFitness, getNetworkByID(i).getNetFitness());
		}
		
		return maxFitness;
	}
	
	public int getNumberOfInputs(){
		if(getPopSize() == 0){
			return 0;
		}else{
			return getNetworkByID(0).getNumberOfInputNodes() - 1; // not counting the bias
		}
	}
	
	public int getNumberOfOutputs(){
		if(getPopSize() == 0){
			return 0;
		}else{
			return getNetworkByID(0).getNumberOfOutputNodes();
		}
	}
	
	public int getNumberOfSpecies(){
		if(getPopSize() == 0){
			return 0;
		}else{
			return species.getNumOfSpecies();
		}
	}
	
	public void setSpeciationParameters(float coefDisjoint, float coefExcess, float coefWeights, float threshold){
		species.setSpeciationParameters(coefDisjoint, coefExcess, coefWeights, threshold);
	}
	
	public void setMutationProbabilities(int mutateNodeAddProbability, int mutateGeneAddProbability, int mutateWeightProbability){
		this.mutateNodeAddProbability = mutateNodeAddProbability;
		this.mutateGeneAddProbability = mutateGeneAddProbability;
		this.mutateWeightProbability = mutateWeightProbability;
	}
	
    public void addNetwork(Network net){
    	net.setNetID(getPopSize());
    	
    	// separate networks by species
		
    	// if this is the first network added to the population initialize species
    	if(getPopSize() == 0){
    		species = new Species();
    		species.addSpecie(net);
    		net.setSpecie(0);
    	}else{
	
	    	// for every species currently existing
	    	for(int j = 0; j < species.getNumOfSpecies(); j++){
	    					
	    		// if the topologies are close enough then the net belongs to this species
	    		if(Species.sameSpecies(net, species.getSpecieRepresentative(j))){
	    			species.incrementSpecieSize(j);
	    			net.setSpecie(j);
	    			break;
	    		}
	    	}	
    	}
    	
    	// if after searching over all species we couldn't find a match then add a new species
    	if(!net.hasSpecie()){
    		species.addSpecie(net);
    		net.setSpecie(species.getNumOfSpecies() - 1);
    	}
    		
    	// add this network to population
    	population.add(net);
    }

    
    public void mutatePopulation(){
    	for (int i = 0; i < getPopSize(); i++) {
			getNetworkByID(i).mutate(mutateGeneAddProbability, mutateNodeAddProbability, mutateWeightProbability);
		}
    }
    
    public void savePopulationToFile(String pathName){
    	JSONObject pop = new JSONObject();
    	
    	JSONArray networks = new JSONArray();
    	
    	// add every network in population
    	Network netw = null;
    	for(int i = 0; i < getPopSize(); i++){
    		
    		netw = getNetworkByID(i);
			
    		
    		
    		// for every network, add its information: nodes, genes
    		
    		JSONObject net = new JSONObject();
    		JSONObject numLayers = new JSONObject();
    		
    		JSONArray nodes = new JSONArray();
    		JSONArray genes = new JSONArray();
    		
    		
    		JSONObject node = new JSONObject();
    		JSONObject gene = new JSONObject();

    		
    		// for every node
    		for(int j = 0; j < netw.getNodeCount(); j++){
    			node = new JSONObject();
    			
				node.put("ID",j);
				node.put("Layer", netw.getNodeByID(j).getLayer());
				node.put("Type", netw.getNodeByID(j).getNodeType());
				
				System.out.println(" *** ADDING A NEW NODE *** ");
				System.out.println(j+" <-> "+node.get("ID"));
				
				nodes.add(node);
			}
    		
    		// for every gene
    		for(int j = 0; j < netw.getGeneCount(); j++){
    			gene = new JSONObject();
    			
				gene.put("Innovation", netw.getGeneByID(j).getInnovation());
				gene.put("Input Node", netw.getGeneByID(j).getInID());
				gene.put("Output Node", netw.getGeneByID(j).getOutID());
				gene.put("Weight", netw.getGeneByID(j).getWeight());
				gene.put("State", netw.getGeneByID(j).getGeneState());
				
				genes.add(gene);
			}
    		
    		// for every Layer (save the number of nodes in that layer)
    		for(int j = 0; j < netw.getNumLayers(); j++){
    			numLayers.put(Integer.toString(j), netw.getNumNodesInLayer(j));
    		}

    		// save network properties
    		net.put("ID", i);
    		net.put("Number of inputs", netw.getNumberOfInputNodes());
    		net.put("Number of outputs", netw.getNumberOfOutputNodes());
    		net.put("Species", netw.getSpecie());
    		// net.put("Fitness", netw.getNetFitness());
    		net.put("Number of layers", netw.getNumLayers());
    		//net.put("Node number per layer", );
    		net.put("Genes",genes);
    		net.put("Nodes",nodes);
    		net.put("Number of nodes per layer", numLayers);
    		
    		
    		networks.add(net);
		}
    	
    	// add all the networks to population
    	pop.put("Networks", networks);
    	
    	JSONArray geneHistory = new JSONArray();
    	JSONObject geneInHistory = new JSONObject();
    	
    	// for every gene in Gene History
		for(int i = 0; i < Network.getGeneHistorySize(); i++){
			geneInHistory = new JSONObject();
			
			Gene gene = Network.getGeneInHistoryByID(i);
			
			geneInHistory.put("Innovation", gene.getInnovation());
			geneInHistory.put("Input Node", gene.getInID());
			geneInHistory.put("Output Node", gene.getOutID());
			
			geneHistory.add(geneInHistory);

		}

    	pop.put("Gene History", geneHistory);
    	
    	pop.put("Innovation number", Network.getInnovationNumber());
    	
    	try (FileWriter file = new FileWriter(pathName)) {

            file.write(pop.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.print(pop.toJSONString());
    }

	public static Population loadPopulationFromFile(String filePath){
	
		Population pop = new Population();

		// load components of population here
		JSONParser parser = new JSONParser();
		
		try{
			Object obj = parser.parse(new FileReader(filePath));
			
			JSONObject jsonObject = (JSONObject)obj;
			
			JSONArray networks = (JSONArray)jsonObject.get("Networks");
			Iterator<JSONObject> iterator = networks.iterator();
			
			// network properties
			JSONObject net;
			int netID;
			int netInNum;
			int netOutNum;
			int netSpeciesNum;
			int numOfLayers;
			JSONArray genes;
			JSONArray nodes;
			JSONObject numLayers;
			
			// for each network
			while(iterator.hasNext()){
				
				System.out.println(" *** LOAD Another network *** ");

				net = iterator.next();
				
				netID = (int)(long)net.get("ID");
				netInNum = (int)(long)net.get("Number of inputs");
				netOutNum = (int)(long)net.get("Number of outputs");
				netSpeciesNum = (int)(long)net.get("Species");
				numOfLayers = (int)(long)net.get("Number of layers");
				numLayers = (JSONObject)net.get("Number of nodes per layer");
				
				genes = (JSONArray)net.get("Genes");
				nodes = (JSONArray)net.get("Nodes");
				
				
				Iterator<JSONObject> itNodes = nodes.iterator();
				Iterator<JSONObject> itGenes = genes.iterator();

				List<Node> nodesList = new ArrayList<Node>();
				List<Gene> genesList = new ArrayList<Gene>();
				
				// gene properties
				JSONObject gene;
				int innovation;
				int inNode;
				int outNode;
				float weight;
				boolean state;
				
				// for each gene
				while(itGenes.hasNext()){
					
					gene = itGenes.next();
					
					innovation = (int)(long)gene.get("Innovation");
					inNode = (int)(long)gene.get("Input Node");
					outNode = (int)(long)gene.get("Output Node");
					weight = (float)(double)gene.get("Weight");
					state = (boolean)gene.get("State");
					
					genesList.add(new Gene(innovation, inNode, outNode, weight, state));
				}
				
				
				// node properties
				JSONObject node;
				int nodeID;
				int nodeLayer;
				int nodeType;
				
				// for each node
				while(itNodes.hasNext()){
					
					node = itNodes.next();
					
					nodeID = (int)(long)node.get("ID");
					nodeLayer = (int)(long)node.get("Layer");
					nodeType = (int)(long)node.get("Type");
					
					Node newNode = new Node(nodeID, nodeType, nodeLayer);
					
					nodesList.add(newNode);
				}
				
				// for each layer (retrieve the number of nodes)
				int[] numOfNodesPerLayer = new int[Network.MAX_NUMBER_OF_LAYERS];
				for(int i = 0; i < numOfLayers; i++){
					int numOfNodes = (int)(long)numLayers.get(Integer.toString(i));
					numOfNodesPerLayer[i] = numOfNodes;
				}

				pop.addNetwork(new Network(netID, netInNum, netOutNum, netSpeciesNum, numOfLayers, nodesList, genesList, numOfNodesPerLayer));
				
			}
			
			// recover geneHistory
			
			JSONArray geneHistory = (JSONArray)jsonObject.get("Gene History");
			Iterator<JSONObject> itGeneH = geneHistory.iterator();
			
			// gene in History properties
			JSONObject geneH;
			int innovation;
			int inNode;
			int outNode;
			
			// new Gene List (to replace geneHistory)
			List<Gene> geneList = new ArrayList<Gene>();
			
			while(itGeneH.hasNext()){
				
				geneH = itGeneH.next();
				
				innovation = (int)(long)geneH.get("Innovation");
				inNode = (int)(long)geneH.get("Input Node");
				outNode = (int)(long)geneH.get("Output Node");
				
				geneList.add(new Gene(innovation, inNode, outNode, 0f, true));
			}

			Network.setGeneHistory(geneList);
			
			// get and set the Innovation Number from the file
			int innovationNumber = (int)(long)jsonObject.get("Innovation number");
			Network.setInnovationNumber(innovationNumber);
			
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
		
		return pop;
	}
}
