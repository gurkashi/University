package graphs;

import com.gurkashi.fj.lambdas.Predicate;
import com.gurkashi.fj.queries.stracture.Queriable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

/**
 * Created by Gur on 11/11/2016.
 *
 * IMPORTANT !!
 * Graph is directed
 *
 */
public class Graph {
    final Collection<Vertex> vertices;
    final Collection<Edge> edges;

    public Graph(){
        this.vertices = new ArrayList<Vertex>();
        this.edges = new ArrayList<Edge>();
    }

    public static boolean isDirected() {
        return false;
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

    public static Graph fromFile(String path) throws IOException {
        Graph graph = new Graph();

        try(Scanner scanner = new Scanner(new File(path))){
            String header = scanner.nextLine();
            createInitialVertices(header, graph);
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

    private static void configEdge(String[] parts, Graph graph) {
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

    private static void createInitialVertices(String header, Graph graph) {
        int n = Integer.parseInt(header.split(" ")[1]);

        for(Integer i = 1; i <= n; i++){
            Vertex vi = new Vertex(i.toString(), graph);

            graph.addVertex(i.toString());
        }
    }

    private static void configVertex(final String[] parts, Graph graph) {
        Vertex vertex = graph.getVertex(parts[1]);
        String alias = parts[4].equals(";") ? null : parts[4];

        if (parts[2].equals("K")){
            vertex.addKey(new Key(parts[3], graph, alias));
        }
        else if (parts[2].equals("L")){
            vertex.addLock(new Lock(parts[3], graph, alias));
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
}