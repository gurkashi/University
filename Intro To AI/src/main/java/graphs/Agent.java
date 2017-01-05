package graphs;

public class Agent extends GraphElement {
    final Vertex start;
    final Vertex goal;

    public Agent(String id, Graph context, String start, String goal){

        super(id, context, id);

        this.goal = context.getVertex(goal);
        this.start = context.getVertex(start);
    }
}
