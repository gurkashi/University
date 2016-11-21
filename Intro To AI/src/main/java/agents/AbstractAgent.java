package agents;

import graphs.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAgent extends GraphElement {
    Vertex goal;
    Vertex current;
    double score;
    private List<Key> keys;

    protected AbstractAgent(String id, Vertex initial, Vertex goal, Graph world){
        super(id, world);
        this.current = initial;
        this.goal = goal;
        this.score = 0;
        this.keys = new ArrayList<>();

        for (Key key: initial.getKeys()){
            keys.add(key);
        }
        initial.getKeys().clear();
    }

    public void noOp(){
        score += 1;
    }

    public void traverse(Vertex next){
        Edge edge = getContext().getEdge(current, next);

        if (edge == null) {
            return;
        }

        score += edge.getWeight();

        if (tryUnlock(next)){
            keys.addAll(next.getKeys());
            next.getKeys().clear();
            current = next;
        }
    }

    private boolean tryUnlock(Vertex next) {
        if (keys.containsAll(next.getLocks())) {

            keys.removeAll(next.getLocks());
            next.getLocks().clear();
            return true;
        }

        return false;
    }

    public Vertex getCurrent(){
        return current;
    }

    public void doMove(){
        Vertex nextMove = calculateNextMove();

        if(nextMove == null){
            noOp();
        }
        else {
            traverse(nextMove);
        }
    }

    public Vertex getGoal() {
        return goal;
    }

    @Override
    public String toString(){
        String keysString = "";
        for (Key key: keys){
            keysString += key.getId()+ ",";
        }
        if (keys.size() > 0) {
            keysString = keysString.substring(0, keysString.length() - 1);
        }

        return String.format("%s score:{%.2f} keys:{%s} current:{%s} goal:{%s}", super.toString(), score, keysString, current, goal);
    }

    public abstract Vertex calculateNextMove();

    public List<Key> getKeys() {
        return keys;
    }

    public int getScore() {
        return (int) score;
    }
}