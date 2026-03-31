# Boggle Solver - Princeton Algorithms II Week 4

## Overview

This is an implementation of the Boggle game solver for Princeton's Algorithms II course. The solver finds all valid English words on a given Boggle board.

## Project Structure

```
week 4/
├── BoggleSolver.java      # Main solver implementation
├── BoggleBoard.java       # Board representation
├── BoggleGame.java        # Game logic and GUI
├── TrieST26.java          # 26-way Trie for prefix search
├── testing/
│   ├── board-*.txt        # Test board configurations
│   └── dictionary-*.txt   # Word dictionaries
├── Programming Assignment 4_ Boggle.pdf
├── Programming Assignment 4_ FAQ.pdf
└── grader_boggle_93_3.docx
```

## Rules

- **Board**: Standard Boggle (4×4) or Boggle Deluxe (4×7)
- **Word length**: Minimum 3 letters
- **Adjacency**: Each letter must be adjacent to the previous (8 directions, including diagonals)
- **Cube reuse**: Each cube can only be used once per word
- **'Qu' rule**: The pair "Qu" counts as a single letter. If Q appears on the board, it must be used as "Qu"

## Scoring

| Word Length | Points |
|-------------|--------|
| 3–4 letters | 1 |
| 5 letters | 2 |
| 6 letters | 3 |
| 7 letters | 5 |
| 8+ letters | 11 |

## Building & Running

Compile with the algs4 library:
```bash
javac -cp .:algs4.jar *.java
```

Run the game:
```bash
java -cp .:algs4.jar BoggleGame
```

## API

```java
BoggleSolver(String dictionary)           // Initialize with word list
Iterable<String> getAllValidWords(BoggleBoard board)  // Find all valid words
int scoreOf(String word)                   // Score a single word (0 if invalid)
int maxScore(BoggleBoard board)            // Maximum possible score for board
```

## Algorithm

1. **Preprocessing**: Build a Trie from the dictionary for O(1) prefix lookups
2. **Search**: For each board position, perform DFS using the Trie
3. **Pruning**: Stop exploring a path if its prefix is not in the Trie
4. **Validation**: When a prefix forms a complete word (3+ letters), add to results
