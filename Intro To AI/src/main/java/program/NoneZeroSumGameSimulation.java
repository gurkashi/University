package program;

import graphs.Graph;
import org.json.JSONArray;
import org.json.JSONObject;

import static graphs.Graph.*;

/**
 * Created by Gur on 12/27/2016.
 */
public class NoneZeroSumGameSimulation extends Simulation{
    protected NoneZeroSumGameSimulation(Graph graph) {
        super(graph);
    }

    @Override
    public boolean finishSimulation(JSONObject state) {
        for (Object json: state.getJSONArray(AGENTS)){
            JSONObject agent = (JSONObject) json;

            if (!isAgentDone(state, agent.getString(ID))){
                return false;
            }
        }
        return true;
    }

    @Override
    public String calculateNextMove(JSONObject state, JSONObject agent) {
        return nonZeroSum(state, MAX_DEPTH * 2, agent.getString(ID)).action;
    }

    public Tuple nonZeroSum(JSONObject state, int depth, String agentId){
        JSONObject agent = getById(state.getJSONArray(AGENTS), agentId);
        JSONObject otherAgent = getById(state.getJSONArray(AGENTS), agentId.equals(AGENT1)? AGENT2 : AGENT1);

        if (agentId.equals(AGENT1) && agent.getString(AT).equals("4")){
            System.out.print("");
        }

        // if depth is reaches - using heuristic to take the cheapest path
        if (depth == 0){
            int agentHeuristics = getHeuristicValue(graph.getVertex(agent.getString(AT)), graph.getVertex(agent.getString(GOAL)));

            // closest wins
            Tuple result = new Tuple((-1) * (agentHeuristics + agent.getInt(SCORE)), agent.getString(AT));
            return result;
        }
        // if the agent is at the goal - he wins
        if (agent.getString(AT).equals(agent.getString(GOAL))){
            return new Tuple(agent.getInt(SCORE) * (-1), agent.getString(AT));
        }

        int v = Integer.MIN_VALUE;
        String best = "";
        JSONObject currentVertex = getVertex(state, agent.getString(AT));
        for (int i = 0; i < currentVertex.getJSONArray(EDGES).length(); i++){
            // expand
            JSONObject vertex = getVertex(state, currentVertex.getJSONArray(EDGES).getJSONObject(i).getString(ID));

            if (agentId.equals(AGENT1) && vertex.getString(ID).equals("4")){
                System.out.print("");
            }

            JSONObject clonedState = new JSONObject(state.toString());
            moveAgentToVertex(agent.getString(ID), clonedState, vertex.getString(ID));
            int recursiveResult = Math.max(v, nonZeroSum(clonedState, depth - 1, otherAgent.getString(ID)).score);
            if (recursiveResult > v){
                v = recursiveResult;
                best = vertex.getString(ID);
            }
        }
        return new Tuple(v, best);
    }

    JSONObject getVertex(JSONObject state, String vertexId){
        JSONArray vertices = state.getJSONArray(VERTICES);
        JSONObject vertex = getById(vertices, vertexId);
        return vertex;
    }

    /*

    01 function alphabeta(node, depth, α, β, maximizingPlayer)
    02      if depth = 0 or node is a terminal node
    03          return the heuristic value of node
    04      if maximizingPlayer
    05          v := -∞
    06          for each child of node
    07              v := max(v, alphabeta(child, depth – 1, α, β, FALSE))
    08              α := max(α, v)
    09              if β ≤ α
    10                  break (* β cut-off *)
    11          return v
    12      else
    13          v := ∞
    14          for each child of node
    15              v := min(v, alphabeta(child, depth – 1, α, β, TRUE))
    16              β := min(β, v)
    17              if β ≤ α
    18                  break (* α cut-off *)
    19          return v

    */
}

