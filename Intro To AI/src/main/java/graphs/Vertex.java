package graphs;

import com.gurkashi.fj.queries.stracture.Queriable;

import java.util.*;

public class Vertex extends GraphElement {
    final Map<Vertex, Edge> neigbores;
    final Collection<Lock> locks;
    final Collection<Key> keys;
    public final Map<String, Double> ptable;
    public final Map<String, Double> pkeys;
    public double p;

    public Vertex(String id, Graph context){
        super(id, context);

        this.neigbores = new HashMap<Vertex, Edge>();
        this.locks = new ArrayList<Lock>();
        this.keys = new ArrayList<Key>();
        this.ptable = new HashMap<>();
        this.pkeys = new HashMap<>();
    }

    public Collection<Vertex> getChildren(){
        return getNeigbores();
    }

    public Collection<Vertex> getParents(){
        Collection<Vertex> result = new ArrayList<>();

        for (Vertex candidate: getContext().getVertices()){
            if (candidate != this && candidate.neigbores.containsKey(this)){
                result.add(candidate);
            }
        }

        return result;
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
                        if (o1.id.equals("BRC")){
                            return -1;
                        }
                        else if (o1.id.equals("BRC")){
                            return 1;
                        }
                        else {
                            return o1.getId().compareTo(o2.getId());
                        }
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

    private static Collection<String> getPermutations(Collection<String> rows, int rest){
        if (rest == 0){
            return rows;
        }

        Collection<String> addition = new ArrayList<>();
        for (String row: rows){
            addition.add(row + "T");
            addition.add(row + "F");
        }
        return getPermutations(addition, rest-1);
    }

    public static Collection<String> getPermutations(int rest){
        Collection<String> rows = new ArrayList<>();
        rows.add("T");
        rows.add("F");
        return getPermutations(rows, rest - 1);
    }
}