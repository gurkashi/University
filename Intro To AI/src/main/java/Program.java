import agents.AbstractAgent;
import agents.GreedyAgent;
import agents.GreedyKeyCollectorAgent;
import agents.HumanAgent;
import graphs.Graph;
import program.Simulation;

import java.io.IOException;

/**
 * Created by Gur on 11/11/2016.
 */
public class Program {
    public static void main(String args[]) throws IOException {
        //playHuman();
        //playGreedy();
        playGreedyKeyCollector();
    }

    private static void playHuman() throws IOException {
        Graph graph;
        AbstractAgent agent;
        Simulation simulation;
        graph = Graph.fromFile("C:\\Users\\Gur\\IdeaProjects\\IntroToAI\\src\\main\\resources\\graph.txt");
        agent = new HumanAgent("Human", graph.getVertex("1"), graph.getVertex("4"), graph);
        simulation = new Simulation(graph);
        simulation.runSimulation(agent);
    }

    private static void playGreedy() throws IOException {
        Graph graph;
        AbstractAgent agent;
        Simulation simulation;
        graph = Graph.fromFile("C:\\Users\\Gur\\IdeaProjects\\IntroToAI\\src\\main\\resources\\graph.txt");
        agent = new GreedyAgent("Greedy", graph.getVertex("1"), graph.getVertex("4"), graph);
        simulation = new Simulation(graph);
        simulation.runSimulation(agent);
    }


    private static void playGreedyKeyCollector() throws IOException {
        Graph graph;
        AbstractAgent agent;
        Simulation simulation;
        graph = Graph.fromFile("C:\\Users\\Gur\\IdeaProjects\\IntroToAI\\src\\main\\resources\\graph.txt");
        agent = new GreedyKeyCollectorAgent("Greedy Key Collector", graph.getVertex("1"), graph.getVertex("4"), graph);
        simulation = new Simulation(graph);
        simulation.runSimulation(agent);
    }
}
