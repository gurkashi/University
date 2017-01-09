package graphs;

import com.gurkashi.fj.lambdas.Predicate;
import com.gurkashi.fj.lambdas.Selector;
import com.gurkashi.fj.queries.stracture.Queriable;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Gur on 11/11/2016.
 *
 * IMPORTANT !!
 * Graph is directed
 *
 */
public class Graph {
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
    private double PBRC;
    boolean isDirected = false;
    int numOfKeys = 0;

    public Graph(int depth){
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
        this.agents = new ArrayList<Agent>();
        this.depth = depth;
    }

    public static boolean isDirected(Graph graph) {
        return graph.isDirected;
    }

    @Override
    public String toString(){
        return toJSON().toString();
    }

    public static List<Vertex> topSort(Graph g){
        final Set<Vertex> used = new HashSet<>();
        List<Vertex> sorted = new ArrayList<>();

        while (sorted.size() < g.getVertices().size()){
            Vertex next = Queriable.create(Vertex.class)
                    .map(new Selector<Vertex, Pair<Vertex, List<Vertex>>>() {
                        @Override
                        public Pair<Vertex, List<Vertex>> select(Vertex vertex) {
                            List<Vertex> children = new ArrayList<Vertex>();
                            children.addAll(vertex.getChildren());
                            children.removeAll(used);
                            return new Pair<Vertex, List<Vertex>>(vertex, children);
                        }
                    })
                    .filter(new Predicate<Pair<Vertex, List<Vertex>>>() {
                        @Override
                        public boolean predict(Pair<Vertex, List<Vertex>> vertexListPair) {
                            return vertexListPair.getValue().size() == 0;
                        }
                    })
                    .map(new Selector<Pair<Vertex,List<Vertex>>, Vertex>() {
                        @Override
                        public Vertex select(Pair<Vertex, List<Vertex>> vertexListPair) {
                            return vertexListPair.getKey();
                        }
                    })
                    .first()
                    .execute(g.getVertices());
            sorted.add(next);
            used.add(next);
        }

        return sorted;
    }

