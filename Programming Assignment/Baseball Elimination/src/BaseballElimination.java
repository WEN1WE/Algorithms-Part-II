/**
 * @author wen
 * data: 2019/5/24
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;

import java.util.HashMap;

public class BaseballElimination {
    private HashMap<String, int[]> teams2wlr;
    private HashMap<Integer, String> i2team;
    private int[][] g;
    private int numberOfTeams;

    public BaseballElimination(String filename) {
        In in = new In(filename);
        numberOfTeams = in.readInt();
        teams2wlr = new HashMap<>();
        i2team = new HashMap<>();
        g = new int[numberOfTeams][numberOfTeams];
        for (int i = 0; i < numberOfTeams; i++) {
            String team = in.readString();
            int[] wlr = {in.readInt(), in.readInt(), in.readInt(), i};
            teams2wlr.put(team, wlr);
            i2team.put(i, team);
            for (int j = 0; j < numberOfTeams; j++) {
                g[i][j] = in.readInt();
            }
        }
    }

    public int numberOfTeams() {
        return numberOfTeams;
    }

    public Iterable<String> teams() {
        return teams2wlr.keySet();
    }

    public int wins(String team) {
        return teams2wlr.get(team)[0];
    }

    public int losses(String team) {
        return teams2wlr.get(team)[1];
    }

    public int remaining(String team) {
        return teams2wlr.get(team)[2];
    }

    private int index(String team) {
        return teams2wlr.get(team)[3];
    }

    public boolean isEliminated(String team) {


        return true;
    }

    private FlowNetwork constructGraph(String team) {
        int nTeamV = numberOfTeams - 1;
        int nIJV = (nTeamV * nTeamV - nTeamV) / 2;
        int nV = nTeamV + nIJV + 2;
        int maxWin = wins(team) + remaining(team);
        int indexOfteam = index(team);
        int currentV = 1;
        FlowNetwork network = new FlowNetwork(nV);

        int sign1 = 0;
        for (int i = 1; i < numberOfTeams; i++) {
            if (i == indexOfteam) {
                sign1 = -1;
                continue;
            }
            int sign2 = 0;
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (sign2 == indexOfteam) {
                    sign2 = -1;
                    continue;
                }
                network.addEdge(new FlowEdge(0, currentV, g[i + sign1][j + sign2]));
                network.addEdge(new FlowEdge(currentV, nIJV + i + sign1, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(currentV, nIJV + j + sign2, Double.POSITIVE_INFINITY));
                currentV += 1;
            }
        }

        int sign = 0;
        for (int i = 1; i <= nTeamV ; i++) {
            if (i2team.get(i).equals(team)) {
                sign = -1;
                continue;
            }
            network.addEdge(new FlowEdge(nIJV + i + sign, nV - 1, maxWin - wins(i2team.get(i))));
        }
        return network;
    }

    public static void main(String[] args) {

        FlowNetwork f = new FlowNetwork(12);
        f.addEdge(new FlowEdge(0, 1, 3));
        f.addEdge(new FlowEdge(0, 2, 8));
        f.addEdge(new FlowEdge(0, 3, 7));
        f.addEdge(new FlowEdge(0, 4, 2));
        f.addEdge(new FlowEdge(0, 5, 7));
        f.addEdge(new FlowEdge(0, 6, 0));
        f.addEdge(new FlowEdge(1, 7, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(1, 8, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(2, 7, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(2, 9, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(3, 7, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(3, 10, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(4, 8, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(4, 9, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(5, 8, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(5, 10, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(6, 9, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(6, 10, Double.POSITIVE_INFINITY));
        f.addEdge(new FlowEdge(7, 11, 1));
        f.addEdge(new FlowEdge(8, 11, 5));
        f.addEdge(new FlowEdge(9, 11, 7));
        f.addEdge(new FlowEdge(10, 11, 13));
        System.out.println(f);

        BaseballElimination b = new BaseballElimination(args[0]);
        FlowNetwork f1 = b.constructGraph("Detroit");
        System.out.println(f1);
    }
}
