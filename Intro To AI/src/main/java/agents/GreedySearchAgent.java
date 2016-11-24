package agents;

import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Vertex;

import java.util.Comparator;

/**
 * Created by Gur on 11/24/2016.
 */
public class GreedySearchAgent extends AbstractAgent {
    public GreedySearchAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove() {
        return heuristicEvaluationFunction();
    }

    protected Vertex heuristicEvaluationFunction(){
        return Queriable.create(Vertex.class)
                .sortBy(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex o1, Vertex o2) {
                        double sum1 = 0.0;
                        for (Vertex v : o1.getNeigbores()){
                            if (v != current){
                                sum1 += o1.getEdge(v).getWeight();
                            }
                        }

                        double sum2 = 0.0;
                        for (Vertex v : o2.getNeigbores()){
                            if (v != current){
                                sum2 += o2.getEdge(v).getWeight();
                            }
                        }

                        return sum1 < sum2? -1 : 1;
                    }
                })
                .firstOrNull()
                .execute(current.getNeigbores());
    }
}