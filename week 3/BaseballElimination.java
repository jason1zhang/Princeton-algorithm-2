import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.algs4.StdOut;


/******************************************************************************
 *  Name: Rhett Zhang
 *  Date: 2020/01/04
 *
 *  Compilation:  javac-algs4 BaseballElimination.java
 *  Execution:    java-algs4 BaseballElimination
 *  Dependencies: StdOut.java
 *
 *  Given the standings in a sports division at some point during the season,
 *  determine which teams have been mathematically eliminated
 *  from winning their division.
 *
 *  % java-algs4 BaseballElimination
 *
 ******************************************************************************/

public class BaseballElimination {
    private final int N;                          // number of teams
    private final ST<String, Integer> teamsST;    // mapping from team name to its index
    private final String[] teams;                 // mapping from index to team name
    private final int[] wins;                     // current wins of each team
    private final int[] losses;                   // current losses for each team
    private final int[] remaining;                // remaining games for each team
    private final int[][] games;                  // games left to be played

    private final int gV;                         // number of game vertices
    private final int tV;                         // number of team vertices

    // private FlowNetwork G;              // baseball elimination network

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In in = new In(filename);
        this.N = in.readInt();

        this.teamsST = new ST<String, Integer>();
        this.teams = new String[this.N];

        this.wins = new int[this.N];
        this.losses = new int[this.N];
        this.remaining = new int[this.N];

        this.games = new int[this.N][this.N];

        for (int i = 0; i < this.N; i++) {
            // this.teams[i] = in.readString();
            String team = in.readString();
            teamsST.put(team, i);
            teams[i] = team;

            this.wins[i] = in.readInt();
            this.losses[i] = in.readInt();
            this.remaining[i] = in.readInt();

            for (int k = 0; k < this.N; k++)
                this.games[i][k] = in.readInt();
        }


