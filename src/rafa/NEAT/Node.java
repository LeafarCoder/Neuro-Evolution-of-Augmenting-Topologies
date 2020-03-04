package rafa.NEAT;

import java.io.Serializable;
import java.util.*;

public class Node implements Serializable{
	
	public final static int INPUT_NODE = 0;
    public final static int INPUT_BIAS_NODE = 1;
    public final static int HIDDEN_NODE = 2;
    public final static int OUTPUT_NODE = 3;
	
    private int ID;	// 0-indexed
    private int layer;
    private int type;
    private double value;
    private List<Gene> forwardGenes = new ArrayList<Gene>(); // saves genes that leave the node (forward)
    
    // create a copy of the node
    public Node(Node copy){
        ID = copy.ID;
        type = copy.type;
 
        // if this is the bias node set it to 1, else reset value to 0
        value = (copy.type == INPUT_BIAS_NODE) ? 1f : 0f;
    }

	public Node(int id_, int type_){
		ID=id_;
		type=type_;
		value = (type_==INPUT_BIAS_NODE) ? 1f : 0f;
	}
	
	public Node(int ID, int type, int layer){
		this.ID=ID;
		this.type=type;
		value = (type==INPUT_BIAS_NODE) ? 1f : 0f;
		this.layer = layer;
	}
	
	public void addSignal(double signal){
		value += signal;
	}
	
	public int getNodeID(){
		return ID;
	}
	
	public int getLayer(){
		return layer;
	}
	
	public int getNodeType(){
		return type;
	}
	
	public double getNodeValue(){
		return value;
	}
	
	public List<Gene> getForwardGenes(){
		return forwardGenes;
	}
	
	public void addForwardGene(Gene new_gene){
		forwardGenes.add(new_gene);
	}

	public void clearForwardGenes(){
		forwardGenes.clear();
	}
	
	public void setLayer(int layer){
		this.layer = layer;
	}
	
	public void setNodeValue(double v){
		if(type != INPUT_BIAS_NODE)value=v;
	}
	
	public void activation(){
		value = (double)Math.tanh(value);
	}
}




