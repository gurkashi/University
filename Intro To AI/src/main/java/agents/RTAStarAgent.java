package agents;

import graphs.Graph;
import graphs.Vertex;

/**
 * Created by Gur on 11/24/2016.
 */
public class RTAStarAgent extends AStarSearchAgent {
    final int n;
    int counter;

    public RTAStarAgent(String id, Vertex initial, Vertex goal, Graph world, int n) {
        super(id, initial, goal, world);

        this.n = 0;
        this.counter = 0;
    }

    @Override
    public Vertex calculateNextMove(){
        // TODO: add implementation here
        return super.calculateNextMove();
    }
}
