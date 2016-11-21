package program;

import agents.AbstractAgent;
import graphs.Graph;

/**
 * Created by Gur on 11/21/2016.
 */
public class Simulation {
    private final AbstractAgent[] agents;
    private final Graph graph;

    public Simulation (Graph graph, AbstractAgent ... agents){
        this.graph = graph;
        this.agents = agents;
    }

    public void runSimulation(AbstractAgent agent){
        System.out.println(graph);

        while (!agent.getCurrent().equals(agent.getGoal())){
            agent.doMove();

            System.out.println(String.format("Agent: %s is at %s with score: %d" , agent, agent.getCurrent(), agent.getScore()));
        }
    }
}
