package agents.ass2;

import Algorithms.Dijkstra;
import agents.AbstractAgent;
import graphs.Graph;
import graphs.Vertex;

/**
 * Created by Gur on 12/13/2016.
 */
public abstract class MinimaxAgent extends AbstractAgent {
    final int depth;

    protected MinimaxAgent(String id, Vertex initial, Vertex goal, Graph world, int depth) {
        super(id, initial, goal, world);
        this.depth = depth;
    }

     /**
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
      **/
    protected double alphaBeta(Vertex vertex, int depth, double alpha, double beta, boolean isMaximizingAgent){
        if (depth == 0 || vertex == getGoal()){
            return getHeuristicValue(vertex, isMaximizingAgent);
        }

        if (isMaximizingAgent){
            double v = Double.MIN_VALUE;
            for(Vertex neighbore: vertex.getNeigbores()){
                v = Math.max(v, alphaBeta(neighbore, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, v);
                if (beta <= alpha){
                    break;
                }
            }
            return v;
        }
        else {
            double v = Double.MAX_VALUE;
            for(Vertex neighbore: vertex.getNeigbores()){
                v = Math.min(v, alphaBeta(neighbore, depth - 1, alpha, beta, false));
                beta = Math.min(beta, v);
                if (beta <= alpha){
                    break;
                }
            }
            return v;
        }
    }

    /** sort by distance to goal **/
    protected int getHeuristicValue(Vertex vertex, boolean isMaximizingAgent){
        Dijkstra dijkstra = new Dijkstra(getContext(), getCurrent());

        dijkstra.execute();

        return dijkstra.getDistance().containsKey(vertex)? dijkstra.getDistance().get(vertex) : Integer.MAX_VALUE;
    }
}