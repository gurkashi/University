package graphs;

public abstract class GraphElement implements Comparable<GraphElement>{
    String id;
    Graph context;
    String alias;

    protected GraphElement(String id, Graph context){
        this(id, context, null);
    }

    protected GraphElement(String id, Graph context, String alias){
        this.id = id;
        this.context = context;
        this.alias = alias;
    }

    @Override
    public boolean equals(Object other){
        return this == other || other instanceof GraphElement && ((GraphElement)other).id.equals(id);
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    public String toString(){
        return alias == null?
                this.getClass().getSimpleName() + ": " + id :
                this.getClass().getSimpleName() + ": " + id + "(" + alias + ")";
    }

    @Override
    public int compareTo(GraphElement other){
        return getId().compareTo(other.getId());
    }

    public String getId(){
        return this.id;
    }

    public Graph getContext(){
        return this.context;
    }
}
