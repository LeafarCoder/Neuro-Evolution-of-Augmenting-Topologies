package rafa.NEAT;

import java.util.*;

public class Species {
	
	private int numOfSpecies = 0;
	
	private List<Network> specieRepresentative = new ArrayList<Network>();
	private List<Integer> specieSize = new ArrayList<Integer>();
	
	private static float deltaThreshold = 0.3f;	//threshold value that determines whether 2 neural networks belong to the same species
	private static float disjointCoefficient = 1; //coefficient effect of disjoint genes  
	private static float excessCoefficient = 1; //coefficient effect of  excess genes
	private static float weightCoefficient = 0.4f; //coefficient effect of average weight difference between equal genes
	
	
	public int getNumOfSpecies(){
		return numOfSpecies;
	}
	
	public Network getSpecieRepresentative(int id){
		return specieRepresentative.get(id);
	}
	
	public int getSpecieSize(int id){
		return specieSize.get(id);
	}
	
	public void incrementSpecieSize(int ID){
		specieSize.set(ID, specieSize.get(ID) + 1);
	}
	
	public void setDeltaThreshold(float deltaThreshold_) {
        deltaThreshold = deltaThreshold_;
    }
	
	public void setSpeciationParameters(float coefDisjoint, float coefExcess, float coefWeights, float threshold){
		disjointCoefficient = coefDisjoint;
		disjointCoefficient = coefExcess;
		weightCoefficient = coefWeights;
		deltaThreshold = threshold;
	}
	
	public void addSpecie(Network representativeNet){
		specieRepresentative.add(representativeNet);
		specieSize.add(1);
		numOfSpecies++;
		
		System.out.println("Adding a new specie ("+numOfSpecies+") with representative "+representativeNet.getNetID());
	}
	
	public void removeSpecie(int id){
		specieRepresentative.remove(id);
		specieSize.remove(id);
		numOfSpecies--;
	}
	
	public static boolean sameSpecies(Network net1, Network net2) {
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
        float averageWeightDifference = 0; //average weight difference of the two network's equal genes
 
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
 
        averageWeightDifference /= (float)equalGenes; //get average weight difference of equal genes
 
        // https://www.cs.cmu.edu/afs/cs/project/jair/pub/volume21/stanley04a-html/node3.html
        similarity = (averageWeightDifference * weightCoefficient) + //calculate weight difference disparity
                     (((float)disjointGenes * disjointCoefficient) / (float)largerGenomeSize) +  //calculate disjoint disparity
                     (((float)excessGenes * excessCoefficient) / (float)largerGenomeSize); //calculate excess disparity
 
        //if similairty is <= to threshold then return true, otherwise false
        return similarity <= deltaThreshold; //return boolean compare value
    }
    
}

