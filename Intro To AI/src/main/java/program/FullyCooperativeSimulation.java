package program;

import graphs.Graph;
import org.json.JSONArray;
import org.json.JSONObject;

import static graphs.Graph.*;

/**
 * Created by Gur on 12/27/2016.
 */
public class FullyCooperativeSimulation extends Simulation{
    protected FullyCooperativeSimulation(Graph graph) {
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
        return alphabeta(state, MAX_DEPTH * 2, Integer.MIN_VALUE, Integer.MAX_VALUE, agent.getString(ID)).action;
    }

    public Tuple alphabeta(JSONObject state, int depth, int alpha, int beta, String agentId){
        JSONObject agent = getById(state.getJSONArray(AGENTS), agentId);
        JSONObject otherAgent = getById(state.getJSONArray(AGENTS), agentId.equals(AGENT1)? AGENT2 : AGENT1);

        // if depth is reaches - using heuristic to take the cheapest path
        if (depth == 0){
            int agentHeuristics = getHeuristicValue(graph.getVertex(agent.getString(AT)), graph.getVertex(agent.getString(GOAL)));
            int otherAgentHeuristics = getHeuristicValue(graph.getVertex(otherAgent.getString(AT)), graph.getVertex(otherAgent.getString(GOAL)));

            // closest wins
            Tuple result = new Tuple((-1) * (agentHeuristics + otherAgentHeuristics), agent.getString(AT));
            return result;
        }
        // if both agents are at the goal - they win
        if (agent.getString(AT).equals(agent.getString(GOAL)) && otherAgent.getString(AT).equals(otherAgent.getString(GOAL))){
            return new Tuple(agent.getInt(SCORE) + otherAgent.getInt(SCORE), agent.getString(AT));
        }

        // assuming agent-1 is maximizing (although irrelevant because we are in zero sum game of 1/-1)
        // max turn
        if (agentId.equals(AGENT1)){
            int v = Integer.MIN_VALUE;
            String best = "";
            JSONObject currentVertex = getVertex(state, agent.getString(AT));
            for (int i = 0; i < currentVertex.getJSONArray(EDGES).length(); i++){
                JSONObject vertex = getVertex(state, currentVertex.getJSONArray(EDGES).getJSONObject(i).getString(ID));
                JSONObject clonedState = new JSONObject(state.toString());
                moveAgentToVertex(agent.getString(ID), clonedState, vertex.getString(ID));
                int recursiveResult = Math.max(v, alphabeta(clonedState, depth - 1, alpha, beta, otherAgent.getString(ID)).score);
                if (recursiveResult > v){
                    v = recursiveResult;
                    best = vertex.getString(ID);
                }
                alpha = Math.max(alpha, v);
                if (beta <= alpha){
                    break;
                }
            }
            return new Tuple(v, best);
        }
        // min turn
        else {
            int v = Integer.MAX_VALUE;
            String best = "";
            JSONObject currentVertex = getVertex(state, agent.getString(AT));
            for (int i = 0; i < currentVertex.getJSONArray(EDGES).length(); i++){
                JSONObject vertex = getVertex(state, currentVertex.getJSONArray(EDGES).getJSONObject(i).getString(ID));
                JSONObject clonedState = new JSONObject(state.toString());
                moveAgentToVertex(agent.getString(ID), clonedState, vertex.getString(ID));
                int recursiveResult = Math.min(v, alphabeta(clonedState, depth - 1, alpha, beta, otherAgent.getString(ID)).score);
                if (recursiveResult < v){
                    v = recursiveResult;
                    best = vertex.getString(ID);
                }
                beta = Math.min(beta, v);
                if (beta <= alpha){
                    break;
                }
            }
            return new Tuple(v, best);
        }
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

