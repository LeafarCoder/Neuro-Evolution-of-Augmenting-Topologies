package rafa.NEAT;

public class Gene{
	 
    public final int GENE_INFORMATION_SIZE = 4;
 
    private int innovation;
    private int inID;
    private int outID;
    private float weight;
    private boolean on;

    public Gene(Gene copy){
        //copy all the properties of the given gene
        innovation = copy.innovation;
        inID = copy.inID;
        outID = copy.outID;
        weight = copy.weight;
        on = copy.on;
    }
 
    public Gene(int inovation_, int inID_, int outID_, float weight_, boolean on_){
        innovation = inovation_;
        inID = inID_;
        outID = outID_;
        weight = weight_;
        on = on_;
    }
 
    public int getInID() {
        return inID;
    }

    public int getOutID() {
        return outID;
    }

    public int getInnovation(){
        return innovation;
    }
 
    public float getWeight(){
        return weight;
    }

    public boolean getGeneState() {
        return on;
    }

    public void setGeneState(boolean on_) {
        on = on_;
    }

    public void setWeight(float weight_) {
        weight = weight_;
    }
 
    public String getGeneString() {
        String gene = String.format("%2s", inID) + " -> " + String.format("%2s", outID) + " (weight: " + String.format("%4s",weight) + ", active: "+(on?'1':'0')+")";
 
        return gene;
    }

    public boolean equals(Gene other) {
        if (other == null)return false;
 
        if (inID == other.inID && outID == other.outID)return true;
 
        return false;
    }
}
