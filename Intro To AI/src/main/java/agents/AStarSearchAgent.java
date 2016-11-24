package agents;

import Algorithms.AStar;
import com.gurkashi.fj.lambdas.Selector;
import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Vertex;

/**
 * Created by Gur on 11/24/2016.
 */
public class AStarSearchAgent extends GreedySearchAgent {
    public AStarSearchAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove(){
        AStarSearchAgentAlgorithm algorithm = new AStarSearchAgentAlgorithm(current, goal);

        algorithm.execute();

        if (!algorithm.isSuccess()){
            return null;
        }

        int index = algorithm.getTotalPath().indexOf(current);

        return algorithm.getTotalPath().get(index + 1);
    }

    private class AStarSearchAgentAlgorithm extends AStar{
        protected AStarSearchAgentAlgorithm(Vertex start, Vertex goal) {
            super(start, goal);
        }

        @Override
        protected Double heuristicCostEstimate(final Vertex start, Vertex goal) {
            if (start == goal) return 0.0;

            return Queriable.create(Vertex.class)
                    .map(new Selector<Vertex, Double>() {
                        @Override
                        public Double select(Vertex vertex) {
                            double sum = 0;
                            for (Vertex v : vertex.getNeigbores()){
                                if (v != start){
                                     sum += vertex.getEdge(v).getWeight();
                                }
                            }
                            return sum / vertex.getNeigbores().size();
                        }
                    })
                    .firstOrNull()
                    .execute(start.getNeigbores());
        }
    }
}