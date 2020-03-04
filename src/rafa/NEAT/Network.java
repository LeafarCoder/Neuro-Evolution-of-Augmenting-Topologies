package rafa.NEAT;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Network implements Serializable{

    public final static int MAX_NUMBER_OF_LAYERS = 100;
    
    // static variable: shared over all networks
    private static int innovationNumber = 0; //initial innovation number of 0
    private static List<Gene> geneHistory = new ArrayList<Gene>();  // keeps the gene historial
    
    private List<Gene> geneList; //list of the genome sequence for this neural network
    private List<Node> nodeList; //list of nodes for this neural network
    private List<List<Integer>> nodesInLayer;	// list of nodes in each layer
    
    private int numberOfInputs; //Number of input perceptrons of neural network (NOT including bias)
    private int numberOfOutputs; //Number of output perceptrons
    private int netID; //ID of this neural network
 
    private int speciesID = -1;	// default value (-1) => has no species
    private double timeLived; //time the neural network actually lived in the test environment (in generations)
    private double netFitness; //fitness of this neural network
    private int numLayers;  //number of layers in network
    private int[] nodeNumPerLayer = new int[MAX_NUMBER_OF_LAYERS];   // almost impossible to get 100 layers.. but oh well xD

    private boolean simulation_running;
    // add "NET GENES HISTORIAL"
   
    
    // constructor to create a network from scratch
    public Network(int netID, int numberOfInputs, int numberOfOutputs) {
        this.netID = netID; //copy ID
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfOutputs;
        
        netFitness = 0f; //reset net fitness
        
        timeLived = 0f; //reset time lived
        numLayers = 2;  // input layer + output layer
        nodeNumPerLayer[0] = numberOfInputs + 1; // (include bias)
        nodeNumPerLayer[1] = numberOfOutputs;
        
        initilizeNodes(); //initialize initial nodes
        initilizeGenes(); //initialize initial gene sequence
        
        setNodesInLayer();
        setNodesForwardGenes();
    }
   
    // constructor used in crossover
    public Network(int netID, int numberOfInputs_, int numberOfOutputs_, List<Node> copyNodes, List<Gene> copyGenes){
    	this.netID = netID;
    	
        numberOfInputs = numberOfInputs_; //copy number of inputs
        numberOfOutputs = numberOfOutputs_; //copy number of outputs
 
        nodeList=copyNodes; //copy node list
        geneList=copyGenes; //copy gene list

        netFitness = 0f; //reset net fitness

        timeLived = 0f; //reset time lived
        
        // set forward genes
        setNodesForwardGenes();
        
        // update layers
        numLayers = 0;
        
        // reset all nodeLayers to zero:
        for (int i = 0; i < getNodeCount(); i++) {
            getNodeByID(i).setLayer(0);
            nodeNumPerLayer[0]++;
        }
        
        // search for every Gene that has an Input (or bias) as In Node
        // and let it propagate to update layers
        for(int i = 0; i < numberOfInputs + 1; i++){
            Node node = nodeList.get(i);
            int nodeType = node.getNodeType();
            if(nodeType == Node.INPUT_NODE || nodeType == Node.INPUT_BIAS_NODE){
                for(Gene g : node.getForwardGenes()){
                    forwardPropagationLayerUpdate(g.getOutID(), 1);
                }
            }
        }
        // set all output Nodes to maximum layer value:
        updateOutputNodesLayers();
        
        // set correct IDs to genes
        for(int i = 0; i < getGeneCount(); i++){
        	getGeneByID(i).setID(i);
        }
        
        setNodesInLayer();
        setNodesForwardGenes();
    }
    
    
    public Network() {
    	
	}

	public int getSpecie(){
        return speciesID;
    }
    
    public double getNetFitness() {
        return netFitness; //return fitness
    }

    public double getTimeLived() {
        return timeLived; //return time lived
    }
 
    public int getNetID() {
        return netID; //return network ID
    }
 
    public int getNodeCount() {
        return nodeList.size(); //return node code
    }

    public int getGeneCount() {
        return geneList.size(); //gene count
    }
    
    public int getNumLayers(){
        return numLayers;
    }
    
    public int getNumberOfInputNodes() {
        return numberOfInputs; //return number of inputs (NOT including Bias)
    }
 
    public int getNumberOfOutputNodes() {
        return numberOfOutputs; //return number of outputs
    }

    public String getGenomeString() {
        Gene gene;
        String genome = "Network "+getNetID()+":\n";

        for (int i = 0; i < getGeneCount(); i++) {
            gene = getGeneByID(i);
            genome += "Gene "+String.format("%3s",i)+" ("+gene.getInnovation()+"): "+gene.getGeneString()+"\n";

        }
        return genome;
    }

    public static Gene getGeneInHistoryByInnovation(int ID){
        return geneHistory.get(ID);
    }

    public static int getGeneHistorySize(){
        return geneHistory.size();
    }
    
    public double[] getOutputValues(){
        double[] values = new double[numberOfOutputs]; //create an array with size of number of output nodes
    
        for (int i = 0; i < numberOfOutputs; i++) { //run through number of outputs
            values[i] = getNodeByID(i + numberOfInputs + 1).getNodeValue(); //set output nodes value (skips inputs and bias nodes)
        }
    
        return values; //return output nodes value array
    }
    
    // First layer (index 0): input layer
    public int getNumNodesInLayer(int layer){
        return nodeNumPerLayer[layer];  
    }
    
    public Gene getGeneByInnovation(int innovation){
        return  geneHistory.get(innovation);
    }
    
    public Gene getGeneByID(int ID){
        if(ID < getGeneCount()){
        	return geneList.get(ID);
        }
        System.err.println("rafa.NEAT.Network.getGeneByID: Could not find the requested Gene!");
        return null;
    }
    
    public Node getNodeByID(int ID){
    	if(ID < getNodeCount()){
        	return nodeList.get(ID);
        }
        System.err.println("rafa.NEAT.Network.getNodeByID: Could not find the requested Node!");
        return null;
    }
    
    public List<Gene> getGeneList(){
        return geneList;
    }

    public List<Node> getNodeList(){
        return nodeList;
    }
    
    public static int getInnovationNumber(){
        return innovationNumber;
    }

    public boolean getSimulationRunning() {
    	return simulation_running;
    }
    
    public void setSimulationRunning(boolean state){
    	simulation_running = state;
    }
    
    public static void setGeneHistory(List<Gene> geneH){
        geneHistory = geneH;
    }
    
    public static void setInnovationNumber(int n){
        innovationNumber = n;
    }
    
    public void setSpecieID(int specie){
        this.speciesID = specie;
    }

    public void setNetID(int nID) {
    	this.netID = nID; //set ID
    }
    
    public void setTimeLived(double timeLived_) {
        this.timeLived = timeLived_; //set time lived
    }

    public void setNetFitness(double netFitness_) {
    	this.netFitness = netFitness_; //set fitness
    }
 
    public void setNumLayers(int numLayers){
        this.numLayers = numLayers;
    }
    
    public void setInputValues(double[] inputs) {
        // for each input value
        for (int i = 0; i < numberOfInputs; i++){
            getNodeByID(i).setNodeValue(inputs[i]);
        }
    }
    
    public void setSpecieControlToDefault(){
    	speciesID = -1;
    }
    
    public void resetGeneHistory(){
        geneHistory = new ArrayList<Gene>();
    }
    
    public boolean hasSpecie(){
        return speciesID != -1;
    }
    
    public void resetNodesValues() {
        for (int i = 0; i < nodeList.size(); i++) { //run through number of inputs
                getNodeByID(i).setNodeValue(0f); //change value of node to given value at index i
        }
    }

    public void addTimeLived(double time_){
        timeLived += time_; //increment by given time lived
    }

    public void mutate(double addGeneProbability, double addNodeProbability, double mutateWeightProbability) {

        if (new Random().nextFloat() <= addGeneProbability) { //random number is below chance
            mutateAddGene(); //add connection between 2 nodes
        }
        
        if (new Random().nextFloat() <= addNodeProbability) {//random number is below chance*2
            mutateAddNode(); //add a new node between an existing connection
        } 
        
        if (new Random().nextFloat() <= mutateWeightProbability){
            mutateWeight(); //mutate weight
        }
    }

    public void clearNodeValues() {
        int numberOfNodes = nodeList.size(); //number of nodes
    
        for (int i = 0; i < numberOfNodes; i++) { //run through all nodes
            getNodeByID(i).setNodeValue(0f); //set values to 0
        }
    }

    public static Network crossoverNetworks (int newID, Network parent1, Network parent2) {
    	// newID is the ID to be attributed to the new newtwork
    	
        Network child = null; //child to create
    
        Hashtable<Integer, Gene[]> geneHash = new Hashtable<Integer, Gene[]>(); //hash table to be used to compared genes from the two parents
    
        List<Gene> childGeneList = new ArrayList<Gene>(); //new child gene list to be created
        List<Node> childNodeList = new ArrayList<Node>(); //new child node list to be created
    
        List<Gene> geneList1 = parent1.geneList; //get gene list of parent 1
        List<Gene> geneList2 = parent2.geneList; //get gene list of parent 2
    
        int numberOfGenes1 = geneList1.size(); //get number of genes in parent 1
        int numberOfGenes2 = geneList2.size(); //get number of genes in parent 2
        int numberOfInputs = parent1.getNumberOfInputNodes(); //number of inputs (same for both parents)
        int numberOfOutputs = parent1.getNumberOfOutputNodes(); //number of outputs (same for both parents)
    
        // Both parents have the same number of input and output nodes.
        // Those are the first (inputNodesNumber + outputNodesNumber + 1) nodes.
        // The hidden nodes are the ones that follow.
        // The child must inherit the nodes from the parent that has more nodes! 
        if (parent1.getNodeCount() > parent2.getNodeCount()) { //if parents 1 has more nodes than parent 2
             //copy parent1's nodes list
            for(int i = 0; i < parent1.getNodeCount(); i++){
                childNodeList.add(new Node(parent1.getNodeByID(i)));
            }
        }else { //otherwise parent 2 has equal and more nodes than parent 1
             //copy parent2's nodes list
            for(int i = 0; i < parent2.getNodeCount(); i++){
                childNodeList.add(new Node(parent2.getNodeByID(i)));
            }
        }
    
        // Create a hashtable with innovation times and the genes that were generated at those times
        
        for (int i = 0; i < numberOfGenes1; i++) { //run through all genes in parent 1
            geneHash.put(geneList1.get(i).getInnovation(), new Gene[] {geneList1.get(i), null}); //add into the hash with innovation number as the key and gene array of size 2 as value
        }
    
        for (int i = 0; i < numberOfGenes2; i++) { //run through all genes in parent 2
            int innovation = geneList2.get(i).getInnovation(); //get innovation number
            
            if (geneHash.containsKey(innovation)) { //if there is a key in the hash with the given innovation number
                Gene[] geneValue = (Gene[])geneHash.get(innovation); //get gene array value with the innovation key
                geneValue[1] = geneList2.get(i); //since this array already contains value in first location, we can add the new gene in the second location
                geneHash.remove(innovation); //remove old value with the key
                geneHash.put(innovation, geneValue); //add new value with the key
            }
            else { //there exists no key with the given innovation number
                geneHash.put(innovation, new Gene[] {null , geneList2.get(i) }); //add into  the hash with innovation number as the key and gene array of size 2 as value
            }
        }
    
        // get all innovation keys in ascending order
        
        Enumeration<Integer> keysEnum = geneHash.keys(); //get all keys in the hash
        // Enumeration to Integer Array
        int[] keys = new int[geneHash.size()]; //int array with size of number of keys in the hash
        int cnt=0;
        while (keysEnum.hasMoreElements()) {
            keys[cnt++]=(int)(keysEnum.nextElement());
        }
        Arrays.sort(keys); //order keys in ascending order
    
        Gene gene = null;
        
        // Crossover happens here:
        for (int i = 0; i < keys.length; i++){ //run through all innovation numbers
            Gene[] geneValue = (Gene[])geneHash.get(keys[i]); //get value at each index
    
            //compare value is used to compare gene activation states in each parent
            int compareValue = -1;
            
            // Gene exists in both parents:
            // 0 = both genes are active
            // 1 = both genes are inactive
            // 2 = one is inactive and one is active
            // Gene only exists in one of the parents:
            // 3 = gene is dominant in one of the parents and is active
            // 4 = gene is dominant in one of the parents and is inactive
            
            int index = -2; // index of gene (0, 1 or -1 in case of both)
            
            if (geneValue[0] != null && geneValue[1] != null){ //gene exits in both parents
                
                // we can insert some weight to favor the parent with most fitness
                if (geneValue[0].getGeneState() && geneValue[1].getGeneState()) { // both genes are active
                    compareValue = 0;
                }
                else if (!geneValue[0].getGeneState() && !geneValue[1].getGeneState()) { // both genes are inactive
                    compareValue = 1;
                }
                else { // one gene is active and the other is inactive
                    compareValue = 2;
                }
                
                index = -1;	// gene exists in both parents
                
            }else{  // if gene only exist in one parent
                
                if(geneValue[0] != null) { //gene value at first index from parent 1 exists
                    
                    // if gene is active (3) else if gene is not active (4)
                    compareValue = geneValue[0].getGeneState() ? 3 : 4; //set compared value to 3
    
                    index = 0;
                    
                }else if (geneValue[1] != null) { //both parents have equal fitness and gene value at second index from parent 2 exists
                    
                    // if gene is active (3) else if gene is not active (4)
                    compareValue = geneValue[1].getGeneState() ? 3 : 4; //set compared value to 3
        
                    index = 1;
                }
            }
            
            // if a gene was selected
            if(index != -2){
            	float[] fitnesses = new float[]{(float) parent1.getNetFitness(), (float) parent2.getNetFitness()};
                // having the two parents' genes mutate the new inherited gene depending on the compareValue
                gene = crossoverGeneMutate(fitnesses, geneValue, index, compareValue);
                //add gene to the child gene list
                childGeneList.add(gene);
            }
            
        }
    
        child = new Network(newID, numberOfInputs, numberOfOutputs, childNodeList, childGeneList); //create new child neural network
        
        return child; //return newly created neural network
    }

    // feed input and propagate
    public double[] fireNet(double[] inputs){

    	// reset all node values (no need for input nodes)
    	for(int i = getNumberOfInputNodes(); i < getNodeCount(); i++){
    		getNodeByID(i).setNodeValue(0);
    	}
    	
    	// set input
    	for(int i = 0; i < getNumberOfInputNodes(); i++){
    		getNodeByID(i).setNodeValue(inputs[i]);
    	}
    	// set bias to 1 (just to be sure)
    	getNodeByID(getNumberOfInputNodes()).setNodeValue(1);
    	
    	// for each layer 'i'
    	for(int i = 0; i < this.getNumLayers(); i++){
    		
    		// for each node 'j' in layer 'i'
    		for(int j = 0; j < this.getNumNodesInLayer(i); j++){
        		int inID = nodesInLayer.get(i).get(j);
        		Node inNode = getNodeByID(inID);
        		
        		// activate node before spreading (if not input or bias node)
        		if((inNode.getNodeType() != Node.INPUT_BIAS_NODE) && (inNode.getNodeType() != Node.INPUT_NODE)){
        			inNode.activation();
        		}
        		
        		List<Gene> forwardGenes = inNode.getForwardGenes();

        		// for each link between node 'inID' and every other node 'outID' in ForwardGenes
        		for(Gene g : forwardGenes){
        			
        			// if gene is not activated then skip
        			if(g.getGeneState()){
        				
        		        if(getNodeByID(g.getInID()).getLayer() == getNodeByID(g.getOutID()).getLayer())System.out.println("Nodes linking in the same Layer: " + g.getInID() + " | " + g.getOutID());

	        			double signal = inNode.getNodeValue() * g.getWeight();
	        			getNodeByID(g.getOutID()).addSignal(signal);
        			}
        		}
        		
        	}
    	}
    	
    	return getOutputValues();
    }
    
    
    private double[] getAllNodeValues() {
        double[] values = new double[nodeList.size()]; //create an array with the size of number of nodes
 
        for (int i = 0; i < values.length; i++){ //run through number of nodes
            values[i] = getNodeByID(i).getNodeValue(); //set node values
        }
        return values; //return all nodes value array
    }
    
    private double[] getInputValues(){
        double[] values = new double[numberOfInputs]; //create an array with size of number of input nodes
 
        for (int i = 0; i < numberOfInputs; i++){ //run through number of inputs
            values[i] = getNodeByID(i).getNodeValue(); //set input nodes value
        }
 
        return values; //return input nodes value array
    }
    
    private double[] getHiddenValues(){
        int numberOfHiddens = nodeList.size() - (numberOfInputs + numberOfOutputs); //get number of hidden nodes that exist
        double[] values = new double[numberOfHiddens];  //create an array with size of number of hidden nodes
 
        for (int i = 0; i < numberOfHiddens; i++){  //run through number of hiddens
            values[i] = getNodeByID(i + numberOfInputs + numberOfOutputs).getNodeValue();  //set hidden nodes value
        }
 
        return values; //return hidden nodes value array
    }
    
    private void setNodesInLayer(){
    	// sets an array for each Layer with the Nodes (ID's) it contains
    	
    	nodesInLayer = new ArrayList<List<Integer>>();
    	
    	// initialize each layer
    	for(int i = 0; i < this.getNumLayers(); i++){
    		nodesInLayer.add(new ArrayList<Integer>());
    	}
    	for(int i = 0; i < this.getNodeCount(); i++){    		
    		nodesInLayer.get(this.getNodeByID(i).getLayer()).add(i);
    	}
    	/* See Nodes per Layer
    	System.out.println("Nodes per layer:");
    	for(int i = 0; i < this.getNumLayers(); i++){
    		for(int j = 0; j < nodesInLayer.get(i).size(); j++){
    			System.out.print(nodesInLayer.get(i).get(j)+" ");
    		}
    		System.out.println();
    	}
    	*/
    }
    
    private void setNodesForwardGenes(){
    	// Sets the ForwardGenes (outID of genes that link a node to another that is at a higher layer) 
    	// for each Node in the Network
    	
    	// resets to null
    	for(int i = 0; i < getNodeCount(); i++){
    		getNodeByID(i).clearForwardGenes();
    	}
    	// updates
    	for(Gene g: geneList){
    		getNodeByID(g.getInID()).addForwardGene(g);
    	}
    	
    	
    }
    
    private int getInnovationNumber(int in, int out){
        Gene gene;
        
        for(int i = 0; i < geneHistory.size(); i++){
            gene = geneHistory.get(i);
            
            if(gene.getInID() == in && gene.getOutID() == out){
                return gene.getInnovation();                
            }
        }
        
        geneHistory.add(new Gene(innovationNumber, 0, in , out, 0f, true));
        return innovationNumber++;
    }

    private void initilizeNodes() {
        nodeList = new ArrayList<Node>(); //create an empty node list
    
        Node node = null;
    
        for (int i = 0; i < numberOfInputs + 1; i++) { //run through number of input nodes (+ Bias node)
    
        	//if this is the last input
            if(i == numberOfInputs){
                node = new Node(i,Node.INPUT_BIAS_NODE); //make it a input bias type node  with index i as node ID
            }else{ //if this is not the last input
                node = new Node(i, Node.INPUT_NODE); //make it a input type node with index i as node ID
            }
            node.setLayer(0);
            
            nodeList.add(node); //add node to the node list
        }
    
        for (int i = numberOfInputs + 1; i < numberOfInputs + numberOfOutputs + 1; i++){  //run through number of output perceptrons
            node = new Node(i, Node.OUTPUT_NODE); //make it an output type node  with index i as node ID
            node.setLayer(1);
            
            nodeList.add(node); //add node to the node list
        }
    }

    private void initilizeGenes() {
        geneList = new ArrayList<Gene>(); //create an empty gene list
        Random rand = new Random();
        
        for (int i = 0; i < numberOfInputs + 1; i++){ //run through number of inputs (+ bias)
            for (int j = numberOfInputs + 1; j < numberOfInputs + numberOfOutputs + 1; j++){ //run through number of outputs
                
                // System.out.println(getNetID()+" -> "+"from node "+i+" to "+j+": ");
                Gene gene = new Gene(getInnovationNumber(i,j), getGeneCount(), i, j, ((rand.nextFloat()*2) - 1) * Population.mutationWeightPower, true); // create gene with default weight in [-1, 1]*Population.mutationWeightPower and active
                insertNewGene(gene); //insert gene to correct location in gene list
            }
        }
    }

    private void mutateAddGene(){
        //random node ID's
        int randomNodeID1 = 0, randomNodeID2 = 0;
        int randNodeIndex1, randNodeIndex2;
        
        boolean canAdd = false;

        // used to randomly select the second random node
        List<Integer> pick1 = new ArrayList<Integer>();
        List<Integer> pick2;
        // add all nodes to pick1
        for (int i = 0; i < nodeList.size(); i++){
            // prevents from picking an Output as InNode to new connection
            if(getNodeByID(i).getNodeType() != Node.OUTPUT_NODE){
                pick1.add(i);
            }
        }
        
        while(!pick1.isEmpty()) {
        	// pick any random node ID from 0 to nodeList.size - 1
            randNodeIndex1 = new Random().nextInt(pick1.size());    
            randomNodeID1 = pick1.get(randNodeIndex1);
            Node node1 = getNodeByID(randomNodeID1);
            pick1.remove(randNodeIndex1);
            
            pick2 = new ArrayList<Integer>();
            
            // for each other node (not the randomNodeID1)
            for (int i = 0; i < nodeList.size(); i++) {
                if(i == randomNodeID1)continue;
                Node node2 = getNodeByID(i);
                // if ID i is an input or comes before (in layer) then skip
                if(node2.getNodeType() != Node.INPUT_NODE && node2.getNodeType() != Node.INPUT_BIAS_NODE && node2.getLayer() > node1.getLayer()){                
                    if(!connectionExists(randomNodeID1, i)){
                        // System.out.println(randomNodeID1+" can connect with "+i);
                        pick2.add(i);
                    }
                }
            }
            
            // if randomNodeID1 is connected to all other nodes then repeat from above
            if(pick2.isEmpty()) continue;
            
            randNodeIndex2 = new Random().nextInt(pick2.size());
            randomNodeID2 = pick2.get(randNodeIndex2);

            canAdd = true;
            break;
            
        }
        
        // if there is no possible connection to make return function
        if(!canAdd)return;
        
        // feedback probability -> probability that the connection feeds from a higher layer to a lower layer
        int node1Layer = getNodeByID(randomNodeID1).getLayer();
        int node2Layer = getNodeByID(randomNodeID2).getLayer();
        
        int feedbackProbability = 0;   // 0% chance of feedback
        int rand = new Random().nextInt(101);
        if(rand >= feedbackProbability){
            // dont feedback
            if(node1Layer > node2Layer){
                int store = randomNodeID1;
                randomNodeID1 = randomNodeID2;
                randomNodeID2 = store;
            }
        }else{
            // feedback
            if(node1Layer <= node2Layer){
                int store = randomNodeID1;
                randomNodeID1 = randomNodeID2;
                randomNodeID2 = store;
            }
        }
        
        // if second node is input then reverse
        /*
        int store = randomNodeID1;
        randomNodeID1 = randomNodeID2;
        randomNodeID2 = store;       
        /*

        if(rand <= feedbackProbability)
            System.out.println("Add Connection Mutation (f):");
        else
            System.out.println("Add Connection Mutation:");
        
        System.out.println("Input ID: "+randomNodeID1);
        System.out.println("Output ID: "+randomNodeID2);
        System.out.println();
            
        */
        
        Gene gene = new Gene(getInnovationNumber(randomNodeID1,randomNodeID2), getGeneCount(), randomNodeID1, randomNodeID2, 1f, true); //create gene which is enabled and 1 as default weight

        insertNewGene(gene); //add gene to the gene list
        getNodeByID(randomNodeID1).addForwardGene(gene);
        
    }
    
    
    private void mutateAddNode(){
        int inID, newID, outID; //inID is old connections in node, outID is old connections out node, newID is the new node, and new innovation number for the connections
      
        double oldWeight; //weight from the old gene
  
        Gene oldGene = null; //find a random old gene
        
        ArrayList<Integer> activeGenes = getActiveGenes();
        if(activeGenes.size() == 0){return;}
        
        int randomGeneIndex = new Random().nextInt(activeGenes.size()); //pick random gene
        oldGene = geneList.get(activeGenes.get(randomGeneIndex)); //get gene at random index

        oldGene.setGeneState(false); //disable this gene
        inID = oldGene.getInID(); //get in node ID
        outID = oldGene.getOutID(); //get out node ID
        oldWeight = oldGene.getWeight(); //get old weight
        
        Node newNode = new Node(getNodeCount(), Node.HIDDEN_NODE); //create new hidden node
        newID = newNode.getNodeID(); //get new node's ID
        
        System.out.println("	Old in node layer: " + getNodeByID(inID).getLayer() + " id: " + getNetID());
        newNode.setLayer(getNodeByID(inID).getLayer() + 1);    // set Layer of the new Node
        nodeNumPerLayer[newNode.getLayer()]++;
                
        nodeList.add(newNode); //add new node to the node list
        
        // add genes to gene list
        Gene newGene1 = new Gene(getInnovationNumber(inID, newID), getGeneCount(), inID, newID, 1f, true); //create new gene
        insertNewGene(newGene1);
        Gene newGene2 = new Gene(getInnovationNumber(newID, outID), getGeneCount(), newID, outID, oldWeight, true);  //create new gene
        insertNewGene(newGene2);
        
        getNodeByID(inID).addForwardGene(newGene1);
        getNodeByID(newID).addForwardGene(newGene2);

     // search for every Gene that has an Input (or bias) as In Node
        // and let it propagate to update layers
        for(int i = 0; i < numberOfInputs + 1; i++){
            Node node = nodeList.get(i);
            int nodeType = node.getNodeType();
            if(nodeType == Node.INPUT_NODE || nodeType == Node.INPUT_BIAS_NODE){
                for(Gene g : node.getForwardGenes()){
                    forwardPropagationLayerUpdate(g.getOutID(), 1);
                }
            }
        }
        // propagate to forward nodes and update their layer values
        // forwardPropagationLayerUpdate(outID, newNode.getLayer() + 1);
        
        // set all output Nodes to maximum layer value:
        // updateOutputNodesLayers();
        
        /*
        // due to the way the forward propagation is design there might occur lapses in layer, so we correct that here:
        // first and second delimit the zero sequence
        int first = -1;
        int second = -1;
        for (int i = 0; i < getNumLayers(); i++){
            if(nodeNumPerLayer[i] == 0){
                if(first == -1){
                    // at first occurrence set the boundaries to this index
                    first = i;
                    second = i;
                }else{
                    // as we get more occurrences of zeros drag the second boundary index
                    second = i;
                }
            }
        }
        // if sequence of zeros occur:
        if(first != -1){
        	for (int i = second + 1; i < getNumLayers(); i++) {
        		nodeNumPerLayer[first++] = nodeNumPerLayer[i];
        		nodeNumPerLayer[i] = 0;
        	}
        	setNumLayers(first);
        }
        
        */
        
        // set all output Nodes to maximum layer value:
        updateOutputNodesLayers();
        
        setNodesInLayer();
    }
    
    private void mutateWeight(){

    	for (int i = 0; i < geneList.size(); i++){ //run through all genes
    		Gene gene = geneList.get(i); // get gene at index i

    		if(gene.getGeneState()){	// only mutate active genes
    			float linearMutationProb = new Random().nextFloat();
    			if (linearMutationProb <= Population.uniformWeightMutationProb) {	// make uniform mutation
    				// using gaussian function
    				double change = new Random().nextGaussian() * 0.1 * Population.mutationWeightPower;
    				double weight = gene.getWeight() + change;
    				gene.setWeight(weight);

    			}else{ // make random weight mutation
    				//pick random weight between -Population.mutationWeightPower and Population.mutationWeightPower
    				double weight = ((new Random().nextFloat() * 2) - 1) * Population.mutationWeightPower;
    				gene.setWeight(weight);
    			}

    		}
        }
    }
    
    private boolean hasActiveGene(){
        boolean ok = false;
        for (int i = 0; i < geneList.size(); i++) {
            ok |= geneList.get(i).getGeneState(); //  same as: ok = ok || geneList...
        }
        return ok;
    }
    
    // return a list with Active Gene's IDs (0-indexed)
    private ArrayList<Integer> getActiveGenes(){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        for (int i = 0; i < geneList.size(); i++) {
        	Gene g = geneList.get(i);
            if(g.getGeneState()){
            	ans.add(g.getID());
            }
        }
        return ans;
    }
    
    private void updateOutputNodesLayers(){
    	int numLayers = getNumLayers();
        for(int i = numberOfInputs + 1; i < numberOfInputs + numberOfOutputs + 1; i++){ // iterate over all outputs
            if(getNodeByID(i).getNodeType() == Node.OUTPUT_NODE){
                nodeNumPerLayer[getNodeByID(i).getLayer()]--;
                getNodeByID(i).setLayer(numLayers - 1);
                nodeNumPerLayer[numLayers - 1]++;
            }
        }
    }
    
    private boolean connectionExists(int inID, int outID) {
        
        for (int i = 0; i < geneList.size(); i++) { //run through gene list
            int nodeInID = geneList.get(i).getInID(); //get in node
            int nodeOutID = geneList.get(i).getOutID(); //get out node
 
            if (nodeInID == inID && nodeOutID == outID){
                return true; //return true
            }
        }
        
        return false; //return false if no match
    }
    
    private void forwardPropagationLayerUpdate(int id , int layer){

    	if(layer >= MAX_NUMBER_OF_LAYERS)return;
    	
    	Node node = getNodeByID(id);
    	
        if(getNumLayers() < layer + 1) setNumLayers(layer + 1);
        
        // if node is input return
        if(node.getNodeType() == Node.INPUT_NODE || node.getNodeType() == Node.INPUT_BIAS_NODE){
            return;
        }

        // update number of nodes per layer:
        /*
        System.out.println("Updating "+id+":");
        System.out.println("old num in layer "+getNodeByID(id).getLayer()+": "+nodeNumPerLayer[getNodeByID(id).getLayer()]);
        System.out.println("old num in layer "+layer+": "+nodeNumPerLayer[layer]);
        System.out.println();
        */
        
        // update node layer
        if(node.getLayer() < layer){
            nodeNumPerLayer[node.getLayer()]--;
            nodeNumPerLayer[layer]++;
            
            node.setLayer(layer);
            // System.out.println(" Node "+id+" updated its layer to "+layer);
        }
        
        // if the current node is an output then no need for further propagation
        if(node.getNodeType() == Node.OUTPUT_NODE){
        	setNumLayers(Math.max(layer, getNumLayers()));
            // System.out.println(" In output. Going to return.");
            return;
        }
        
        // WARNING: this assumes that the NN is not recurrent
        for(Gene gene: node.getForwardGenes()){
        	// -> System.out.println(gene.getInID() + "(" + getNodeByID(gene.getInID()).getLayer() + ") | " + gene.getOutID() + "(" + getNodeByID(gene.getOutID()).getLayer() + ")");
            forwardPropagationLayerUpdate(gene.getOutID(), layer + 1);
        }

    }

    private void insertNewGene(Gene gene) {
        int innovation = gene.getInnovation(); //get innovation number
        int low=0;
        int high=getGeneCount()-1;
        int mid;
        
        
     // binary search for innovation
        if(high>=0)
        while(low<=high){
            mid = (low+high)/2;
            
            if(innovation < getGeneList().get(mid).getInnovation()){
                high = mid - 1;
            }else{
                low = mid + 1;
            }
        }
        
        //add gene to the given insert index location
        geneList.add(low,gene);   
    }
    
    private static Gene crossoverGeneMutate(float[] fitnesses, Gene[] copyGene, int index, int compareValue) {
        
    	Gene gene;
        
        Random rand = new Random(System.currentTimeMillis());
        float randomNumber;
        
        if(index == -1){	// both parents have the gene
            randomNumber = rand.nextFloat();
            
            // 40% chance that the weight of the new node is the average of the parents' nodes
            // 60% chance that the node is randomly picked from either parent
            if(randomNumber <= 40){
                gene = new Gene(copyGene[0]);
                gene.setWeight((copyGene[0].getWeight() + copyGene[1].getWeight())/2f);
            }else{
                // randomly select one gene from either parent
                gene = new Gene(copyGene[rand.nextInt(2)]);
            }
        }else{
            gene = new Gene(copyGene[index]);
        }
        
        //System.out.println("Gene "+gene.getInnovation()+":");
        
        // compareValue may change the activation state of the gene
        switch (compareValue) {
        
        // gene exists in both parents && is active in both parents
        case 0:
            if (rand.nextFloat() < Population.crossoverToggleInheritedGeneStateProb){
            	// inactivate gene
                gene.setGeneState(false);
            }
            break;
        
        // gene exists in both parents && is inactive in both parents
        case 1:
            if (rand.nextFloat() < Population.crossoverToggleInheritedGeneStateProb) {
            	// reactivate gene
                gene.setGeneState(true);
            }
            break;
           
        // gene exists in both parents && is active only in one
        case 2:
        	// set to State from network with higher fitness
        	boolean on = true;	// if both have the same fitness leave at True
        	if(fitnesses[0] > fitnesses[1]){
        		on = copyGene[0].getGeneState();
        	}else if(fitnesses[0] < fitnesses[1]){
        		on = copyGene[1].getGeneState();
        	}
        	gene.setGeneState(on);
            break;
        
        // gene only exists in one parent && is active
        case 3:
            if (rand.nextFloat() < Population.crossoverToggleInheritedGeneStateProb) {
                gene.setGeneState(false);
            }
            break;
            
        // gene only exists in one parent && is inactive
        case 4:
            if (rand.nextFloat() < Population.crossoverToggleInheritedGeneStateProb) {
                gene.setGeneState(true);
            }
            break;
        }
 
        return gene; //return new gene
    }
    
    public void seeTest(){
    	for(int i = 0; i < getNodeCount(); i++){
    		System.out.print(i+": ");
    		for(int j = 0; j < getNodeByID(i).getForwardGenes().size(); j++){
    			System.out.print(getNodeByID(i).getForwardGenes().get(j)+"("+
    		getNodeByID(i).getForwardGenes().get(j).getGeneState()+") ");
    		}
    		System.out.println();
    	}
    }
}