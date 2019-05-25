/**
 * @author wen
 * data: 2019/5/24
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import java.util.ArrayList;
import java.util.HashMap;

public class BaseballElimination {
    private final HashMap<String, int[]> teams2wlr;
    private final int[][] g;
    private final int numberOfTeams;
    private final int numberOfIJ;
    private final int numberOfVertexes;
    private String maxWinTeam;

    /**
     * Create a baseball division from given filename in format specified below
     * @param filename filename
     */
    public BaseballElimination(String filename) {
        In in = new In(filename);
        numberOfTeams = in.readInt();
        numberOfIJ = (numberOfTeams * numberOfTeams - numberOfTeams) / 2;
        numberOfVertexes = 2 + numberOfIJ + numberOfTeams;
        teams2wlr = new HashMap<>();
        g = new int[numberOfTeams][numberOfTeams];
        for (int i = 0; i < numberOfTeams; i++) {
            String team = in.readString();
            int[] wlr = {in.readInt(), in.readInt(), in.readInt(), i};
            teams2wlr.put(team, wlr);
            for (int j = 0; j < numberOfTeams; j++) {
                g[i][j] = in.readInt();
            }
        }
        maxWinTeam = getMaxWinTeam();
    }

    /**
     * Returns number of teams.
     * @return number of teams
     */
    public int numberOfTeams() {
        return numberOfTeams;
    }

    /**
     * Returns all teams.
     * @return all teams
     */
    public Iterable<String> teams() {
        return teams2wlr.keySet();
    }

    /**
     * Returns number of wins for given team.
     * @param team team name
     * @return number of wins for given team
     */
    public int wins(String team) {
        validate(team);
        return teams2wlr.get(team)[0];
    }

    /**
     * Returns number of losses for given team.
     * @param team team name
     * @return number of losses for given team
     */
    public int losses(String team) {
        validate(team);
        return teams2wlr.get(team)[1];
    }

    /**
     * Returns number of remaining games for given team.
     * @param team team name
     * @return number of remaining games for given team
     */
    public int remaining(String team) {
        validate(team);
        return teams2wlr.get(team)[2];
    }

    /**
     * Returns the index of the given team.
     * @param team team name
     * @return the index of the given team
     */
    private int index(String team) {
        validate(team);
        return teams2wlr.get(team)[3];
    }

    /**
     * Returns number of remaining games between team1 and team2.
     * @param team1 team1 name
     * @param team2 team2 name
     * @return number of remaining games between team1 and team2
     */
    public int against(String team1, String team2) {
        validate(team1);
        validate(team2);
        return g[index(team1)][index(team2)];
    }

    /**
     * Is given team eliminated?
     * @param team team name
     * @return is given team eliminated?
     */
    public boolean isEliminated(String team) {
        validate(team);
        if (wins(maxWinTeam) > wins(team) + remaining(team)) {
            return true;
        }

        FordFulkerson maxflow = getMaxflow(team);
        int indexOfteam = index(team);
        int currentVertex = 0;
        for (int i = 1; i < numberOfTeams; i++) {
            for (int j = i + 1; j <= numberOfTeams; j++) {
                currentVertex++;
                if (i == indexOfteam + 1 || j == indexOfteam + 1) {
                    continue;
                }
                if (maxflow.inCut(currentVertex)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns subset R of teams that eliminates given team; null if not eliminated.
     * @param team team name
     * @return subset R of teams that eliminates given team; null if not eliminated
     */
    public Iterable<String> certificateOfElimination(String team) {
        validate(team);
        ArrayList<String> subset = new ArrayList<>();
        if (wins(maxWinTeam) > wins(team) + remaining(team)) {
            subset.add(maxWinTeam);
            return subset;
        }

        FordFulkerson maxflow = getMaxflow(team);
        int indexOfteam = index(team);
        int currentVertex = 0;
        int sign = 0;
        for (int i = 1; i < numberOfTeams; i++) {
            for (int j = i + 1; j <= numberOfTeams; j++) {
                currentVertex++;
                if (i == indexOfteam + 1 || j == indexOfteam + 1) {
                    continue;
                }
                if (maxflow.inCut(currentVertex)) {
                    sign += 1;
                    break;
                }
            }
        }

        if (sign == 0) {
            return null;
        }

        for (String currentTeam : teams()) {
            if (currentTeam.equals(team)) {
                continue;
            }
            if (maxflow.inCut(index(currentTeam) + numberOfIJ + 1)) {
                subset.add(currentTeam);
            }
        }
        return subset;
    }

    /**
     * Construct maxflow that do not contain any vertex related to the given team.
     * @param team team name
     * @return maxflow of this problem
     */
    private FordFulkerson getMaxflow(String team) {
        int maxWin = wins(team) + remaining(team);
        int indexOfteam = index(team);
        int currentVertex = 0;
        FlowNetwork network = new FlowNetwork(numberOfVertexes);
        FordFulkerson maxflow;

        for (int i = 1; i < numberOfTeams; i++) {
            for (int j = i + 1; j <= numberOfTeams; j++) {
                currentVertex++;
                if (i == indexOfteam + 1 || j == indexOfteam + 1) {
                    continue;
                }
                network.addEdge(new FlowEdge(0, currentVertex, g[i - 1][j - 1]));
                network.addEdge(new FlowEdge(currentVertex, numberOfIJ + i, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(currentVertex, numberOfIJ + j, Double.POSITIVE_INFINITY));
            }
        }

        for (String currentTeam : teams()) {
            if (currentTeam.equals(team)) {
                continue;
            }
            network.addEdge(new FlowEdge(index(currentTeam) + numberOfIJ + 1, numberOfVertexes - 1, maxWin - wins(currentTeam)));
        }

        maxflow = new FordFulkerson(network, 0, numberOfVertexes - 1);
        return maxflow;
    }

    /**
     * Find the team with the max wins.
     * @return team with the max wins.
     */
    private String getMaxWinTeam() {
        for (String team : teams()) {
            if (maxWinTeam == null) {
                maxWinTeam = team;
            } else if (wins(maxWinTeam) < wins(team)) {
                maxWinTeam = team;
            }
        }
        return maxWinTeam;
    }

    /**
     * Throw exception if input argument is invalid team.
     * @param team team name.
     */
    private void validate(String team) {
        if (!teams2wlr.containsKey(team)) {
            throw new java.lang.IllegalArgumentException();
        }
    }
}
