# Baseball Elimination - Programming Assignment 3

## Overview

This assignment determines which teams have been **mathematically eliminated** from winning their baseball division. It uses **Maximum Flow (Ford-Fulkerson algorithm)** to solve the problem. Score: **100/100**.

## Files

### Source Files

| File | Description |
|------|-------------|
| `BaseballElimination.java` | Main implementation - determines team elimination |
| `FlowEdge.java` | Flow edge data structure |
| `FlowNetwork.java` | Flow network data structure |
| `FordFulkerson.java` | Ford-Fulkerson max flow algorithm |

### Test Data (`testing/`)

| File | Teams | Description |
|------|-------|-------------|
| `teams4.txt`, `teams4a.txt`, `teams4b.txt` | 4 | Small test cases |
| `teams5.txt`, `teams5a/b/c.txt` | 5 | Medium test cases |
| `teams7.txt` | 7 | 7-team division |
| `teams8.txt` | 8 | 8-team division |
| `teams10.txt`, `teams12.txt` | 10-12 | Larger divisions |
| `teams24.txt` through `teams60.txt` | 24-60 | Large divisions |

### Documentation

| File | Description |
|------|-------------|
| `Programming Assignment 3_ Baseball Elimination.pdf` | Assignment specification |
| `Programming Assignment 3_ FAQ.pdf` | Frequently asked questions |

## Building and Running

Compile:
```bash
javac BaseballElimination.java
```

Run:
```bash
java BaseballElimination testing/teams4.txt
```

## Input Format

```
N                           // number of teams
TeamName                    // team name
wins losses remaining g[0][1] g[0][2] ... g[0][N-1]  // wins, losses, remaining games, games vs each team
...
```

Example (`teams4.txt`):
```
4
Atlanta                100  28  32  0  3  1  2
Philadelphia            81  40  27  3  0  1  2
NewYork                 80  41  27  1  1  0  2
Montreal                78  43  27  2  2  2  0
```

## Two Types of Elimination

### 1. Trivial Elimination

A team is **trivially eliminated** if some other team has already won more games than the maximum games the team can still win:

```
maxWins(X) = wins(X) + remaining(X)
if maxWins(X) < wins(Y) for any Y → X is eliminated
```

### 2. Non-Trivial Elimination

A team is eliminated if there's no valid schedule where all remaining games can be played such that the team wins the division. This is modeled as a maximum flow problem.

## Algorithm: Maximum Flow

### Flow Network Construction

For team X being checked, build a flow network:

```
                capacity = INF
        g1 ──────────────→ TeamA ───────→ t
       /                      ↑
      / capacity = games      │ capacity =
     s                        | maxWins(X) - wins(A)
      \                       ↓
       \              g2 ──────────────→ TeamB
        \             /                ...
         \           /
          g3 ────────┘
```

**Vertices:**
- `s` = source (game vertices)
- `g1, g2, ...` = game vertices (one per remaining game)
- `TeamA, TeamB, ...` = team vertices (one per other team)
- `t` = sink

**Edge Capacities:**
| Edge | Capacity |
|------|----------|
| `s → game(i,j)` | Number of games left between team i and j |
| `game(i,j) → team i` | INF |
| `game(i,j) → team j` | INF |
| `team i → t` | maxWins(X) - wins(i) |

### Check Elimination

1. Build flow network for team X
2. Compute max flow using Ford-Fulkerson
3. If max flow < total games from s → team X is eliminated
4. The min-cut gives the **certificate of elimination**

## Implementation Details

### BaseballElimination.java

```java
public class BaseballElimination {
    // Data structures
    private ST<String, Integer> teamsST;    // team name → index
    private int[] wins, losses, remaining;   // team stats
    private int[][] games;                  // remaining games matrix

    public boolean isEliminated(String team);
    public Iterable<String> certificateOfElimination(String team);
    private FlowNetwork buildFlowNetwork(String teamX);
}
```

### FlowNetwork.java

Flow network with V vertices and adjustable edges. Used by Ford-Fulkerson to compute maximum flow.

### FordFulkerson.java

Implements the Ford-Fulkerson algorithm with:
- BFS for augmenting path finding (ensures termination)
- Min-cut computation for certificate extraction

## Example Output

```
java BaseballElimination testing/teams4.txt

Atlanta is not eliminated
Philadelphia is not eliminated
NewYork is not eliminated
Montreal is eliminated by the subset R = { Atlanta NewYork }
```

**Interpretation:** Montreal is eliminated because even if Montreal wins all its remaining games, Atlanta and New York winning their head-to-head games would give them more wins than Montreal can achieve.

## Score

**100/100** - Perfect score
