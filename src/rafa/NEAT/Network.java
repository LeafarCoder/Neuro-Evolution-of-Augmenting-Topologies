package rafa.NEAT;

import java.util.*;

public class Network {

    public final static int MAX_NUMBER_OF_LAYERS = 100;
    
    private static int innovationNumber = 0; //initial innovation number of 0
    
    private List<Gene> geneList; //list of the genome sequence for this neural network
    private List<Node> nodeList; //list of nodes for this neural network
    
    // static variable: shared over all networks
    private static List<Gene> geneHistory = new ArrayList<Gene>();  // keeps the gene historial
    
    
    private int numberOfInputs; //Number of input perceptrons of neural network (including bias)
    private int numberOfOutputs; //Number of output perceptrons
    private int netID; //ID of this neural network
 
    private int species = -1;
    private float timeLived; //time the neural network actually lived in the test environment
    private float netFitness; //fitness of this neural network
    private int numLayers;  //number of layers in network
    private int[] nodeNumPerLayer = new int[MAX_NUMBER_OF_LAYERS];   // almost impossible to get 100 layers.. but oh well xD

    // add "NET GENES HISTORIAL"
    
    
    // control variables
    boolean[] visited;

    
    // constructor to load network from JSON file
    
    public Network(int netID, int numberOfInputs, int numberOfOutputs, int species, int numLayers, List<Node> nodes, List<Gene> genes, int[] nodeNumPerLayer){
        
        this.netID = netID;
        this.numberOfInputs = numberOfInputs;
        this.numberOfOutputs = numberOfOutputs;
        this.species = species;
        this.numLayers = numLayers;
        
        this.geneList = genes;
        this.nodeList = nodes;
        
        this.nodeNumPerLayer = nodeNumPerLayer;
        
    }
    
    // constructor to create a network from scratch
    public Network(int netID, int numberOfInputs, int numberOfOutputs) {
        this.netID = netID; //copy ID
        this.numberOfInputs = numberOfInputs + 1; // add bias input
        this.numberOfOutputs = numberOfOutputs;
        
        //netFitness = 0f; //reset net fitness
        netFitness = new Random().nextFloat();
        
        timeLived = 0f; //reset time lived
        numLayers = 2;  // input layer + output layer
        nodeNumPerLayer[0] = numberOfInputs + 1;
        nodeNumPerLayer[1] = numberOfOutputs;
        
        initilizeNodes(); //initialize initial nodes
        initilizeGenes(); //initialize initial gene sequence
    }
   
    // constructor used in crossover
    public Network(int numberOfInputs_, int numberOfOutputs_, List<Node> copyNodes, List<Gene> copyGenes){

        numberOfInputs = numberOfInputs_; //copy number of inputs
        numberOfOutputs = numberOfOutputs_; //copy number of outputs
 
        nodeList=copyNodes; //copy node list
        geneList=copyGenes; //copy gene list

        //netFitness = 0f; //reset fitness
        netFitness = new Random().nextFloat();
        
        timeLived = 0f; //reset time lived
        
        // update layers
        numLayers = 0;
        
        // reset all nodeLayers to zero:
        for (int i = 0; i < getNodeCount(); i++) {
            getNodeByID(i).setLayer(0);
            nodeNumPerLayer[0]++;
        }
        
        // search for every Gene that has an Input (or bias) as In Node
        // and let it propagate to update layers
        
        /*
        for(int i = 0; i < getGeneCount(); i++){
            Gene gene = geneList.get(i);
            
            System.out.println("Gene "+i+": "+gene.getInID()+" -> "+gene.getOutID() + " ("+gene.getGeneState()+")");
        }
        */
        
        for(int i = 0; i < getGeneCount(); i++){
            Gene gene = geneList.get(i);
            
            if(getNodeByID(gene.getInID()).getNodeType() == Node.INPUT_NODE ||
            getNodeByID(gene.getInID()).getNodeType() == Node.INPUT_BIAS_NODE){
                visited = new boolean[getNodeCount()];
                visited[gene.getInID()] = true;
                // System.out.println("From node: "+gene.getInID());
                forwardPropagationLayerUpdate(gene.getOutID(), 1);
            }
        }
        // set all output Nodes to maximum layer value:
        for(int i = 0; i < getNodeCount(); i++){
            if(getNodeByID(i).getNodeType() == Node.OUTPUT_NODE){
                nodeNumPerLayer[getNodeByID(i).getLayer()]--;
                getNodeByID(i).setLayer(getNumLayers() - 1);
                nodeNumPerLayer[getNodeByID(i).getLayer()]++;
            }
        }
    }
    
    
    public int getSpecie(){
        return species;
    }
    
