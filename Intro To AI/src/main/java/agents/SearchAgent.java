package agents;

import graphs.Graph;
import graphs.Vertex;

/**
 * Created by Gur on 11/24/2016.
 */
public class SearchAgent extends AbstractAgent {
    protected SearchAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove() {
        return null;
    }
}
