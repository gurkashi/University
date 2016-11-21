package agents;

import Algorithms.Dijkstra;
import Algorithms.IgnoreBlockedVerticesDijkstra;
import Algorithms.IgnoreWeightDijkstra;
import com.gurkashi.fj.lambdas.Predicate;
import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Vertex;

import java.util.Comparator;
import java.util.Map;

public class GreedyKeyCollectorAgent extends GreedyAgent{
    public GreedyKeyCollectorAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove() {
        if (graphHasNoKeys()){
            return super.calculateNextMove();
        }

        final Dijkstra dijkstra = new IgnoreWeightDijkstra(this, getContext(), current);
        dijkstra.execute();

        Vertex nearestKey = Queriable.create(Vertex.class)
                .filter(new Predicate<Vertex>() {
                    @Override
                    public boolean predict(Vertex vertex) {
                        return vertex.getKeys().size() > 0;
                    }
                })
                .sortBy(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex v1, Vertex v2) {
                        return dijkstra.getDistance().get(v1) - dijkstra.getDistance().get(v2);
                    }
                })
                .firstOrNull()
                .execute(getContext().getVertices());

        if (nearestKey == null){
            return null;
        }
        else {
            return dijkstra.getNextMove(current, nearestKey);
        }
    }

    private boolean graphHasNoKeys() {
        return Queriable.create(Vertex.class)
                .filter(new Predicate<Vertex>() {
                    @Override
                    public boolean predict(Vertex vertex) {
                        return vertex.getKeys().size() > 0;
                    }
                })
                .count()
                .execute(getContext().getVertices()) == 0;
    }
}