    public static Graph fromFile(String path, int depth) throws IOException {
        Graph graph = new Graph(depth);

        try(Scanner scanner = new Scanner(new File(path))){
            String probOfBRC = scanner.nextLine();
            graph.setPBRC(Double.parseDouble(probOfBRC.split(" ")[1]));

            String numOfKeysString = scanner.nextLine();
            graph.setNumOfKeys(Integer.parseInt(numOfKeysString.split(" ")[1]));

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

    public static Graph toBayesian(Graph graph) {
        graph.isDirected = true;
        final double weight = 1;
        Graph bayesian = new Graph(0);
        bayesian.isDirected = true;

        //  for each vertex add
        //      one node for each resource
        //      one node for each blockage
        //  add one global BRC node
        int numOfBayesianNodes = graph.getVertices().size() * graph.getNumOfKeys() * 2 + 1;
        createInitialVertices(numOfBayesianNodes , bayesian);

        // rename ids of vertices in bayesian graph
        Vertex brc = bayesian.getVertex("1");
        brc.id = "BRC";
        brc.p = graph.PBRC;
        int i = 2;
        for (Vertex v: graph.getVertices()){
            for (int j = 0; j < graph.getNumOfKeys() * 2; j+= 2){
                Vertex virj = bayesian.getVertex(Integer.toString(i + j));
                Vertex vibj = bayesian.getVertex(Integer.toString(i + j + 1));
                virj.p = v.pkeys.get("" + (j/2+1));

                String vertexId = v.id;
                String keyId = Integer.toString(j/2 + 1);

                virj.id = vertexId + "R" + keyId;
                vibj.id = vertexId + "B" + Integer.toString(j/2 + 1);
            }

            i += 2 * graph.getNumOfKeys();
        }

        // add edges from brc to blockades
        for (Vertex v: bayesian.getVertices()){
            if (v != brc && v.id.contains("B")){
                bayesian.addEdge(brc, v, weight);
            }
        }

        // add edges from adjacent vertices (resource to blockage only)
        for (Vertex v: bayesian.getVertices()){
            for (Vertex u: bayesian.getVertices()){
                if (v == u || v == brc || u == brc){ continue; }
                if (isResourceVertex(v) && isBlockageVertex(u) && shareSameKey(v, u)){
                    Vertex graphSource = graph.getVertex(getIdOfGraphVertex(v));
                    Vertex graphTarget = graph.getVertex(getIdOfGraphVertex(u));

                    if (graph.getEdge(graphSource, graphTarget) != null){
                        bayesian.addEdge(v, u, weight);
                    }
                }
            }
        }

        for (Vertex v: bayesian.getVertices()){
            if (isResourceVertex(v)){
                // p is the probability value already
            }
            else if (isBlockageVertex(v)){
                Collection<String> nodeTableKeys = Vertex.getPermutations(v.getParents().size());
                for (String row: nodeTableKeys){
                    v.ptable.put(row, getProbOfBlockage(row));
                }
            }
        }

        return bayesian;
    }

    // TODO: how to handle more than one adj resource
    private static double getProbOfBlockage(String s){
        int sum = 0;
        for (int i = 1; i < s.length(); i++){
            if (s.charAt(i) == 'T'){
                sum += 1;
            }
        }
        boolean brc = s.charAt(0) == 'T';

        if (brc == false){
            if (sum == 0){
                return 0.01;
            }
            else {
                return 0.2;
            }
        }
        else {
            if (sum == 0){
                return 0.001;
            }
            else {
                return 0.1;
            }
        }
    }

    private static boolean shareSameKey(Vertex v, Vertex u) {
        return getKey(v).equals(getKey(u));
    }

    private static String getKey(Vertex v) {
        String key = "";
        if (isResourceVertex(v)){
            key =  v.id.substring(v.id.indexOf("R") + 1, v.id.length());
        }
        else if (isBlockageVertex(v)){
            key =  v.id.substring(v.id.indexOf("B") + 1, v.id.length());
        }
        return key;
    }

    static String getIdOfGraphVertex(Vertex v){
        if (isResourceVertex(v)){
            return v.id.substring(0, v.id.indexOf("R"));
        }
        if (isBlockageVertex(v)){
            return v.id.substring(0, v.id.indexOf("B"));
        }
        return "";
    }

    static boolean isResourceVertex(Vertex v){
        return v.id.contains("R");
    }

    static boolean isBlockageVertex(Vertex v){
        return !v.id.equals("BRC") && v.id.contains("B");
    }

    private static void configEdge(String[] parts, Graph graph) {
        Vertex from = graph.getVertex(parts[1]);
        Vertex to = graph.getVertex(parts[2]);

        if (parts[3].startsWith("W")){
            parts[3] = parts[3].substring(1);
        }
        double weight = Double.parseDouble(parts[3]);

        graph.addEdge(from, to, weight);

        if (!isDirected(graph)) {
            graph.addEdge(to, from, weight);
        }
    }

    private static void createInitialVertices(String header, Graph graph) {
        int n = Integer.parseInt(header.split(" ")[1]);

        createInitialVertices(n, graph);
    }

    private static void createInitialVertices(int n, Graph graph) {
        for(Integer i = 1; i <= n; i++){
            Vertex vi = new Vertex(i.toString(), graph);

            graph.addVertex(i.toString());
        }
    }


    private static void configVertex(final String[] parts, graphs.Graph graph) {
        Vertex vertex = graph.getVertex(parts[1]);

        if (parts.length >= 5){
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

    public void setPBRC(double PBRC) {
        this.PBRC = PBRC;
    }

    public int getNumOfKeys() {
        return numOfKeys;
    }

    public void setNumOfKeys(int n){
        this.numOfKeys = n;
    }

    public String printBayesian(Graph bayesian) {
        String result = "";

        result += "GLOBAL:" + "\n";
        result += "P(BRC)=" + PBRC + "\n";
        result += "P(NOT BRC)=" + (1.0 - PBRC) + "\n\n";

        for (Vertex v: getVertices()){
            result += "Vertex " + v.id + ":" + "\n";
            for (String key: v.pkeys.keySet()){
                result += "\t" + "P(K" + key +")=" + v.pkeys.get(key) +"\n";
                result += "\t" + "P(NOT K" + key +")=" + (1.0 - v.pkeys.get(key)) +"\n";
            }
            result += "\n";

            for (Vertex bayesianVertex: bayesian.getVertices()){
                if (isBlockageVertex(bayesianVertex) && getIdOfGraphVertex(bayesianVertex).equals(v.id)){
                    Collection<Vertex> parents = bayesianVertex.getParents();

                    result += "\t" + "Parents (Ordered): ";
                    for (Vertex p : parents){
                        result += p.id + "\t";
                    }
                    result += "\n";

                    for (String row: bayesianVertex.ptable.keySet()){
                        result += "\t" + "P(BLOCK | " + row + ")=" + bayesianVertex.ptable.get(row) + "\n";
                        result += "\t" + "P(NOT BLOCK | " + row + ")=" + (1.0 - bayesianVertex.ptable.get(row)) + "\n";
                    }
                }
            }
        }

        result += "\n";

        return result.trim();
    }

    public static double chanceOf(String v, String b, List<Evidence> evidences, Graph bayesian) {
        String vertexId = v + "B" + b;
        Vertex vertex = bayesian.getVertex(vertexId);

        double sum = 0;
        for(String row: vertex.ptable.keySet()){
            if (matchEvidences(row, vertex, evidences, bayesian)){
                sum += calculateRowChance(vertex, row, evidences, bayesian);
            }
        }
        return sum;
    }

    private static double calculateRowChance(Vertex vertex, String row, List<Evidence> evidences, Graph bayesian) {
        double res = vertex.ptable.get(row);

        Iterator<Vertex> parentIterator = vertex.getParents().iterator();
        for (int i = 0; i < row.length(); i++) {
            final Vertex parent = parentIterator.next();

            Evidence evidence = Queriable.create(Evidence.class).filter(new Predicate<Evidence>() {
                @Override
                public boolean predict(Evidence evidence) {
                    return evidence.vertex.equals(parent.id);
                }
            }).singleOrNull().execute(evidences);

            if (evidence == null) {
                if (row.charAt(i) == 'T'){
                    res *= parent.p;
                }
                else if (row.charAt(i) == 'F'){
                    res *= 1-parent.p;
                }
            }
        }
        return res;
    }

    private static boolean matchEvidences(String row, Vertex vertex, List<Evidence> evidences, Graph bayesian){
        Iterator<Vertex> parentIterator = vertex.getParents().iterator();
        for (int i = 0; i < row.length(); i++){
            final String parentId = parentIterator.next().id;
            final boolean value = row.charAt(i) == 'T'? true:false;

            Evidence e = Queriable.create(Evidence.class).filter(new Predicate<Evidence>() {
                @Override
                public boolean predict(Evidence evidence) {
                    return evidence.vertex.equals(parentId);
                }
            }).singleOrNull().execute(evidences);

            if (e != null && e.exists != value){
                return false;
            }
        }
        return true;
    }
}