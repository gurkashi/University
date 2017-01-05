package graphs;

import com.gurkashi.fj.queries.stracture.Queriable;

import java.util.*;

public class Vertex extends GraphElement {
    final Map<Vertex, Edge> neigbores;
    final Collection<Lock> locks;
    final Collection<Key> keys;
    public final Map<String, Double> pkeys;

    public Vertex(String id, Graph context){
        super(id, context);

        this.neigbores = new HashMap<Vertex, Edge>();
        this.locks = new ArrayList<Lock>();
        this.keys = new ArrayList<Key>();
        this.pkeys = new HashMap<>();
    }

    @Override
    public String toString(){
        return super.toString() + ", locks:{" + getLocksString() + "} keys:{" + getKeysString() + "}";
    }

    private String getLocksString() {
        String result = "";
        for (Lock lock: locks){
            result += lock.id + ";";
        }

        if (locks.size() > 0) {
            return result.substring(0, result.length() - 1);
        }
        else {
            return "";
        }
    }

    private String getKeysString() {
        String result = "";
        if (keys.size() > 0) {
            for (Key key : keys) {
                result += key.id + ";";
            }
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public void addNeigbore(Vertex vertex, Edge edge){
        this.neigbores.put(vertex, edge);
    }

    public Edge getEdge(Vertex neigbore){
        return neigbores.get(neigbore);
    }

    public Set<Vertex> getNeigbores(){
        return (Set<Vertex>) Queriable.create(Vertex.class)
                .sortBy(new Comparator<Vertex>() {
                    @Override
                    public int compare(Vertex o1, Vertex o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                })
                .copyTo(new TreeSet<Vertex>())
                .execute(neigbores.keySet());
    }

    public void addLock(Lock lock){
        this.locks.add(lock);
    }

    public void removeLock(Lock lock){
        this.locks.remove(lock);
    }

    public void removeAllLocks(){
        this.locks.clear();
    }

    public void addKey(Key key){
        this.keys.add(key);
    }

    public void removeKey(Key key){
        this.keys.remove(key);
    }

    public void removeAllKeys(){
        this.keys.clear();
    }

    public Collection<Lock> getLocks() {
        return locks;
    }

    public Collection<Key> getKeys() {
        return keys;
    }
}