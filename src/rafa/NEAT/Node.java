package rafa.NEAT;

import java.util.*;

public class Node{
	
	public final static int INPUT_NODE = 0;
    public final static int INPUT_BIAS_NODE = 1;
    public final static int HIDDEN_NODE = 2;
    public final static int OUTPUT_NODE = 3;
	
    private int ID;
    private int layer;
    private int type;
    private float value;
    
    // create a copy of the node
    public Node(Node copy){
        ID = copy.ID;
        type = copy.type;
 
        // if this is the bias node set it to 1, else reset value to 0
        value = copy.type == INPUT_BIAS_NODE ? 1f : 0f;
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
	
	public int getNodeID(){
		return ID;
	}
	
	public int getLayer(){
		return layer;
	}
	
	public int getNodeType(){
		return type;
	}
	
	public float getNodeValue(){
		return value;
	}
	
	public void setLayer(int layer){
		this.layer = layer;
	}
	
	public void setNodeValue(float v){
		if(type != INPUT_BIAS_NODE)value=v;
	}
	
	public void activation(){
		value = (float)Math.tanh(value);		
	}
}




