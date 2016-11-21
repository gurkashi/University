package Algorithms;

import agents.AbstractAgent;
import graphs.Edge;
import graphs.Graph;
import graphs.Vertex;

public class IgnoreWeightDijkstra extends Dijkstra{
    private final AbstractAgent agent;

    public IgnoreWeightDijkstra(AbstractAgent agent, Graph graph, Vertex source) {
        super(graph, source);

        this.agent = agent;
    }

    @Override
    public int getWeight(Edge edge){ return 1; }

    @Override
    public boolean shouldUseVertex(Vertex vertex) {
        return agent.getKeys().containsAll(vertex.getLocks());
    }
}

