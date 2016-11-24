package Algorithms;

import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Vertex;

import java.util.*;

/**
 * Created by Gur on 11/24/2016.
 */
public abstract class AStar implements Algorithm {

    private boolean success = false;
    private final Vertex start;
    private final Vertex goal;

    protected AStar(Vertex start, Vertex goal){
        this.graph = start.getContext();
        this.totalPath = new ArrayList<>();
        this.start = start;
        this.goal = goal;
    }

    final Graph graph;
    final ArrayList<Vertex> totalPath;

    @Override
    public void execute() {
        doAStar(start, goal);
    }

    private void doAStar(Vertex start, Vertex goal){
        // The set of nodes already evaluated.
        Set<Vertex> closedSet = new HashSet<>();

        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start node is known.
        Set<Vertex> openSet = new HashSet<>();
        openSet.add(start);

        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually contain the
        // most efficient previous step.
        Map<Vertex, Vertex> cameFrom = new HashMap<>();

        // For each node, the cost of getting from the start node to that node.
        // map with default value of Infinity
        Map<Vertex, Double> gScore = new HashMap<>();
        for(Vertex v: graph.getVertices()){
            gScore.put(v, Double.MAX_VALUE);
        }

        // The cost of going from start to start is zero.
        gScore.put(start, 0.0);

        // For each node, the total cost of getting from the start node to the goal
        // by passing by that node. That value is partly known, partly heuristic.
        // map with default value of Infinity
        final Map<Vertex, Double> fScore = new HashMap();
        for (Vertex v: graph.getVertices()){
            fScore.put(v, Double.MAX_VALUE);
        }

        // For the first node, that value is completely heuristic.
        fScore.put(start, heuristicCostEstimate(start, goal));

        while (!openSet.isEmpty()){

            // the node in openSet having the lowest fScore[] value
            Vertex current = Queriable.create(Vertex.class)
            .min(new Comparator<Vertex>() {
                @Override
                public int compare(Vertex v1, Vertex v2) {
                    return fScore.get(v1) - fScore.get(v1) < 0.0? -1 : 1;
                }
            })
            .execute(openSet);

            if (current == goal) {
                reconstructPath(cameFrom, current);
                return;
            }

            openSet.remove(current);
            closedSet.add(current);

            for (Vertex neighbor : current.getNeigbores()){
                // Ignore the neighbor which is already evaluated.
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                // The distance from start to a neighbor
                double tentativeGScore = gScore.get(current) + current.getEdge(neighbor).getWeight();

                // Discover a new node
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
                // This is not a better path.
                else if (tentativeGScore >= gScore.get(neighbor)) {
                    continue;
                }

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tentativeGScore);
                fScore.put(neighbor, gScore.get(neighbor) + heuristicCostEstimate(neighbor, goal));
            }
        }
    }

    private void reconstructPath(Map<Vertex, Vertex> cameFrom, Vertex current) {
        totalPath.add(current);

        while (cameFrom.keySet().contains(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }

        success = true;
    }

    public boolean isSuccess() { return success; }

    public List<Vertex> getTotalPath() { return totalPath; }

    protected abstract Double heuristicCostEstimate(Vertex start, Vertex goal);
}
