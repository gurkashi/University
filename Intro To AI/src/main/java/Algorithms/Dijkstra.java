package Algorithms;

import com.gurkashi.fj.queries.stracture.ExecutionChain;
import com.gurkashi.fj.queries.stracture.Queriable;
import com.gurkashi.fj.queries.stracture.ScalarQuery;
import graphs.Edge;
import graphs.Graph;
import graphs.Vertex;

import java.util.*;

/**
 * Created by Gur on 11/20/2016.
 *
 * 1  function Dijkstra(Graph, source):
 2
 3      create vertex set Q
 4
 5      for each vertex v in Graph:             // Initialization
 6          dist[v] ← INFINITY                  // Unknown distance from source to v
 7          prev[v] ← UNDEFINED                 // Previous node in optimal path from source
 8          add v to Q                          // All nodes initially in Q (unvisited nodes)
 9
 10      dist[source] ← 0                        // Distance from source to source
 11
 12      while Q is not empty:
 13          u ← vertex in Q with min dist[u]    // Source node will be selected first
 14          remove u from Q
 15
 16          for each neighbor v of u:           // where v is still in Q.
 17              alt ← dist[u] + length(u, v)
 18              if alt < dist[v]:               // A shorter path to v has been found
 19                  dist[v] ← alt
 20                  prev[v] ← u
 21
 22      return dist[], prev[]
 */
public class Dijkstra extends Algorithm {
    final Map<Vertex, Integer> distance;
    final Map<Vertex, Vertex> route;
    final Set<Vertex> unvisited;
    final Graph graph;
    final Vertex source;
    //final Vertex target;
    final ExecutionChain<Collection<Vertex>, Vertex> min;

    public Dijkstra(Graph graph, Vertex source){
        this.graph = graph;
        this.source = source;
        this.distance = new HashMap<>();
        this.route = new HashMap<>();
        this.unvisited = new HashSet<>();
        this.min = Queriable.create(Vertex.class)
                    .min(new Comparator<Vertex>() {
                        @Override
                        public int compare(Vertex v1, Vertex v2) {
                            return distance.get(v1) - distance.get(v2);
                        }
                    });
    }

    public void execute(){
        // init the algorithm
        for(Vertex vertex: graph.getVertices()){
            if (shouldUseVertex(vertex)) {
                distance.put(vertex, Integer.MAX_VALUE);
                route.put(vertex, null);
                unvisited.add(vertex);
            }
        }
        distance.put(source, 0);

        while (!unvisited.isEmpty()){
            Vertex u = min.execute(unvisited);
            unvisited.remove(u);

            for (Vertex v: u.getNeigbores()){
                int alternative = distance.get(u) + getWeight(u.getEdge(v));
                if (alternative < distance.get(v)){
                    distance.put(v, alternative);
                    route.put(v, u);
                }
            }
        }
    }

    protected boolean shouldUseVertex(Vertex vertex) {
        return true;
    }

    protected int getWeight(Edge edge){
        return (int) edge.getWeight();
    }

    public Map<Vertex, Integer> getDistance(){ return distance; }
    public Map<Vertex, Vertex> getRoute(){ return route; }

    public Vertex getNextMove(Vertex source, Vertex target) {
        Vertex position = target;
        while (getRoute().get(position) != source){
            position = getRoute().get(position);

            if (position == null) {
                return null;
            }
        }

        return position;
    }
}