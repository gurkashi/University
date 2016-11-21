package graphs;

public class Edge extends GraphElement {
    double weight;
    Vertex from;
    Vertex to;

    public Edge(String id, Graph context, double weight, Vertex from, Vertex to){
        super(id, context);

        this.weight = weight;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString(){
        return super.toString() + ", " + from.id + "->" + to.id + ", w: " + weight;
    }

    public double getWeight(){
        return this.weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    public Vertex getFrom(){
        return this.from;
    }

    public Vertex getTo(){
        return this.to;
    }
}
