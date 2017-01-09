package graphs;

/**
 * Created by Gur on 1/6/2017.
 */
public class Evidence {
    public final String vertex;
    public final boolean exists;

    public Evidence(String resource, String vertex, boolean exists) {
        if (resource.toLowerCase().equals("brc")){
            this.vertex = "BRC";
        }
        else {
            this.vertex = vertex + "R" + resource;
        }

        this.exists = exists;
    }
}
