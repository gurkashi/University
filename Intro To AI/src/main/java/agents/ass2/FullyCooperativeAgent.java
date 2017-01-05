package agents.ass2;

import Algorithms.Dijkstra;
import agents.AbstractAgent;
import graphs.Graph;
import graphs.Vertex;

public class FullyCooperativeAgent extends AbstractAgent {
    protected FullyCooperativeAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove() {
        if (getCurrent() == getGoal()){
            return null;
        }

        Dijkstra dijkstra = new Dijkstra(getContext(), getCurrent());

        dijkstra.execute();

        return dijkstra.getNextMove(getCurrent(), getGoal());
    }
}
