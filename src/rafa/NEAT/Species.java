package rafa.NEAT;

import java.io.Serializable;
import java.util.*;

public class Species implements Serializable{
	// Species ID: 0,1,2,3,...

	private static final long serialVersionUID = 1L;
	
	private Population parentPopulation;
	
	// species is a collection of species.
	// The first network of each species is the Network representative (used to compare)
	private Hashtable<Integer, List<Network>> species = new Hashtable<Integer, List<Network>>();
	private int lastSpeciesID = 0;
	private static double speciationCompatibilityThreshold;	//threshold value that determines whether 2 neural networks belong to the same species
	private static double speciationDisjointCoefficient; //coefficient effect of disjoint genes  
	private static double speciationExcessCoefficient; //coefficient effect of  excess genes
	private static double speciationWeightDifferenceCoefficient; //coefficient effect of average weight difference between equal genes
	private static double speciationCompatibilityModifier; //modifies the deltaThreshold
	private static double speciationSurvivalThreshold; // Percentage of survival for networks within a species (only the best nets survive)
	
	private static int speciationSpeciesSize;	// sets the target for the number of species allowing to control the deltaThreshold
	
	public Species(Population parentPop){
		
		parentPopulation = parentPop;
		
		Species.speciationSpeciesSize = (int) Population.speciationSpeciesSize;
		Species.speciationSurvivalThreshold = Population.speciationSurvivalThreshold;
		Species.speciationDisjointCoefficient = Population.speciationDisjointCoefficient;
		Species.speciationExcessCoefficient = Population.speciationExcessCoefficient;
		Species.speciationWeightDifferenceCoefficient = Population.speciationWeightDifferenceCoefficient;
		Species.speciationCompatibilityThreshold = Population.speciationCompatibilityThreshold;
		Species.speciationCompatibilityModifier = Population.speciationCompatibilityModifier;
	}
	
	public void killUnfitNetworks(){

		// iterate over all Species to kill the unfitted networks
		// (also erase them from the population)
		Comparator<Network> comparator = new NetworkComparatorWorstFirst();
		
		Enumeration<Integer> speciesIDs = getSpeciesIDs();
		while(speciesIDs.hasMoreElements()){
			int specie_id = speciesIDs.nextElement();
			System.out.println(" from species " + specie_id);
			List<Network> specie_nets = Arrays.asList(getNetworksFromSpecie(specie_id));
			Collections.sort(specie_nets, comparator);
			
			System.out.println(" >>> ABOUT TO DELETE <<< ");
			for(int i = 0; i < specie_nets.size(); i++){
				Network net = specie_nets.get(i);
				System.out.println(net.getNetID() + " --> " + net.getNetFitness());
			}
			System.out.println(" >>> DONE PRESENTING FITNESSES <<< ");
			 System.out.println(" --> kill " + (int)(specie_nets.size() * (1 - speciationSurvivalThreshold)));
			// kill worst networks
			int kill_num = (int)(specie_nets.size() * (1 - speciationSurvivalThreshold));
			for(int i = 0; i < kill_num; i++){
				Network net = specie_nets.get(i);
				
				// remove from species
				removeFromSpecie(specie_id, net);
				
				// remove from population
				parentPopulation.removeNetwork(net);

			}
		}
	}
	
	public void adjustDeltaThreshold(){
		if(species.size() < speciationSpeciesSize){
			speciationCompatibilityThreshold -= speciationCompatibilityModifier;
			System.out.println(" - threshold");
		}else{
			speciationCompatibilityThreshold += speciationCompatibilityModifier;
			System.out.println(" + threshold");
		}
		
		if(speciationCompatibilityThreshold < 0.3f){
			speciationCompatibilityThreshold = 0.3f;
		}
		
		System.out.println(speciationCompatibilityThreshold);
	}
	
	// get available Species' IDs
	public Enumeration<Integer> getSpeciesIDs(){
		return species.keys();
	}
	
	public int getNumOfSpecies(){
		return species.size();
	}
	
	public Network getSpecieRepresentative(int id){
		// representative Network is always the first in each species.
		return species.get(id).get(0);
	}
	
	public Network[] getNetworksFromSpecie(int id){
		int sp_size = getSpecieSize(id);
		Network[] nets = new Network[sp_size];
		
		for(int i = 0; i < sp_size; i++){
			nets[i] = species.get(id).get(i);
		}
		
		return nets;
	}
	
	public int getSpecieSize(int id){
		return species.get(id).size();
	}

	public void setDeltaThreshold(double deltaThreshold_) {
		speciationCompatibilityThreshold = deltaThreshold_;
    }
	
	public void addSpecie(Network representativeNet){
		List<Network> new_specie = new ArrayList<Network>();
		new_specie.add(representativeNet);
		species.put(lastSpeciesID++,new_specie);
		representativeNet.setSpecieID(lastSpeciesID - 1);

		/*
		System.out.println(
			"src::NEAT::Species::addSpecie:\n\t"
			+ "Adding the Network "+representativeNet.getNetID()+" to the new Specie "+(lastSpeciesID - 1)+".\n\t"
			+ "Number of species: "+species.size()+"\n");
		 */
	}

	public void addToSpecie(int id, Network net){
		if(species.containsKey(id)){
			species.get(id).add(net);
			net.setSpecieID(id);
		}else{
			List<Network> new_specie = new ArrayList<Network>();
			new_specie.add(net);
			species.put(lastSpeciesID,new_specie);
			net.setSpecieID(lastSpeciesID);
			lastSpeciesID++;
		}
	}
	
