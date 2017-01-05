package program;

import Algorithms.Dijkstra;
import graphs.Graph;
import graphs.Vertex;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static graphs.Graph.*;

/**
 * Created by Gur on 11/11/2016.
 */
public abstract class Simulation {
    static final String FILE = "C:\\Users\\Gur\\Desktop\\graph.txt";
    static final int NOOP_COST = 1;
    static final int MAX_DEPTH = 1;
    static final boolean RUN_SIMULTANIOUSLY = false;
    static final String AGENT1 = "A";
    static final String AGENT2 = "B";
    static final boolean PRINT_WORLD = false;

    protected final Graph graph;

    public static void main(String args[]) throws IOException {
        play();
    }

    private static void play() throws IOException {
        Graph graph = Graph.fromFile(FILE, MAX_DEPTH);
        graph.addAgent(AGENT1,"1","4");
        graph.addAgent(AGENT2,"2","4");

        Simulation[] simulations = {
                new MinimaxSimulation(graph),
                new NoneZeroSumGameSimulation(graph),
                //new FullyCooperativeSimulation(graph)
        };

        for (Simulation simulation: simulations){
            System.out.println(simulation.name() + "\n");
            simulation.runSimulation();
            System.out.println("\n---------------------\n");
        }
    }

    protected String name(){
        return this.getClass().getSimpleName();
    }

    protected Simulation(Graph graph){
        this.graph = graph;
    }

    // the heuristic is the relaxed problem with no locks
    public int getHeuristicValue(Vertex src, Vertex goal){
        Dijkstra dijkstra = new Dijkstra(graph, src);
        dijkstra.execute();
        return dijkstra.getDistance().get(goal);
    }

    public abstract boolean finishSimulation(JSONObject state);

    public void runSimulation(){
        JSONObject state = graph.toJSON();
        while (! finishSimulation(state)){
            if (!RUN_SIMULTANIOUSLY){
                for (Object o: state.getJSONArray(AGENTS)){
                    JSONObject agent = (JSONObject) o;
                    String agentStateBefore = agent.toString();
                    String vertex = calculateNextMove(new JSONObject(state.toString()), new JSONObject(agent.toString()));
                    moveAgentToVertex(agent.getString(ID), state, vertex);
                    System.out.println(String.format("%s -> %s", agentStateBefore, agent.toString()));

                    if (PRINT_WORLD) {
                        System.out.println(state);
                    }
                }
            }
            else {
                ArrayList<String> vertices = new ArrayList<>();
                for (Object o: state.getJSONArray(AGENTS)){
                    JSONObject agent = (JSONObject) o;
                    vertices.add(calculateNextMove(state, agent));
                }

                for (Object o: state.getJSONArray(AGENTS)){
                    JSONObject agent = (JSONObject) o;
                    moveAgentToVertex(agent.getString(ID), state, vertices.get(0));
                    vertices.remove(0);
                    System.out.println(state);
                    System.out.println();
                }
            }
        }
    }

    public abstract String calculateNextMove(JSONObject state, JSONObject agent);

    public void moveAgentToVertex(String agentId, JSONObject state, String vertex) {
        JSONObject agent = getById(state.getJSONArray(AGENTS), agentId);

        // agent is at goal - no action needed
        if (isAgentDone(state, agent.getString(ID))){
            return;
        }

        // agent has done "NO-OP"
        if (vertex.equals(agent.getString(AT))){
            doNoOp(agent);
            return;
        }

        JSONObject currentVertex = getById(state.getJSONArray(VERTICES), agent.getString(AT));
        JSONObject nextVertex = getById(state.getJSONArray(VERTICES), vertex);

        // agent and new vertex are not connected
        if (!areNeighbores(currentVertex.getString(ID), nextVertex.getString(ID), state)){
            doNoOp(agent);
            return;
        }

        JSONArray agentKeys = agent.getJSONArray(KEYS);
        JSONArray locks = nextVertex.getJSONArray(LOCKS);
        // pay score of weight between the two vertices
        incrementAgentScore(agent, getEdgeWeight(currentVertex, nextVertex.getString(ID)));
        // collect keys from current vertex
        collectKeysFromCurrentVertex(currentVertex, agentKeys);
        // try go to next vertex
        if (keyMatchingLocks(agentKeys, locks)){
            // remove locks and keys
            openLocks(agentKeys, locks);
            // move the agent
            agent.put(AT, nextVertex.getString(ID));
        }
        // no keys, stay at current vertex
        else {
            return;
        }
    }

    private void collectKeysFromCurrentVertex(JSONObject currentVertex, JSONArray agentKeys) {
        // collect keys from current vertex
        for (int i = 0; i < currentVertex.getJSONArray(KEYS).length(); i++){
            agentKeys.put(currentVertex.getJSONArray(KEYS).getString(i));
        }
        // remove the keys from the vertex
        while (currentVertex.getJSONArray(KEYS).length() > 0){
            currentVertex.getJSONArray(KEYS).remove(0);
        }
    }

    private void openLocks(JSONArray agentKeys, JSONArray locks) {
        for (Object obj: locks){
            String lock = (String) obj;
            int index = getIndex(agentKeys, lock);
            if (index != -1){
                agentKeys.remove(index);
            }
        }
        while (locks.length() > 0){
            locks.remove(0);
        }
    }

    protected int getIndex(JSONArray array, String id){
        for (int i = 0; i < array.length(); i++){
            if (array.get(i).equals(id)){
                return i;
            }
        }

        return -1;
    }

    private int getEdgeWeight(JSONObject vertex, String neigbore){
        JSONArray edges = vertex.getJSONArray(EDGES);
        return getById(edges, neigbore).getInt(WEIGHT);
    }

    private boolean keyMatchingLocks(JSONArray keys, JSONArray locks){
        for (int i = 0; i < locks.length(); i++){
            String lock = locks.getString(i);
            if (!contains(keys, lock)){
                return false;
            }
        }
        return true;
    }

    protected boolean contains(JSONArray array, String id){
        for (int i = 0; i < array.length(); i++){
            if (id.equals(array.getString(i))){
                return true;
            }
        }
        return false;
    }

    private void incrementAgentScore(JSONObject agent, int addition){
        agent.put(SCORE, agent.getInt(SCORE) + addition);
    }

    private void doNoOp(JSONObject agent){
        incrementAgentScore(agent, NOOP_COST);
    }

    private boolean areNeighbores(String v1, String v2, JSONObject state){
        JSONObject currentVertex = getById(state.getJSONArray(VERTICES), v1);
        JSONArray neighbores = currentVertex.getJSONArray(EDGES);
        for (Object o: neighbores){
            JSONObject v = (JSONObject) o;
            if (v.get(NEIGBORE).equals(v2)){
                return true;
            }
        }
        return false;
    }

    protected JSONObject getById(JSONArray array, String id){
        for (Object object: array){
            JSONObject jsonObject = (JSONObject) object;
            if (jsonObject.get(ID).equals(id)){
                return jsonObject;
            }
        }
        return null;
    }

    protected boolean isAgentDone(JSONObject state, String id){
        JSONObject agent = getById(state.getJSONArray(AGENTS), id);

        return agent.get(GOAL).equals(agent.get(AT));
    }
}