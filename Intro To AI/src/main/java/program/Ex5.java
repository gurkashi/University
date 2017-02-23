package program;

import com.gurkashi.fj.lambdas.Predicate;
import com.gurkashi.fj.lambdas.Selector;
import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Key;
import graphs.Vertex;

import java.util.*;

/**
 * Created by Gur on 1/10/2017.
 *
 * main idea:
 *      1.  create states with the cartesian representation V * P(K) * P (B)
 *          this is a representation of a vertex in the graph, subset of keys of the agent, subset of blockages in the next vertex
 *      2.  solve the mdp in the following way
 *          a. actions are transitions to a vertex in the graph
 *          b. next state is tagged by the vertex in the graph + the keys in it
 *          c. probabilities of getting so a state s' is the probability of the keys adding to that state.
 *          d. blockage with no matching keys makes the probability to an action to fail
 *          e. reward function is the weight of the graph
 *      3.  after solving the mdp, let the agent play the simulation. it's states are constructed from the graph + actual keys
 */
public class Ex5 {
    public void run(){
        // READ GRAPH
        // SOLVE MDP
        // START AGENT
        // TRAVERSE TO GOAL
    }

    final static int NUM_OF_ITERATIONS = 100;
    Collection<String> states;
    Map<String, Double> values = new HashMap<>();
    Map<String, String> policy = new HashMap<>();


    // read the graph and stuff

    private Collection<Key> keys (Graph graph){
        Collection<Key> keys = new ArrayList<>();
        for (int i = 1; i <= graph.getNumOfKeys(); i++){
            keys.add(new Key(Integer.toString(i), graph, Integer.toString(i)));
        }
        return keys;
    }

    private String keystring(Collection<Key> keys){
        String s = "";
        for (Key k : keys){
            s+= k.id + " ";
        }
        return s.trim();
    }

    private Collection<String> createStates (Graph graph){
        Collection<String> states = new ArrayList<>();
        for (Vertex vertex: graph.getVertices()){
            for (Collection<Key> keys: Queriable.create(Key.class).permutations().execute(keys(graph))){
                String state = vertex.id + "," + keystring(keys);
                states.add(state);
            }
        }
        return states;
    }

    /*

    * */

    public void mdp(){
        Graph graph = null; // TODO: create the graph
        states = createStates(graph); // the states are all vertices * keys

        // init the policy and values
        for (String state: states){
            values.put(state, 0.0); // init values with something
            policy.put(state, state); // init policy with no-op
        }

        // do iterations
        for (int i = 0; i < NUM_OF_ITERATIONS; i++){
            for (String state: states){
                policy.put(state, argmax(state, graph));
                //values.put(state, );
            }
        }
    }

    private String argmax(String state, Graph graph){
        String max_a = "";
        double max_v = Double.MIN_VALUE;

        String currentVertexAtGraphId = state.substring(state.indexOf(','));
        Vertex currentVertexAtGraph = graph.getVertex(currentVertexAtGraphId);
        final String[] currentKeys = state.split(",")[1].split(" ");

        // neighbors in graph will be the actions, the keys added plus the neighbor id will be the states.
        Set<Vertex> neigbores = currentVertexAtGraph.getNeigbores();
        for (Vertex neighbor: neigbores){
            // sum over all probabilties of getting a key and also the chance of getting to the state if blocked
            double sum = 0;

            Collection<String> newKeys = Queriable.create(Key.class).filter(new Predicate<Key>() {
                @Override
                public boolean predict(Key key) {
                    return ! Queriable.create(String.class).execute(currentKeys).contains(key.id);
                }
            }).map(new Selector<Key, String>() {
                @Override
                public String select(Key key) {
                    return key.id;
                }
            }).execute(neighbor.getKeys());

            Collection<Collection<String>> allPermutations = Queriable.create(String.class).permutations().execute(newKeys);
            for(Collection<String> per: allPermutations){
                double probOfGettingToNewState = chanceOfGettingPermutation(per, newKeys, neighbor);
                sum += probOfGettingToNewState;
            }
            sum *= graph.getEdge(currentVertexAtGraph, neighbor).getWeight();

            if (sum > max_v){
                max_v = sum;
                max_a = neighbor.id;
            }
        }

        return max_a;
    }

    double chanceOfGettingPermutation(Collection<String> permutation, Collection<String> total, Vertex neighbor){
        double p = 1.0;
        for(String k: total){
            if (permutation.contains(k)){
                p *= neighbor.pkeys.get(k);
            }
            else {
                p *= (1 -neighbor.pkeys.get(k));
            }
        }
        return p;
    }
}