	public void removeFromSpecie(int id, Network net){
		species.get(id).remove(net);
		
		if(species.get(id).isEmpty())species.remove(id);
	}
	
	public void resetSpeciesControlID(){
		lastSpeciesID = 0;
	}
	
	public void removeSpecie(int id){
		species.remove(id);
	}
	
	public static boolean sameSpecies(Network net1, Network net2) {
		float similarity = getSimilarity(net1, net2);
        return similarity <= speciationCompatibilityThreshold; //return boolean compare value
    }
	
	public static float getSimilarity(Network net1, Network net2){

        Hashtable<Integer, Gene[]> geneHash = new Hashtable<Integer, Gene[]>(); //hash table to be used to compared genes from the two networks
        
        Gene[] geneValue; //will be used to check whether a gene exists in both networks
 
        List<Gene> geneList1 = net1.getGeneList(); //get first network
        List<Gene> geneList2 = net2.getGeneList(); //get second network
 
        Enumeration<Integer> keysEnum; //will be used to get keys from gene hash
        int[] keys; //will be used to get keys array from Enumeration
 
        int numberOfGenes1 = geneList1.size(); //get number of genes in network 1
        int numberOfGenes2 = geneList2.size(); //get number of genes in network 2
        
        int largerGenomeSize = numberOfGenes1 > numberOfGenes2 ? numberOfGenes1 : numberOfGenes2; //get one that is larger between the 2 network
        int excessGenes = 0; //number of excess genes (genes that do match and are outside the innovation number of the other network)
        int disjointGenes = 0; //number of disjoint gene (genes that do not match in the two networks)
        int equalGenes = 0; //number of genes both neural network have

        float similarity = 0; //similarity of the two networks
        double averageWeightDifference = 0; //average weight difference of the two network's equal genes
 
        boolean foundAllExcess = false; //if all excess genes are found
        boolean isFirstGeneExcess = false; //if net 1 contains the excess genes
 
        for (int i = 0; i < numberOfGenes1; i++) { //run through net 1's genes
        	geneHash.put(geneList1.get(i).getInnovation(), new Gene[] {geneList1.get(i), null});  //add into the hash with innovation number as the key and gene array of size 2 as value
        }
 
        for (int i = 0; i < numberOfGenes2; i++) { //run through net 2's genes
            int innovation = geneList2.get(i).getInnovation(); //get innovation number of gene
 
            if (geneHash.containsKey(innovation)) { //if innovation key does not exist
            	geneValue = (Gene[]) geneHash.get(innovation); //get value
                geneValue[1] = geneList2.get(i); //add into second position net 2's gene 
                geneHash.remove(innovation);
                geneHash.put(innovation, geneValue);
            }
            else { //key exists
                geneHash.put(innovation, new Gene[] {null, geneList2.get(i)}); //add into  the hash with innovation number as the key and gene array of size 2 as value
            }
 
        }
      
        
        keysEnum = geneHash.keys(); //get all keys in the hash
        // Enumeration to Integer Array
        keys = new int[geneHash.size()]; //int array with size of number of keys in the hash
        int cnt=0;
        while (keysEnum.hasMoreElements()) {
        	keys[cnt++]=(int)(keysEnum.nextElement());
        }
        Arrays.sort(keys); //order keys in ascending order
     
 
        for (int i = keys.length-1; i >= 0; i--) { //run through all keys backwards (to get all excess gene's first)
            geneValue = (Gene[])geneHash.get(keys[i]); //get value with key
 
            if (!foundAllExcess) { //if all excess genes have not been found
                if (i == keys.length - 1 && geneValue[1] == null) { //this is the first itteration and second gene location is null
                    isFirstGeneExcess = true; //excess genes exit in net 1
                }
 
                if (isFirstGeneExcess && geneValue[1] == null) { //excess gene exist in net 1 and there is no gene in second location of the value
                    excessGenes++; //this is an excess gene and increment excess gene
                }
                else if (!isFirstGeneExcess && geneValue[0] == null) { //excess gene exist in net 2 and there is no gene in first location of the value
                    excessGenes++; //this is an excess gene and increment excess gene
                }
                else { //no excess genes
                    foundAllExcess = true; //all excess genes are found
                    i++;	// re-check this gene
                }
 
            }else{ //if all excess genes are found
                if (geneValue[0] != null && geneValue[1] != null) { //both gene location are not null
                    equalGenes++; //increment equal genes
                    averageWeightDifference += Math.abs(geneValue[0].getWeight() - geneValue[1].getWeight()); //add absolute difference between 2 weight
                }
                else { //this is disjoint gene
                    disjointGenes++; //increment disjoint
                }
            }
        }
 
        averageWeightDifference /= (double)equalGenes; //get average weight difference of equal genes
 
        // https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume21/stanley04a-html/node3.html
        similarity = (float) ((averageWeightDifference * speciationWeightDifferenceCoefficient) + //calculate weight difference disparity
                     ((disjointGenes * speciationDisjointCoefficient) / (float)largerGenomeSize) +  //calculate disjoint disparity
                     ((excessGenes * speciationExcessCoefficient) / (float)largerGenomeSize)); //calculate excess disparity
 
        return similarity;
	}
 
	private class NetworkComparatorWorstFirst implements Comparator<Network>{
        // Overriding compare()method of Comparator 
		public int compare(Network net1, Network net2) {
			// System.out.println(net1 + " " + net2);
			if(net1.getNetFitness() < net2.getNetFitness()){
				return -1;
			}else if(net1.getNetFitness() > net2.getNetFitness()){
				return 1;
			}else{
				return 0;
			}
		}
    }
	
	
}

