package Algorithms;

import agents.AbstractAgent;
import graphs.Edge;
import graphs.Graph;
import graphs.Vertex;

public class IgnoreBlockedVerticesDijkstra extends Dijkstra{
    private final AbstractAgent agent;

    public IgnoreBlockedVerticesDijkstra(Graph graph, Vertex source, AbstractAgent agent) {
        super(graph, source);
        this.agent = agent;
    }

    @Override
    public boolean shouldUseVertex(Vertex vertex) {
        return agent.getKeys().containsAll(vertex.getLocks());
    }
}
