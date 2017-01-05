package agents.ass2;

import com.gurkashi.fj.lambdas.Selector;
import com.gurkashi.fj.queries.collections.GroupBy;
import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Vertex;

import java.util.Comparator;

/*
* in this version, the vertex is cleared of resources after the agent has visited inside
* */
public class ZeroSumGameAgent extends MinimaxAgent{

    public ZeroSumGameAgent(String id, Vertex initial, Vertex goal, Graph world, int depth) {
        super(id, initial, goal, world, depth);
    }

    @Override
    public Vertex calculateNextMove() {
        if (getCurrent() == getGoal()){
            return null;
        }

        return Queriable.create(Vertex.class)
                .groupBy(new Selector<Vertex, Double>() {
                    @Override
                    public Double select(Vertex vertex) {
                        return alphaBeta(vertex, depth, Double.MIN_VALUE, Double.MAX_VALUE, true);
                    }
                })
                .sortBy(new Comparator<GroupBy.Group<Double, Vertex>>() {
                    @Override
                    public int compare(GroupBy.Group<Double, Vertex> o1, GroupBy.Group<Double, Vertex> o2) {
                        return o1.getBy() <= o2.getBy()? -1 : 1;
                    }
                })
                .map(new Selector<GroupBy.Group<Double,Vertex>, Vertex>() {
                    @Override
                    public Vertex select(GroupBy.Group<Double, Vertex> vertexAlphaBetaValue) {
                        return vertexAlphaBetaValue.getGroup().iterator().next();
                    }
                })
                .first().execute(getCurrent().getNeigbores());
    }

    @Override
    protected int getHeuristicValue(Vertex vertex, boolean isMaximizingAgent){
        return isMaximizingAgent? super.getHeuristicValue(vertex, isMaximizingAgent): (-1) * super.getHeuristicValue(vertex, isMaximizingAgent);
    }
}