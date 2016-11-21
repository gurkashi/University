package agents;

import Algorithms.Dijkstra;
import Algorithms.IgnoreBlockedVerticesDijkstra;
import graphs.Graph;
import graphs.Vertex;

import java.util.Map;

public class GreedyAgent extends AbstractAgent{
    public GreedyAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove() {
        Dijkstra dijkstra = new IgnoreBlockedVerticesDijkstra(getContext(), current, this);
        dijkstra.execute();

        return dijkstra.getNextMove(current, goal);
    }
}
