package ass4;

import com.gurkashi.fj.lambdas.Predicate;
import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * Created by Gur on 1/5/2017.
 */
public class BayesNetworkGraph {
    public static final String KEYS = "keys";
    public static final String LOCKS = "locks";
    public static final String VERTICES = "vertices";
    public static final String AGENTS = "agents";
    public static final String GOAL = "goal";
    public static final String AT = "at";
    public static final String WEIGHT = "weight";
    public static final String NEIGBORE = "id";
    public static final String EDGES = "edges";
    public static final String ID = "id";
    public static final String DEPTH = "depth";
    public static final String SCORE = "score";
    public static final String MOVES_COUNT = "moves";

    final Collection<Vertex> vertices;
    final Collection<Edge> edges;
    final Collection<Agent> agents;
    final int depth;

    public BayesNetworkGraph(int depth){
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
        this.agents = new ArrayList<Agent>();
        this.depth = depth;
    }

    public static boolean isDirected() {
        return true;
    }

    @Override
    public String toString(){
        String result = "";

        result += "Vertices:\n";
        for (Vertex vertex: vertices){
            result += vertex.toString() + "\n";
        }

        result += "\nEdges:\n";
        for (Edge edge: edges){
            result += edge.toString() + "\n";
        }

        return result.trim();
    }

    public static graphs.Graph fromFile(String path, int depth) throws IOException {
        graphs.Graph graph = new graphs.Graph(depth);

        try(Scanner scanner = new Scanner(new File(path))){
            String pbrcHeader = scanner.nextLine();
            graph.setPBRC(Double.parseDouble(pbrcHeader.split(" ")[1]));

            String numOfVerticesHeader = scanner.nextLine();
            createInitialVertices(numOfVerticesHeader, graph);
            scanner.nextLine();

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                String[] parts = line.split(" ");

                switch (parts[0]) {
                    case "#V":
                        configVertex(parts, graph);
                        break;
                    case "#E":
                        configEdge(parts, graph);
                        break;
                }
            }
        }

        return graph;
    }

    private static void configEdge(String[] parts, graphs.Graph graph) {
        Vertex from = graph.getVertex(parts[1]);
        Vertex to = graph.getVertex(parts[2]);

        if (parts[3].startsWith("W")){
            parts[3] = parts[3].substring(1);
        }
        double weight = Double.parseDouble(parts[3]);

        graph.addEdge(from, to, weight);

        if (!isDirected()) {
            graph.addEdge(to, from, weight);
        }
    }

    private static void createInitialVertices(String header, graphs.Graph graph) {
        int n = Integer.parseInt(header.split(" ")[1]);

        for(Integer i = 1; i <= n; i++){
            Vertex vi = new Vertex(i.toString(), graph);

            graph.addVertex(i.toString());
        }
    }

    private static void configVertex(final String[] parts, graphs.Graph graph) {
        Vertex vertex = graph.getVertex(parts[1]);

        if (parts.length == 5){
            String key = parts[3];
            Double p = Double.parseDouble(parts[4]);
            vertex.pkeys.put(key, p);
        }
    }

    public Vertex getVertex(final String id) {
        return Queriable.create(Vertex.class)
                .filter(new Predicate<Vertex>() {
                    @Override
                    public boolean predict(Vertex vertex) {
                        return vertex.getId().equals(id);
                    }
                })
                .single()
                .execute(vertices);
    }

    public Vertex addVertex(String id){
        final Vertex vertex = new Vertex(id, this);

        this.vertices.add(vertex);

        return vertex;
    }

    public Edge addEdge(Vertex from, Vertex to, double weight){
        Edge edge = new Edge("<" + from.id + "," + to.id + ">", this, weight, from, to);
        edges.add(edge);
        from.addNeigbore(to, edge);
        return edge;
    }

    public Edge getEdge(final Vertex from, final Vertex to) {
        return Queriable.create(Edge.class)
                .filter(new Predicate<Edge>() {
                    @Override
                    public boolean predict(Edge edge) {
                        return edge.from.equals(from) && edge.to.equals(to);
                    }
                })
                .singleOrNull()
                .execute(edges);
    }

    public Collection<Vertex> getVertices() {
        return vertices;
    }


    public JSONObject toJSON(){
        JSONObject json = new JSONObject();

        json.put(VERTICES, new JSONArray());
        for (Vertex vertex: vertices){
            JSONObject v = new JSONObject(); // the vertex json

            v.put(ID, vertex.id);

            JSONArray keys = new JSONArray(); // the keys in the vertex
            for (Key k : vertex.keys){
                keys.put(k.id);
            }
            v.put(KEYS, keys);

            JSONArray locks = new JSONArray(); // the locks in the vertex
            for (Lock l : vertex.locks){
                locks.put(l.id);
            }
            v.put(LOCKS, locks);

            JSONArray edges = new JSONArray(); // the edges from the vertexs
            for (Vertex neigbore: vertex.getNeigbores()){
                JSONObject edge = new JSONObject();
                edge.put(NEIGBORE, vertex.getEdge(neigbore).to.id);
                edge.put(WEIGHT, vertex.getEdge(neigbore).weight);
                edges.put(edge);
            }
            v.put(EDGES, edges);

            json.getJSONArray(VERTICES).put(v); // adding the vertex
        }

        json.put(DEPTH, depth);
        JSONArray agents = new JSONArray();
        for(Agent agent: this.agents){
            JSONObject a = new JSONObject();
            a.put(ID, agent.id);
            a.put(AT, agent.start.id);
            a.put(GOAL, agent.goal.id);
            a.put(KEYS, new JSONArray());
            a.put(SCORE, 0);
            agents.put(a);
        }
        json.put(AGENTS, agents);

        json.put(MOVES_COUNT, 0);

        return json;
    }

    public void addAgent(String id, String start, String goal) {
        this.agents.add(new Agent(id, this, start, goal));
    }
}
