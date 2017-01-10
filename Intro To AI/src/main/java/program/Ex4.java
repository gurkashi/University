package program;

import graphs.Evidence;
import graphs.Graph;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Gur on 1/5/2017.
 */
public class Ex4 {
    @Test
    public void run() throws IOException {
        List<Evidence> evidences = new ArrayList<>();

        String FILE = "C:\\Users\\Gur\\Desktop\\ex4.txt";

        Graph g = Graph.fromFile(FILE, 0);
        Graph bayesian = Graph.toBayesian(g);

        String command;

        Scanner scanner = new Scanner(System.in);

        System.out.println("Insert the following:");
        System.out.println("\tgraph = print the graph");
        System.out.println("\te <k> <v> <t/f> = evidence at v with key is true/false");
        System.out.println("\tbrc <t/f> = evidence at v with key is true/false");
        System.out.println("\tq <v> <b> = query vertex for having blockade b given evidences");
        System.out.println("\tclear = clear evidences");
        System.out.println("\tquit = quit");

        do {
            String line = scanner.nextLine();
            command = line;

            if (line.toLowerCase().equals("graph")){
                System.out.println(g.printBayesian(bayesian));
            }
            else if (line.toLowerCase().startsWith("cl")){
                evidences.clear();
                System.out.println("Evidences are cleared.");
            }
            else if (line.toLowerCase().startsWith("e")){
                String[] parts = line.split(" ");
                Evidence e = new Evidence(parts[1], parts[2], parts[3].equals("t")? true :false);
                evidences.add(e);
                System.out.println("K" + parts[1] + " is " + (e.exists? "" : "not ") + "present at V" + parts[2]);
            }
            else if (line.toLowerCase().startsWith("brc")){
                String[] parts = line.split(" ");
                Evidence e = new Evidence("BRC", "BRC", parts[1].equals("t")? true :false);
                evidences.add(e);
                System.out.println("BRC is " + (e.exists? "true" : "false"));
            }
            else if (line.toLowerCase().equals("quit")){
                System.out.println("bye bye");
            }
            else if (line.toLowerCase().startsWith("q")){
                String v = line.split(" ")[1];
                String b = line.split(" ")[2];
                System.out.println("Chance of V" + v + " to have B" + b + " is " + Graph.chanceOf(v, b, evidences, bayesian));
            }
            else {
                System.out.println("bad command.");
            }
        }
        while (!command.toLowerCase().equals("quit"));
    }
}