    public float getNetFitness() {
        return netFitness; //reutrn fitness
    }

    public float getTimeLived() {
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
        return numberOfInputs; //return number of inputs
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

    public static Gene getGeneInHistoryByID(int ID){
        return geneHistory.get(ID);
    }

    public static int getGeneHistorySize(){
        return geneHistory.size();
    }
    
    public float[] getOutputValues(){
        float[] values = new float[numberOfOutputs]; //create an array with size of number of output nodes
    
        for (int i = 0; i < numberOfOutputs; i++) { //run through number of outputs
            values[i] = getNodeByID(i + numberOfInputs).getNodeValue(); //set output nodes value
        }
    
        return values; //return output nodes value array
    }
    
    public int getNumNodesInLayer(int layer){
        return nodeNumPerLayer[layer];  
    }
    
    public Gene getGeneByID(int ID){
        return  geneList.get(ID);
    }
    
    public Node getNodeByID(int ID){
        return  nodeList.get(ID);
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

    public static void setGeneHistory(List<Gene> geneH){
        geneHistory = geneH;
    }
    
    public static void setInnovationNumber(int n){
        innovationNumber = n;
    }
    
    public void setSpecie(int specie){
        this.species = specie;
    }

    public void setNetID(int nID) {
        netID = nID; //set ID
    }
    
    public void setTimeLived(float timeLived_) {
        timeLived = timeLived_; //set time lived
    }

    public void setNetFitness(float netFitness_) {
        netFitness = netFitness_; //set fitness
    }
 
    public void setNumLayers(int numLayers){
        this.numLayers = numLayers;
    }
    
    public void setInputValues(float[] inputs) {
        // for each input value (excluding the bias)
        for (int i = 0; i < numberOfInputs - 1; i++){
            getNodeByID(i).setNodeValue(inputs[i]);
        }
    }
    
    public void resetGeneHistory(){
        geneHistory = new ArrayList<Gene>();
    }
    
    public boolean hasSpecie(){
        return species != -1;
    }
    
    public void resetNodesValues() {
        for (int i = 0; i < nodeList.size(); i++) { //run through number of inputs
                getNodeByID(i).setNodeValue(0f); //change value of node to given value at index i
        }
    }
    
    public void addNetFitness(float netFitness_) {
        netFitness += netFitness_; //increment by given fitness
    }

    public void addTimeLived(float time_) {
        timeLived += time_; //increment by given time lived
    }

    public float[] fireNet(float[] inputs){
        
        // resets all node values to 0
        resetNodesValues();
        //set input values to the input nodes
        setInputValues(inputs);
    
        //feed forward net
        float[] tempValues = getAllNodeValues(); //create a temporary storage of previous node values (used as a phenotype)
    
        for (int i = 0; i < geneList.size(); i++) { //run through number of genes
            Gene gene = geneList.get(i); //get gene at index i
            
            if (gene.getGeneState()) { //if gene is active
                int inID = gene.getInID(); //get in node ID
                int outID = gene.getOutID(); //get out node ID
                float weight = gene.getWeight(); //get weight of the connection

                float inNodeValue = tempValues[inID]; //get in node's value
                float outNodeValue = tempValues[outID]; //get out node's value
                
                float newOutNodeValue = outNodeValue + (inNodeValue*weight); //calculate new out node's value
                getNodeByID(outID).setNodeValue(newOutNodeValue); //set new value to the out node
            }
        }
    
        //Activation
        for (int i = 0; i < nodeList.size(); i++) {
            // activation function over all nodes
            getNodeByID(i).activation();
        }
    
        return getOutputValues(); //return output
    }

    public void mutate(int addConnectionProbability, int addNodeProbability, int mutateWeightProbability) {
        
    	int randomNumber = new Random().nextInt(100) + 1; //random number between 0 and 100
        if (randomNumber <= addConnectionProbability) { //random number is below chance
            mutateAddConnection(); //add connection between 2 nodes
        }
        
        randomNumber = new Random().nextInt(100) + 1;
        if (randomNumber <= addNodeProbability) {//random number is below chance*2
            mutateAddNode(); //add a new node between an existing connection
        }
        
        randomNumber = new Random().nextInt(100) + 1;
        if (randomNumber <= mutateWeightProbability){
            mutateWeight(); //mutate weight
        }
    }

    public void clearNodeValues() {
        int numberOfNodes = nodeList.size(); //number of nodes
    
        for (int i = 0; i < numberOfNodes; i++) { //run through all nodes
            getNodeByID(i).setNodeValue(0f); //set values to 0
        }
    }

    public static Network crossoverNetworks (Network parent1, Network parent2) {
        Network child = null; //child to create
    
        Hashtable<Integer, Gene[]> geneHash = new Hashtable<Integer, Gene[]>(); //hash table to be used to compared genes from the two parents
    
        List<Gene> childGeneList = new ArrayList<Gene>(); //new gene child gene list to be created
        List<Node> childNodeList = new ArrayList<Node>(); //new child node list to be created
    
        List<Gene> geneList1 = parent1.geneList; //get gene list of the parent 1
        List<Gene> geneList2 = parent2.geneList; //get gene list of parent 2
    
        int numberOfGenes1 = geneList1.size(); //get number of genes in parent 1
        int numberOfGenes2 = geneList2.size(); //get number of genes in parent 2
        int numberOfInputs = parent1.getNumberOfInputNodes(); //number of inputs (same for both parents)
        int numberOfOutputs = parent1.getNumberOfOutputNodes(); //number of outputs (same for both parents)
    
        // Both parents have the same number of input and output nodes.
        // Those are the first (inputNodesNumber + outputNodesNumber) nodes.
        // The hidden nodes are the ones that follow.
        // The child must inherit the nodes from the parent that has more nodes! 
        if (parent1.getNodeCount() > parent2.getNodeCount()) { //if parents 1 has more nodes than parent 2
             //copy parent1's nodes list
            for(int i = 0; i < parent1.getNodeCount(); i++){
                childNodeList.add(new Node(parent1.getNodeByID(i)));
            }
        }
        else { //otherwise parent 2 has equal and more nodes than parent 1
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
        for (int i = 0; i < keys.length; i++){ //run through all keys
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
                
                index = -1;       
                
            }else{  // if gene doens't exist in both parents
                
                /*

                // if gene only exists in parent 1 and this is the parent with most fitness
                if(parent1.getNetFitness() > parent2.getNetFitness() && geneValue[0] != null){
                    
                    // if gene is active (3) else if gene is not active (4)
                    compareValue = geneValue[0].getGeneState() ? 3 : 4; //set compared value to 3
                        
                    // gene comes from parent 1 (index 0)
                    index = 0;
                
                // if gene only exists in parent 2 and this is the parent with most fitness
                }else if(parent1.getNetFitness() < parent2.getNetFitness() && geneValue[1] != null) {
                    
                    // if gene is active (3) else if gene is not active (4)
                    compareValue = geneValue[1].getGeneState() ? 3 : 4; //set compared value to 3
        
                    // gene comes from parent 2 (index 1)
                    index = 1;
                    
                }else if
                
                */
                
                if(geneValue[0] != null) { //both parents have equal fitness and gene value at first index from parent 1 exists
                    
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
                // having the two parents' genes mutate the new inherited gene depending on the compareValue
                gene = crossoverGeneMutate(geneValue, index, compareValue);
                //add gene to the child gene list
                childGeneList.add(gene);
            }
            
        }
    
        child = new Network(numberOfInputs, numberOfOutputs, childNodeList, childGeneList); //create new child neural network
        return child; //return newly created neural network
    }

    private float[] getAllNodeValues() {
        float[] values = new float[nodeList.size()]; //create an array with the size of number of nodes
 
        for (int i = 0; i < values.length; i++){ //run through number of nodes
            values[i] = getNodeByID(i).getNodeValue(); //set node values
        }
        return values; //return all nodes value array
    }
    
    private float[] getInputValues(){
        float[] values = new float[numberOfInputs]; //create an array with size of number of input nodes
 
        for (int i = 0; i < numberOfInputs; i++){ //run through number of inputs
            values[i] = getNodeByID(i).getNodeValue(); //set input nodes value
        }
 
        return values; //return input nodes value array
    }
    
    private float[] getHiddenValues(){
        int numberOfHiddens = nodeList.size() - (numberOfInputs + numberOfOutputs); //get number of hidden nodes that exist
        float[] values = new float[numberOfHiddens];  //create an array with size of number of hidden nodes
 
        for (int i = 0; i < numberOfHiddens; i++){  //run through number of hiddens
            values[i] = getNodeByID(i + numberOfInputs + numberOfOutputs).getNodeValue();  //set hidden nodes value
        }
 
        return values; //return hidden nodes value array
    }

    private int getInnovationNumber(int in, int out){
        Gene gene;
        
        for(int i = 0; i < geneHistory.size(); i++){
            gene = geneHistory.get(i);
            
            if(gene.getInID() == in && gene.getOutID() == out){
                return gene.getInnovation();                
            }
        }
        
        geneHistory.add(new Gene(innovationNumber, in , out, 0f, true));
        return innovationNumber++;
    }

    private void initilizeNodes() {
        nodeList = new ArrayList<Node>(); //create an empty node list
    
        Node node = null;
    
        for (int i = 0; i < numberOfInputs; i++) { //run through number of input perceptrons
    
        	//if this is the last input
            if(i == (numberOfInputs - 1)){
                node = new Node(i,Node.INPUT_BIAS_NODE); //make it a input bias type node  with index i as node ID
            }else{ //if this is not the last input
                node = new Node(i, Node.INPUT_NODE); //make it a input type node with index i as node ID
            }
            node.setLayer(0);
            
            nodeList.add(node); //add node to the node list
        }
    
        for (int i = numberOfInputs; i < numberOfInputs + numberOfOutputs; i++){  //run through number of output perceptrons
            node = new Node(i, Node.OUTPUT_NODE); //make it an output type node  with index i as node ID
            node.setLayer(1);
            
            nodeList.add(node); //add node to the node list
        }
    }

    private void initilizeGenes() {
        geneList = new ArrayList<Gene>(); //create an empty gene list
    
        for (int i = 0; i < numberOfInputs; i++){ //run through number of inputs
            for (int j = numberOfInputs; j < numberOfInputs + numberOfOutputs; j++){ //run through number of outputs
                
                // System.out.println(getNetID()+" -> "+"from node "+i+" to "+j+": ");
                Gene gene = new Gene(getInnovationNumber(i,j), i, j, (new Random().nextFloat()*2)-1, true); // create gene with default weight of 1.0 and and is active
                insertNewGene(gene); //insert gene to correct location in gene list
            }
        }
    }

    private void mutateAddConnection(){
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
            randNodeIndex1 = new Random().nextInt(pick1.size());    // pick any random node ID from 0 to nodeList.size - 1
            randomNodeID1 = pick1.get(randNodeIndex1);
            pick1.remove(randNodeIndex1);
            
            pick2 = new ArrayList<Integer>();
            
            // for each other node (not the randomNodeID1)
            for (int i = 0; i < nodeList.size(); i++) {
                if(i == randomNodeID1)continue;
                
                // if ID i is an input then skip
                if(getNodeByID(i).getNodeType() != Node.INPUT_NODE && getNodeByID(i).getNodeType() != Node.INPUT_BIAS_NODE){                
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
        
        int feedbackProbability = 10;   // 10% chance of feedback
        int rand = new Random().nextInt(101);
        if(rand <= feedbackProbability){
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
        
        Gene gene = new Gene(getInnovationNumber(randomNodeID1,randomNodeID2), randomNodeID1, randomNodeID2, 1f, true); //create gene which is enabled and 1 as default weight

        insertNewGene(gene); //add gene to the gene list
        
        
    }
    
    
    private void mutateAddNode(){
        int inID, newID, outID; //inID is old connections in node, outID is old connections out node, newID is the new node, and new innovation number for the connections
      
        float oldWeight; //weight from the old gene
  
        Gene oldGene = null; //find a random old gene
        
        if(hasActiveGene()){
            visited = new boolean[getNodeCount() + 1];
            
            while (true) { //run till found
                int randomGeneIndex = new Random().nextInt(geneList.size()); //pick random gene
                oldGene = geneList.get(randomGeneIndex); //get gene at random index
                if (oldGene.getGeneState()) //if gene is active
                    break;
            }
     
            oldGene.setGeneState(false); //disable this gene
            inID = oldGene.getInID(); //get in node ID
            outID = oldGene.getOutID(); //get out node ID
            oldWeight = oldGene.getWeight(); //get old weight
            
            Node newNode = new Node(getNodeCount(), Node.HIDDEN_NODE); //create new hidden node
            newID = newNode.getNodeID(); //get new node's ID
            
            // set layer value
            // update numNodesPerLayer
            
            /*
            if(getNodeByID(inID).getNodeType() == Node.OUTPUT_NODE){
                if(getNodeByID(inID).getLayer() == 1){
                    newNode.setLayer(1);    // set Layer of the new Node
                    nodeNumPerLayer[1]++;
                    setNumLayers(2);
                }else{
                    newNode.setLayer(getNodeByID(inID).getLayer() - 1);    // set Layer of the new Node
                    nodeNumPerLayer[newNode.getLayer() - 1]++;
                }
                
            }else{
            
            */
            newNode.setLayer(getNodeByID(inID).getLayer() + 1);    // set Layer of the new Node
            nodeNumPerLayer[newNode.getLayer()]++;
            // }
            
            nodeList.add(newNode); //add new node to the node list
            
            Gene newGene1 = new Gene(getInnovationNumber(inID, newID), inID, newID, 1f, true); //create new gene
            Gene newGene2 = new Gene(getInnovationNumber(newID, outID), newID, outID, oldWeight, true);  //create new gene
    
            //add genes to gene list
            insertNewGene(newGene1);
            insertNewGene(newGene2);

            // update layer values:
            
            /*
            System.out.println("Add Node Mutation:");
            System.out.println("Input ID: "+inID);
            System.out.println("Output ID: "+outID);
            System.out.println();
            */
            
            visited[inID] = true;
            visited[newID] = true;

            // propagate to forward nodes and update their layer values
            forwardPropagationLayerUpdate(outID, newNode.getLayer() + 1);
            
            System.out.println(" ????? "+getNumLayers());

            // set all output Nodes to maximum layer value:
            for(int i = numberOfInputs; i < numberOfInputs + numberOfOutputs; i++){ // iterate over all ouputs
                if(getNodeByID(i).getNodeType() == Node.OUTPUT_NODE){
                    nodeNumPerLayer[getNodeByID(i).getLayer()]--;
                    getNodeByID(i).setLayer(getNumLayers() - 1);
                    nodeNumPerLayer[getNodeByID(i).getLayer()]++;
                }
            }
            
            // due to the way the forward propagation is design there might occur lapses in layer, so we correct that here:
            // first and second delimite the zero sequence
            int first = -1;
            int second = -1;
            for (int i = 0; i < getNumLayers(); i++){
                if(nodeNumPerLayer[i] == 0){
                    if(first == -1){
                        // at first occurence set the bondaries to this index
                        first = i;
                        second = i;
                    }else{
                        // as we get more occurences of zeros drag the second bondary index
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
            
            
        }
    }
    
    private void mutateWeight() {	
    	// 90% chance of changing weight by a 50% to 150% factor
    	// 10% of randomly changing the weight
    	
        int numberOfGenes = geneList.size(); //number of genes
 
        for (int i = 0; i < numberOfGenes; i++){ //run through all genes
            Gene gene = geneList.get(i); // get gene at index i
            float weight = 0;
            int randomNumber;    //random number between 0 and 100
            
            /*
            randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= 1) { // 1% chance
                //flip sign of weight
                weight = -gene.getWeight();
                gene.setWeight(weight);
            }
            */
            
            randomNumber = new Random().nextInt(100) + 1;
            
            if (randomNumber <= 10) { // 10 % chance
                //System.out.println("Net "+getNetID()+" mutated weight in gene "+getGeneByID(i).getInnovation()+" randomly.");
                //pick random weight between -1 and 1
                weight = (new Random().nextFloat()*2) - 1;
                gene.setWeight(weight);
            }
            
            if (randomNumber > 10) { // 90% chance
                
                //randomly change weight from 50% to 150%
                
                // try change using gaussian function
                // float number = (float)Math.tanh(new Random().nextGaussian()) + 1f;
                
                float factor = new Random().nextFloat()*(3f/2f)+0.5f;   // random factor between 1/2 and 2 (half <-> double)
                weight = gene.getWeight() * factor;
                //System.out.println("Net "+getNetID()+" mutated weight in gene "+getGeneByID(i).getInnovation()+" uniformly ("+factor+").");
                gene.setWeight(weight);
            }
            
            /*
            randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= 5) { // 5% chance
                //flip activation state for gene
                gene.setGeneState(!gene.getGeneState());
            }
            */
            
        }
 
    }
    
    private boolean hasActiveGene(){
        boolean ok = false;
        for (int i = 0; i < geneList.size(); i++) {
            ok |= geneList.get(i).getGeneState(); //  same as: ok = ok || geneList...
        }
        return ok;
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

        if(getNumLayers() < layer + 1) setNumLayers(layer + 1);
        
        // if node is input return
        if(getNodeByID(id).getNodeType() == Node.INPUT_NODE || 
        getNodeByID(id).getNodeType() == Node.INPUT_BIAS_NODE){
            return;
        }
        
        if(visited[id]){return;}
        
        visited[id] = true;

        // update number of nodes per layer:
        /*
        System.out.println("Updating "+id+":");
        System.out.println("old num in layer "+getNodeByID(id).getLayer()+": "+nodeNumPerLayer[getNodeByID(id).getLayer()]);
        System.out.println("old num in layer "+layer+": "+nodeNumPerLayer[layer]);
        System.out.println();
        */
        
        // update node layer
        if(getNodeByID(id).getLayer() < layer){
            nodeNumPerLayer[getNodeByID(id).getLayer()]--;
            nodeNumPerLayer[layer]++;
            
            getNodeByID(id).setLayer(layer);
            // System.out.println(" Node "+id+" updated its layer to "+layer);
        }
        
        // if the current node is an output then no need for further propagation
        if(getNodeByID(id).getNodeType() == Node.OUTPUT_NODE){
            // System.out.println(" In output. Going to return.");
            return;
        }
        
        // not the most efficient way to do this but it saves memory. also each NN has very few genes (from ~10 to ~100)
        // if there is low performance try this: for every Node object keep a List of all the other Nodes it connects to and just iterate that List
        for (int i = 0; i < getGeneCount(); i++) { 
            if(getGeneByID(i).getInID() != id)continue;
            
            forwardPropagationLayerUpdate(getGeneByID(i).getOutID(), layer + 1);
            
            // System.out.println(geneList.get(i).getOutID());
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
    
    private static Gene crossoverGeneMutate(Gene[] copyGene, int index, int compareValue) {
        Gene gene;
        int randomNumber;
        
        if(index == -1){
            randomNumber = new Random().nextInt(100) + 1;
            
            // 40% chance that the weight of the new node is the average of the parents' nodes
            // 60% chance that the node is randomly picked from either parent
            if(randomNumber <= 40){
                gene = new Gene(copyGene[0]);
                gene.setWeight((copyGene[0].getWeight() + copyGene[1].getWeight())/2f);
            }else{
                // randomly select one gene from either parent
                randomNumber = new Random().nextInt(2);
                gene = new Gene(copyGene[randomNumber]);
            }
        }else{
            gene = new Gene(copyGene[index]);
        }
        
        System.out.println("Gene "+gene.getInnovation()+":");
        
        // compareValue may change the activation state of the gene
        switch (compareValue) {
        
        // gene exists in both parents && is active in both parents
        case 0:
            System.out.println("Gene exists in both parents && is active in both parents");
            randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= 5) {
                System.out.println("  Set to false");
                gene.setGeneState(false);
            }
            break;
        
        // gene exists in both parents && is inactive in both parents
        case 1:
            System.out.println("Gene exists in both parents && is inactive in both parents");
            randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= 5) {
                System.out.println("  Set to true");
                gene.setGeneState(true);
            }
            break;
           
        // gene exists in both parents && is active only in one
        case 2:
            System.out.println("Gene exists in both parents && is active only in one");
            randomNumber = new Random().nextInt(100) + 1;
            // 75% change of setting the gene state to false
            if (randomNumber <= 75) {
                System.out.println("  Set to false");
                gene.setGeneState(false);
            }else{
                System.out.println("  Set to true");
                gene.setGeneState(true);
            }
            
            break;
        
        // gene only exists in one parent && is active
        case 3:
            System.out.println("Gene only exists in one parent && is active");
            randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= 5) {
                System.out.println("  Set to false");
                gene.setGeneState(false);
            }
            break;
            
        // gene only exists in one parent && is inactive
        case 4:
            System.out.println("Gene only exists in one parent && is inactive");
            randomNumber = new Random().nextInt(100) + 1;
            if (randomNumber <= 5) {
                System.out.println("  Set to true");
                gene.setGeneState(true);
            }
            break;
        }
 
        return gene; //return new gene
    }

}