        this.gV = (this.N - 1) * (this.N - 2) / 2;   // number of game vertices
        this.tV = (this.N - 1);                      // number of team vertices
    }

    // build the flow network from the teams except teamX
    // which will be checked for its elimination
    private FlowNetwork buildFlowNetwork(String teamX) {

        int idX = this.teamsST.get(teamX);              // indice for the teamX
        int winX = wins(teamX) + remaining(teamX);      // wins + remainings for teamX

        int teamCap;                                    // capicity for team vertices to vertex t

        int teamID = this.gV + 1;                       // steam vertex
        int gameID = 1;                                 // game vertex
        int s = 0;                                      // vertex s, connecting to all game vertices
        int t = this.gV + this.tV + 1;                  // vertex t, connecting to all team vertices

        // mapping from team index to its vertex number in the flow network
        ST<Integer, Integer> teamsMap = new ST<Integer, Integer>();

        FlowNetwork G = new FlowNetwork(this.gV + this.tV + 2);

        /*
        for (String team : teams) {
            int id = this.teams.get(team);
            if (id == idX)      continue;
            else if (id < idX)  teamsST.put(id, teamID + id);
            else                teamsST.put(id, teamID + id - 1);
        }
        */

        // build the mapping from team index to its vertex number in the flow network
        for (int i = 0; i < this.N; i++) {
            if (i == idX) continue;
            else if (i < idX) teamsMap.put(i, teamID + i);
            else teamsMap.put(i, teamID + i - 1);
        }

        for (int i = 0; i < this.N; i++) {
            if (i == idX) continue;               // skip the row for the teamX

            teamCap = winX - this.wins[i];

            // connect team vertices to vertex t
            G.addEdge(new FlowEdge(teamID, t, teamCap));

            // only need to check the half of the game matrix,
            // this also avoid adding the flow edge twice.
            for (int j = (i + 1); j < this.N; j++) {
                if (j == idX) continue;           // skip the column for the teamX

                // connect game vertex to s
                G.addEdge(new FlowEdge(s, gameID, this.games[i][j]));

                // connect game vertex to 2 team vertices
                G.addEdge(new FlowEdge(gameID, teamsMap.get(i), Double.POSITIVE_INFINITY));
                G.addEdge(new FlowEdge(gameID, teamsMap.get(j), Double.POSITIVE_INFINITY));

                // completed processing current game vertex, and move to the next game vertex
                gameID++;
            }

            // completed processing current team vertex, and move to the next team vertex
            teamID++;
        }

        return G;
    }

    // number of teams
    public int numberOfTeams() {
        return this.N;
    }

    // all teams
    public Iterable<String> teams() {
        Queue<String> queue = new Queue<String>();
        for (int i = 0; i < this.N; i++)
            queue.enqueue(teams[i]);

        return queue;
    }

    // number of wins for given team
    public int wins(String team) {
        if (!this.teamsST.contains(team))
            throw new IllegalArgumentException("wins: team is not valid");

        return this.wins[this.teamsST.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!this.teamsST.contains(team))
            throw new IllegalArgumentException("losses: team is not valid");

        return this.losses[this.teamsST.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!this.teamsST.contains(team))
            throw new IllegalArgumentException("remaining: team is not valid");

        return this.remaining[this.teamsST.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!this.teamsST.contains(team1) || !this.teamsST.contains(team2))
            throw new IllegalArgumentException("against: team is not valid");

        return this.games[this.teamsST.get(team1)][this.teamsST.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!this.teamsST.contains(team))
            throw new IllegalArgumentException("isEliminated: team is not valid");

        int id = this.teamsST.get(team);                // index for the team
        int win = wins(team) + remaining(team);         // wins + remainings for teamX

        // StdOut.printf("%d wins for team %d %s", win, id, team);

        // check if trivially eliminated
        for (int i = 0; i < this.N; i++) {
            if (i == id) continue;

            // StdOut.printf("%d: %d", i, wins[i]);
            if (win < wins[i]) return true;
        }

        FlowNetwork G = buildFlowNetwork(team);
        // int V = G.V();
        // int E = G.E();
        int s = 0;
        int t = G.V() - 1;
        // StdOut.println(G);

        // compute maximum flow and minimum cut
        FordFulkerson maxflow = new FordFulkerson(G, s, t);

        // If some edges pointing from s are not full,
        // then there is no scenario in which team x can win the division
        for (FlowEdge e : G.adj(s))
            if (e.flow() < e.capacity())
                return true;

        return false;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!this.teamsST.contains(team))
            throw new IllegalArgumentException("certificateOfElimination: team is not valid");

        int id = this.teamsST.get(team);                // index for the team
        int win = wins(team) + remaining(team);         // wins + remainings for teamX

        Queue<String> queue = new Queue<String>();      // queue for min cut vertices

        // StdOut.printf("%d wins for team %d %s", win, id, team);

        // check if trivially eliminated
        for (int i = 0; i < this.N; i++) {
            if (i == id) continue;

            // StdOut.printf("%d: %d", i, wins[i]);
            if (win < wins[i]) {
                queue.enqueue(teams[i]);
                return queue;
            }
        }

        FlowNetwork G = buildFlowNetwork(team);
        // int V = G.V();
        // int E = G.E();
        int s = 0;
        int t = G.V() - 1;
        // StdOut.println(G);

        // compute maximum flow and minimum cut
        FordFulkerson maxflow = new FordFulkerson(G, s, t);

        // min-cut
        int index;
        for (int v = this.gV + 1; v < G.V() - 1; v++) {
            if (maxflow.inCut(v)) {
                index = v - (this.gV + 1);
                if (index >= id) index++;

                queue.enqueue(teams[index]);
            }
        }

        if (queue.isEmpty()) return null;
        else return queue;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }

        // testing code
        /*
        for (String team1 : division.teams()) {
            StdOut.printf("%s team has %d wins, %d losses, %d remaing games. \n", team1,
                          division.wins(team1), division.losses(team1), division.remaining(team1));

            for (String team2 : division.teams())
                StdOut.printf("%s against %s : %d \t", team1, team2,
                              division.against(team1, team2));

            StdOut.println();
        }

        boolean willLose = division.isEliminated("Montreal");

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.println(team + " is eliminated");

            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }

        String teamX = "Detroit";
        String teamX = "Toronto";
        */
    }
}
