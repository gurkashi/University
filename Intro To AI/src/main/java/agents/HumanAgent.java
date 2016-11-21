package agents;

import com.gurkashi.fj.lambdas.Predicate;
import com.gurkashi.fj.queries.stracture.Queriable;
import graphs.Graph;
import graphs.Vertex;

import java.util.Scanner;

public class HumanAgent extends AbstractAgent{

    public HumanAgent(String id, Vertex initial, Vertex goal, Graph world) {
        super(id, initial, goal, world);
    }

    @Override
    public Vertex calculateNextMove() {
        System.out.println(getContext());
        System.out.println(this);

        System.out.println("Select next move, illegal id means No-Op");
        for (Vertex vertex: current.getNeigbores()){
            System.out.print(vertex.getId()+" ");
        }
        System.out.println();

        String id = null;
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNext()){
            id = scanner.next();
        }


        final String finalId = id;
        Vertex nextMove = Queriable.create(Vertex.class)
                .filter(new Predicate<Vertex>() {
                    @Override
                    public boolean predict(Vertex vertex) {
                        return vertex.getId().equals(finalId);
                    }
                })
                .singleOrNull()
                .execute(getCurrent().getNeigbores());

        return nextMove;
    }
}